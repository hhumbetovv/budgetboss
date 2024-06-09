package com.theternal.common.extensions

import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.getDrawable(id: Int): Drawable? {
    return ContextCompat.getDrawable(requireContext(), id)
}

fun Fragment.getColor(id: Int): Int {
    return ContextCompat.getColor(requireContext(), id)
}

fun Fragment.showToast(text: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), text, length).show()
}