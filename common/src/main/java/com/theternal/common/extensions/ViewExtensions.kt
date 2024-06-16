package com.theternal.common.extensions

import android.animation.LayoutTransition
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

fun View.hide(withLayoutAnimation: Boolean = true) {
    if(visibility == View.GONE) {
        if(withLayoutAnimation) setLayoutAnimation()
        return
    }
    animate()
        .alpha(0f)
        .setDuration(300)
        .withEndAction {
            visibility = View.GONE
            if(withLayoutAnimation) setLayoutAnimation()
        }
}

fun View.fadeOut(withLayoutAnimation: Boolean = true) {
    if(visibility == View.INVISIBLE) {
        if(withLayoutAnimation) setLayoutAnimation()
        return
    }
    animate()
        .alpha(0f)
        .setDuration(300)
        .withEndAction {
            visibility = View.INVISIBLE
            if(withLayoutAnimation) setLayoutAnimation()
        }
}

fun View.show(withLayoutAnimation: Boolean = true) {
    if(visibility == View.VISIBLE) {
        if(withLayoutAnimation) setLayoutAnimation()
        return
    }
    alpha = 0f
    visibility = View.VISIBLE
    animate()
        .alpha(1f)
        .setDuration(300)
        .withEndAction { if(withLayoutAnimation) setLayoutAnimation() }
}

private fun View.setLayoutAnimation() {
    (parent as? ViewGroup)?.apply {
        layoutTransition = LayoutTransition().apply {
            enableTransitionType(LayoutTransition.CHANGING)
        }
    }
}

fun View.getColor(id: Int): Int {
    return ContextCompat.getColor(context, id)
}
