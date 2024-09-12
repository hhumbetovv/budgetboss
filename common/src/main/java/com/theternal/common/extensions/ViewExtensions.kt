package com.theternal.common.extensions

import android.view.View
import androidx.core.content.ContextCompat

fun View.gone() { visibility = View.GONE }

fun View.hide() { visibility = View.INVISIBLE }

fun View.show() { visibility = View.VISIBLE }

fun View.getColor(id: Int): Int {
    return ContextCompat.getColor(context, id)
}
