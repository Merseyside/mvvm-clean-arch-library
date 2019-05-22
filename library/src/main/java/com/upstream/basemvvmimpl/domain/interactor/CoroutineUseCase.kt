package com.upstream.basemvvmimpl.domain.interactor

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


abstract class CoroutineUseCase<T, Params> : CoroutineScope by CoroutineScope(Dispatchers.Main) {

    var backgroundContext: CoroutineContext = Dispatchers.Default

    protected abstract suspend fun executeOnBackground(params: Params?): T

    fun execute(onComplete: (T) -> Unit, onError: (Throwable) -> Unit, params: Params? = null) {
        //parentJob.cancel()
        //parentJob = Job()
        launch {
            try {
                val result = withContext(backgroundContext) {
                    executeOnBackground(params)
                }
                onComplete.invoke(result)
            } catch (e: CancellationException) {
                //Log.d("UseCase", "canceled by user")
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    protected suspend fun <X> background(context: CoroutineContext = backgroundContext, block: suspend () -> X): Deferred<X> {
        return async(context) {
            block.invoke()
        }
    }

    fun unsubscribe() {
        cancel()
    }

}