package com.merseyside.mvvmcleanarch.presentation.view

import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.BindingAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.merseyside.mvvmcleanarch.utils.ext.getColorFromAttr
import com.merseyside.mvvmcleanarch.utils.ext.isNotNullAndEmpty
import com.merseyside.mvvmcleanarch.utils.ext.setTextWithCursor

@BindingAdapter("app:isVisibleOrGone")
fun isVisibleOrGone(view: View, isVisible: Boolean) {
    when(isVisible) {
        true -> view.visibility = View.VISIBLE
        false -> view.visibility = View.GONE
    }
}

@BindingAdapter("app:isVisibleOrGone")
fun isVisibleOrGone(view: View, obj: Any?) {
    view.visibility = if (obj != null) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("app:isVisibleOrGone")
fun isVisibleOrGone(view: View, collection: Collection<*>?) {
    view.visibility = if (collection.isNotNullAndEmpty()) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter("app:isVisible")
fun isVisible(view: View, isVisible: Boolean) {
    when(isVisible) {
        true -> view.visibility = View.VISIBLE
        false -> view.visibility = View.INVISIBLE
    }
}

@BindingAdapter("app:isVisible")
fun isVisible(view: View, obj: Any?) {
    view.visibility = if (obj != null) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

@BindingAdapter("app:isVisible")
fun isVisible(view: View, collection: Collection<*>?) {
    view.visibility = if (collection.isNotNullAndEmpty()) {
        View.VISIBLE
    } else {
        View.INVISIBLE
    }
}

@BindingAdapter("bind:backgroundDrawable")
fun setDrawableBackground(view: View, @DrawableRes res: Int?) {
    if (res != null) {
        view.background = ContextCompat.getDrawable(view.context, res)
    }
}

@BindingAdapter("bind:text")
fun setText(editText: EditText, text: String?) {
    editText.setTextWithCursor(text)
}

@BindingAdapter(value = ["textAttrChanged"]) // AttrChanged required postfix
fun setTextListener(editText: EditText, listener: InverseBindingListener?) {
    editText.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listener?.onChange()
        }
    })
}

@InverseBindingAdapter(attribute = "bind:text")
fun getText(editText: EditText): String? {
    return editText.text.toString()
}

@BindingAdapter("bind:vectorDrawable")
fun loadVectorDrawable(iv: ImageView, @DrawableRes resId: Int?) {
    if (resId != null) {
        iv.setImageResource(resId)
    }
}

@BindingAdapter("app:attrCardBackgroundColor")
fun setCardViewBackgroundColor(cardView: CardView, @AttrRes attrId: Int?) {
    if (attrId != null) {
        cardView.setCardBackgroundColor(cardView.context.getColorFromAttr(attrId))
    }
}

@BindingAdapter("app:attrBackgroundColor")
fun setViewGroupBackgroundColor(viewGroup: ViewGroup, @AttrRes attrId: Int?) {
    if (attrId != null) {
        viewGroup.setBackgroundColor(viewGroup.context.getColorFromAttr(attrId))
    }
}

@BindingAdapter("app:attrBackgroundColor")
fun setViewBackgroundColor(view: View, @AttrRes attrId: Int?) {
    if (attrId != null) {
        view.setBackgroundColor(view.context.getColorFromAttr(attrId))
    }
}

@BindingAdapter("app:attrTextColor")
fun setCustomTextColor(view: TextView, @AttrRes attrId: Int?) {
    if (attrId != null) {
        view.setTextColor(view.context.getColorFromAttr(attrId))
    }
}

@BindingAdapter("app:spanned")
fun setSpannedText(view: TextView, charSequence: CharSequence?) {
    if (charSequence != null) {
        view.text = charSequence
    }
}

@BindingAdapter("app:count")
fun setCount(view: TextView, collection: Collection<*>?) {
    if (collection != null) {
        view.text = collection.size.toString()
    } else {
        view.text = "0"
    }
}

