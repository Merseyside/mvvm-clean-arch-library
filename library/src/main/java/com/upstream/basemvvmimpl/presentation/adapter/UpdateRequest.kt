package com.upstream.basemvvmimpl.presentation.adapter

import java.lang.IllegalArgumentException

class UpdateRequest<T> {

    var isAddNew = true
    private set

    var isDeleteOld = false
    private set

    var list: List<T>? = null
    private set

    class Builder<T> {

        private val request: UpdateRequest<T> = UpdateRequest()

        fun isAddNew(bool: Boolean): Builder<T> {
            request.isAddNew = bool

            return this
        }

        fun isDeleteOld(bool: Boolean): Builder<T> {
            request.isDeleteOld = bool

            return this
        }

        fun setList(list: List<T>): Builder<T> {
            request.list = list

            return this
        }

        fun build(): UpdateRequest<T> {
            if (request.list != null) {

                return request
            }

            throw IllegalArgumentException("List can not be null")
        }
    }
}