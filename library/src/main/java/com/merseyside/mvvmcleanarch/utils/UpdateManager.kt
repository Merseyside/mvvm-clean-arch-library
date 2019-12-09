package com.merseyside.mvvmcleanarch.utils

import android.app.Activity
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import java.lang.IllegalStateException

class UpdateManager(private val activity: Activity) {

    interface OnAppUpdateListener {
        fun updateAvailable()
        fun updateDownloaded()
    }

    interface OnFlexibleUpdateStateListener {
        fun onDownloaded()
        fun onFailed()
        fun onCanceled()
        fun onInstalled()
    }

    private var onAppUpdateListener: OnAppUpdateListener? = null

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)
    private var appUpdateInfo: AppUpdateInfo? = null

    private var requestCode: Int? = null

    fun setOnAppUpdateListener(onAppUpdateListener: OnAppUpdateListener?) {
        if (onAppUpdateListener != null) {
            this.onAppUpdateListener = onAppUpdateListener

            val appUpdateInfoTask = appUpdateManager.appUpdateInfo

            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                Log.d(TAG, "${appUpdateInfo.updateAvailability()}")

                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    if (requestCode != null) {
                        startImmediateUpdate(requestCode!!)
                    }
                } else {
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        onAppUpdateListener.updateDownloaded()
                    } else {
                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                        ) {
                            this.onAppUpdateListener?.updateAvailable()
                        }
                    }
                }
            }
        } else {
            this.onAppUpdateListener = null
        }
    }

    fun startImmediateUpdate(requestCode: Int) {
        this.requestCode = requestCode

        if (appUpdateInfo != null) {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.IMMEDIATE,
                activity,
                requestCode
            )
        } else {
            throw IllegalStateException("App is not available for update")
        }
    }

    fun startFlexibleUpdate(requestCode: Int, onFlexibleUpdateStateListener: OnFlexibleUpdateStateListener) {
        this.requestCode = requestCode

        if (appUpdateInfo != null) {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                AppUpdateType.FLEXIBLE,
                activity,
                requestCode
            )

            appUpdateManager.registerListener { state ->
                    when(state.installStatus()) {
                        InstallStatus.DOWNLOADED -> {
                            onFlexibleUpdateStateListener.onDownloaded()
                        }

                        InstallStatus.FAILED -> {
                            onFlexibleUpdateStateListener.onFailed()
                        }

                        InstallStatus.CANCELED -> {
                            onFlexibleUpdateStateListener.onCanceled()
                        }

                        InstallStatus.INSTALLED -> {
                            onFlexibleUpdateStateListener.onInstalled()
                        }

                        else -> {}
                    }
                }
        } else {
            throw IllegalStateException("App is not available for update")
        }
    }

    fun installDownloadedUpdate() {
        appUpdateManager.completeUpdate()
    }

    companion object {
        private const val TAG = "UpdateManager"
    }
}