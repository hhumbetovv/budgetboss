package com.theternal.common.extensions

import android.view.View
import androidx.core.content.ContextCompat

fun View.hide() {
    if(visibility == View.GONE) return
    animate()
        .alpha(0f)
        .setDuration(300)
        .withEndAction{
            visibility = View.GONE
        }
}

fun View.show() {
    if(visibility == View.VISIBLE) return
    alpha = 0f
    visibility = View.VISIBLE
    animate()
        .alpha(1f)
        .setDuration(300)
        .setListener(null)
}

fun View.getColor(id: Int): Int {
    return ContextCompat.getColor(context, id)
}
