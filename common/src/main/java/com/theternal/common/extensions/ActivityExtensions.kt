package com.theternal.common.extensions

import android.app.Activity
import android.util.TypedValue

fun Activity.toPx(int: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        int,
        resources.displayMetrics
    )
}