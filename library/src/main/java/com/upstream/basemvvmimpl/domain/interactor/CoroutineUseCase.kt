package com.upstream.basemvvmimpl.domain.interactor

import android.content.Context
import android.util.Log
import com.upstream.basemvvmimpl.domain.executor.PostExecutionThread
import com.upstream.basemvvmimpl.domain.executor.ThreadExecutor
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


abstract class CoroutineUseCase<T>(private val threadExecutor: ThreadExecutor,
                                   private val postExecutionThread: PostExecutionThread,
                                   context: Context) {

    protected var parentJob: Job = Job()
    //var backgroundContext: CoroutineContext = IO
    var backgroundContext: CoroutineContext = Dispatchers.IO
    var foregroundContext: CoroutineContext = Dispatchers.Main

    private val uiScope = CoroutineScope(Dispatchers.Main + parentJob)

    protected abstract suspend fun executeOnBackground(): T

    fun execute(onComplete: (T) -> Unit, onError: (Throwable) -> Unit) {
        parentJob.cancel()
        parentJob = Job()
        uiScope.launch(foregroundContext) {
            try {
                val result = withContext(backgroundContext) {
                    executeOnBackground()
                }

                onComplete.invoke(result)
            } catch (e: CancellationException) {
                Log.d("UseCase", "canceled by user")
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    protected suspend fun <X> background(context: CoroutineContext = backgroundContext, block: suspend () -> X): Deferred<X> {
        return uiScope.async(context) {
            block.invoke()
        }
    }

    fun unsubscribe() {
        parentJob.cancel()
    }

}