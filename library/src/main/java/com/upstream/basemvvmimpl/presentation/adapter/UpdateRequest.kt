package com.upstream.basemvvmimpl.presentation.adapter

import java.lang.IllegalArgumentException

class UpdateRequest<T> {

    var isAddNew = true
    private set

    var isDeleteOld = false
    private set

    var list: List<T>? = null
    private set

    inner class Builder {

        fun isAddNew(bool: Boolean): Builder {
            isAddNew = bool

            return this
        }

        fun isDeleteOld(bool: Boolean): Builder {
            isDeleteOld = bool

            return this
        }

        fun setList(list: List<T>): Builder {
            this@UpdateRequest.list = list

            return this
        }

        fun build(): UpdateRequest<T> {
            if (list != null) {

                return this@UpdateRequest
            }

            throw IllegalArgumentException("List can not be null")
        }
    }
}