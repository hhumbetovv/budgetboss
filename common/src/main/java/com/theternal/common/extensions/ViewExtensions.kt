package com.theternal.common.extensions

import android.animation.LayoutTransition
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

fun View.hide() {
    if(visibility == View.GONE) return
    animate()
        .alpha(0f)
        .setDuration(300)
        .withEndAction{
            visibility = View.GONE
            (parent as? ViewGroup)?.apply {
                layoutTransition = LayoutTransition().apply {
                    enableTransitionType(LayoutTransition.CHANGING)
                }
            }
        }
}

fun View.fadeOut() {
    if(visibility == View.INVISIBLE) return
    animate()
        .alpha(0f)
        .setDuration(300)
        .withEndAction{
            visibility = View.INVISIBLE
            (parent as? ViewGroup)?.apply {
                layoutTransition = LayoutTransition().apply {
                    enableTransitionType(LayoutTransition.CHANGING)
                }
            }
        }
}

fun View.show() {
    if(visibility == View.VISIBLE) return
    alpha = 0f
    visibility = View.VISIBLE
    animate()
        .alpha(1f)
        .setDuration(300)
        .setListener(null).withEndAction {
            (parent as? ViewGroup)?.apply {
                layoutTransition = LayoutTransition().apply {
                    enableTransitionType(LayoutTransition.CHANGING)
                }
            }
        }
}

fun View.getColor(id: Int): Int {
    return ContextCompat.getColor(context, id)
}
