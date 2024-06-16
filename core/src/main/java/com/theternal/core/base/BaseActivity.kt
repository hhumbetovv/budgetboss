package com.theternal.core.base

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewbinding.ViewBinding
import com.theternal.common.constants.PrefKeys
import com.theternal.common.extensions.getSavedBoolean
import com.theternal.common.extensions.getSavedString
import java.util.Locale

typealias ActivityInflater<T> = (LayoutInflater) -> T

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    companion object {
        const val SAVED_STATE = "saved_state"
    }

    //! Properties
    abstract val inflateBinding: ActivityInflater<VB>

    private var _binding: VB? = null

    private var isRecreating = false

    val binding: VB
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {

        initTheme()
        initLocale()

        //! Restore State
        val bundle = intent.getBundleExtra(SAVED_STATE)
        super.onCreate(bundle ?: savedInstanceState)

        //! Set Binding
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

    private fun initTheme() {
        if(getSavedBoolean(PrefKeys.IS_THEME_SAVED, false)) {
            AppCompatDelegate.setDefaultNightMode(
                if(getSavedBoolean(PrefKeys.IS_DARK_MODE, true)) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }
    }

    @Suppress("DEPRECATION")
    private fun initLocale() {
        val code = getSavedString(PrefKeys.LOCALE)

        if(code != null) {
            val locale = Locale(code)
            Locale.setDefault(locale)

            val resources = resources

            val config = resources.configuration
            config.setLocale(locale)
            baseContext.resources.updateConfiguration(
                config, baseContext.resources.displayMetrics
            )
        }
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