package com.theternal.common.extensions

import android.animation.LayoutTransition
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

fun View.hide() {
    if(visibility == View.GONE) {
        setLayoutAnimation()
        return
    }
    animate()
        .alpha(0f)
        .setDuration(300)
        .withEndAction {
            visibility = View.GONE
            setLayoutAnimation()
        }
}

fun View.fadeOut() {
    if(visibility == View.INVISIBLE) {
        setLayoutAnimation()
        return
    }
    animate()
        .alpha(0f)
        .setDuration(300)
        .withEndAction {
            visibility = View.INVISIBLE
            setLayoutAnimation()
        }
}

fun View.show() {
    if(visibility == View.VISIBLE) {
        setLayoutAnimation()
        return
    }
    alpha = 0f
    visibility = View.VISIBLE
    animate()
        .alpha(1f)
        .setDuration(300)
        .withEndAction { setLayoutAnimation() }
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
