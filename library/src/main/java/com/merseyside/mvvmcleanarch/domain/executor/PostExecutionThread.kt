package com.merseyside.mvvmcleanarch.domain.executor

import io.reactivex.Scheduler

interface PostExecutionThread {
    val scheduler: Scheduler?
}