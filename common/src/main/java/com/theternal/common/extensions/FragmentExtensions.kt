
package com.theternal.common.extensions

import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.theternal.common.R

typealias Strings = R.string
typealias Colors = R.color

fun Fragment.getDrawable(id: Int): Drawable? {
    return ContextCompat.getDrawable(requireContext(), id)
}

fun Fragment.getColor(id: Int): Int {
    return ContextCompat.getColor(requireContext(), id)
}

fun Fragment.showToast(text: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), text, length).show()
}

fun Fragment.toPx(int: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        int,
        requireContext().resources.displayMetrics
    )
}