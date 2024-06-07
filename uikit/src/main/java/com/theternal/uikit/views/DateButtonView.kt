package com.theternal.uikit.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.theternal.uikit.R
import com.theternal.uikit.databinding.ViewDateButtonBinding
import java.text.SimpleDateFormat
import java.util.Locale
import com.theternal.common.R.color as Colors
@SuppressLint("CustomViewStyleable")
class DateButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defAttrs: Int = 0
): ConstraintLayout(context, attrs, defAttrs) {

    private val binding = ViewDateButtonBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    private var datePicker:  MaterialDatePicker<Long>? = null

    private var listener: ((Long) -> Unit)? = null
    private var fragmentManager: FragmentManager? = null

    private val dateFormatter = SimpleDateFormat(
        "dd.MM.yyyy", Locale.getDefault()
    )

    init {
        context.obtainStyledAttributes(
            attrs, R.styleable.DateButtonView, defAttrs, 0
        ).apply {
            val backgroundTint = getColor(
                R.styleable.DateButtonView_backgroundTint,
                ContextCompat.getColor(context, Colors.frame)
            )

            binding.container.background.apply {
                mutate()
                setTint(backgroundTint)
            }

            recycle()
        }

        binding.container.setOnClickListener {
            if(datePicker?.isAdded == false && fragmentManager != null) {
                datePicker?.show(
                    fragmentManager!!,
                    datePicker?.tag
                )
            }
        }

        binding.date.text = dateFormatter.format(System.currentTimeMillis())
    }

    fun setDateSelectionListener(manager: FragmentManager, listener: (Long) -> Unit) {
        fragmentManager = manager
        this.listener = listener
    }

    fun setDate(date: Long) {
        binding.date.text = dateFormatter.format(date)

        if(datePicker == null) {
            datePicker = MaterialDatePicker.Builder.datePicker().setSelection(date).build()
            addListener()
        }
    }

    private fun addListener() {
        datePicker?.addOnPositiveButtonClickListener { date ->
            listener?.invoke(date)
            setDate(date)
        }
    }
}