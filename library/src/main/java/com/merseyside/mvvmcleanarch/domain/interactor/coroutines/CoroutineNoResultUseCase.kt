package com.merseyside.mvvmcleanarch.domain.interactor.coroutines

import com.merseyside.mvvmcleanarch.utils.Logger
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class CoroutineNoResultUseCase<Params> : BaseCoroutineUseCase<Unit, Params>() {

    fun execute(
        onPreExecute: () -> Unit = {},
        onComplete: () -> Unit = {},
        onError: (Throwable) -> Unit = {},
        onPostExecute: () -> Unit = {},
        params: Params? = null
    ) {
        if (job != null) {
            job!!.cancel()
        }

        launch {
            onPreExecute()

            val deferred = doWorkAsync(params)

            try {
                deferred.await()
                onComplete.invoke()
            } catch (throwable: CancellationException) {
                Logger.log(this, "The coroutine had canceled")
            } catch (throwable: Throwable) {
                Logger.logErr(throwable)
                onError(throwable)
            }

            onPostExecute()
        }
    }
}