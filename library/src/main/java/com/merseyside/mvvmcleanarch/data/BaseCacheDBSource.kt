package com.merseyside.mvvmcleanarch.data

import android.content.Context
import androidx.room.RoomDatabase
import java.io.File

abstract class BaseCacheDBSource<M: RoomDatabase>(context: Context, db: M): BaseDBSource<M>(db) {

    protected val cacheDir: File = context.cacheDir
}