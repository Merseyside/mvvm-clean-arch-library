package com.merseyside.mvvmcleanarch.utils.billing

import android.app.Activity
import android.content.Context
import androidx.annotation.RawRes
import com.android.billingclient.api.*
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.merseyside.mvvmcleanarch.utils.Logger
import com.merseyside.mvvmcleanarch.utils.billing.Security.verifyPurchase
import com.merseyside.mvvmcleanarch.utils.ext.log
import com.merseyside.mvvmcleanarch.utils.getApplicationName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class BillingManager(
    private val context: Context,
    private val base64Key: String,
    @RawRes private val credentialsId: Int? = null
): PurchasesUpdatedListener, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    interface BillingConnectionListener {

        fun onConnected()

        fun onError(code: Int)

        fun onDisconnected()
    }

    interface OnPurchaseListener {

        fun onPurchase(purchase: Purchase)

        fun onError(result: BillingResult)
    }

    private var onPurchaseListener: OnPurchaseListener? = null

    fun setOnPurchaseListener(listener: OnPurchaseListener) {
        this.onPurchaseListener = listener
    }

    private var billingClient: BillingClient? = null

    private suspend fun startConnection(): BillingClient? {

        return suspendCoroutine { cont ->
            if (billingClient == null) {
                val client = BillingClient
                    .newBuilder(context)
                    .enablePendingPurchases()
                    .setListener(this@BillingManager)
                    .build()

                client.startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

                            billingClient = client
                            cont.resume(billingClient)

                        } else {
                            throw IllegalArgumentException("Result code: ${billingResult.responseCode}")
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        billingClient = null
                    }
                })
            } else {
                cont.resume(billingClient)
            }
        }
    }

    override fun onPurchasesUpdated(result: BillingResult?, purchases: MutableList<Purchase>?) {
        if (result != null) {
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                if (purchases != null) {
                    purchases.forEach { purchase ->
                        if (verifyValidSignature(
                                purchase.originalJson,
                                purchase.signature
                            )
                        ) {
                            onPurchaseListener?.onPurchase(purchase)
                        } else {
                            onPurchaseListener?.onError(result)
                        }
                    }
                } else {
                    onPurchaseListener?.onError(result)
                }
            } else {
                onPurchaseListener?.onError(result)
            }
        } else {
            throw IllegalArgumentException("Result is null")
        }
    }

    private fun verifyValidSignature(
        signedData: String,
        signature: String
    ): Boolean {
        return try {
            verifyPurchase(
                base64Key,
                signedData,
                signature
            )
        } catch (e: IOException) {
            Logger.logErr(this, "Got an exception trying to validate a purchase: ${e.message}")
            false
        }
    }

    /**
     * Returns null if something went wrong
     */
    suspend fun getSkuDetails(skuList: List<String>? = null): List<SkuDetails>? {

        val billingClient = startConnection().log()

        if (billingClient != null) {
            val skuDetailsParams = SkuDetailsParams.newBuilder().setSkusList(skuList)
                .setType(BillingClient.SkuType.SUBS).build()

            return suspendCoroutine { cont ->
                billingClient.querySkuDetailsAsync(
                    skuDetailsParams
                ) { result, responseSkuList ->
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        cont.resume(responseSkuList)
                    } else {
                        cont.resume(null)
                    }
                }
            }
        } else {
            throw IllegalStateException("Something went wrong")
        }
    }

    fun startSubscription(activity: Activity, skuDetails: SkuDetails): BillingResult {
        if (billingClient != null) {
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build()

            return billingClient!!.launchBillingFlow(activity, flowParams)
        } else {
            throw IllegalStateException("BillingClient is null")
        }
    }

    suspend fun queryAllSubscriptionsAsync(vararg skus: String): List<Subscription>? {

        if (credentialsId != null) {

            val billingClient = startConnection()

            if (billingClient != null) {
                val historyList = suspendCoroutine<List<PurchaseHistoryRecord>?> { cont ->
                    billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS,
                        PurchaseHistoryResponseListener { result, historyList ->
                            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                                cont.resume(historyList)
                            } else {
                                cont.resume(null)
                            }
                        })
                }.log()

                return historyList
                    ?.filter { skus.contains(it.sku) }
                    ?.map { getSubscriptionState(it.sku, it.purchaseToken) }
            } else {
                throw IllegalStateException()
            }
        } else throw IllegalArgumentException("Please, set credentials id by constructor")
    }

    suspend fun queryActiveSubscriptionsAsync(vararg skus: String): List<Subscription>? {
        return queryAllSubscriptionsAsync(*skus)?.filterIsInstance<Subscription.ActiveSubscription>()
    }

    private suspend fun getSubscriptionState(sku: String, token: String): Subscription {

        val httpTransport = NetHttpTransport()
        val jacksonJsonFactory = JacksonFactory.getDefaultInstance()

        val packageName = context.packageName

        val publisher = AndroidPublisher.Builder(httpTransport, jacksonJsonFactory, HttpCredentialsAdapter(getGoogleCredentials()))
            .setApplicationName(getApplicationName(context))
            .build()

        val request = publisher.Purchases().subscriptions().get(packageName, sku, token).also { Logger.log("", it.keys) }

        return withContext(Dispatchers.IO) {
            Subscription.Builder(request.execute().log()).setSku(sku).build()
        }
    }

    suspend fun startTestPurchase(activity: Activity) {
        val skuList = getSkuDetails(listOf(TEST_SUBSCRIPTION_ID)).log()

//        if (skuList.isNotNullAndEmpty()) {
//            startSubscription(activity, skuList!!.first()) {
//                Logger.log(this, "response code = $it")
//            }
//        } else {
//            throw IllegalArgumentException("No test products")
//        }
    }

    private fun getGoogleCredentials(): GoogleCredentials {
        context.resources.openRawResource(credentialsId!!).use { `is` ->
            return GoogleCredentials.fromStream(`is`)
                .createScoped(PUBLISHER_SCOPE)
        }
    }


    companion object {
        private const val TEST_SUBSCRIPTION_ID = "android.test.purchased"

        private const val PUBLISHER_SCOPE = "https://www.googleapis.com/auth/androidpublisher"
    }
}