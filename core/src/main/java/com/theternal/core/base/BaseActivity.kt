package com.theternal.core.base

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

typealias ActivityInflater<T> = (LayoutInflater) -> T

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    //! Properties
    abstract val inflateBinding: ActivityInflater<VB>

    private var _binding: VB? = null

    val binding: VB
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = inflateBinding.invoke(LayoutInflater.from(this))
        setContentView(_binding!!.root)
        initViews()
    }

    //! Initializers
    open val initViews: Initializer<VB> = {}

    private fun initViews() {
        if(_binding != null) initViews(_binding!!)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    /**
     * Overrides the dispatchTouchEvent to clear focus from an EditText and hide the keyboard
     * when a touch event occurs outside the EditText.
     *
     * @param event The MotionEvent representing the touch event.
     * @return Boolean indicating whether the event was handled.
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}