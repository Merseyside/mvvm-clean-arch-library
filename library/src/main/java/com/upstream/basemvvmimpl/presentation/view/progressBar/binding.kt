package com.upstream.basemvvmimpl.presentation.view.progressBar

import androidx.databinding.BindingAdapter

@BindingAdapter("bind:visibility")
fun setProgressBarVisibility(progress: TextProgressBar, visibility: Int) {
    progress.visibility = visibility
}

@BindingAdapter("bind:text")
fun setText(progress: TextProgressBar, text: String?) {
    progress.setText(text)
}