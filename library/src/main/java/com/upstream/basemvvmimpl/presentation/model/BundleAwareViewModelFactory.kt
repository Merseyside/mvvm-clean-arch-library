package com.upstream.basemvvmimpl.presentation.model

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BundleAwareViewModelFactory<T: ParcelableViewModel>(private val bundle: Bundle?): BaseViewModelFactory<T>() {

    override fun <M: ViewModel?> create(modelClass: Class<M>): M {

        val viewModel = getViewModel()

        if (bundle != null) {
            viewModel.readFrom(bundle)
        }

        return viewModel as M
    }


}