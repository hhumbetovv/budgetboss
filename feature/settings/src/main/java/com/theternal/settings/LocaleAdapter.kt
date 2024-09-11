package com.theternal.settings

import androidx.recyclerview.widget.DiffUtil
import com.theternal.common.extensions.fadeOut
import com.theternal.common.extensions.show
import com.theternal.core.base.BaseAdapter
import com.theternal.core.base.Binder
import com.theternal.core.base.Inflater
import com.theternal.settings.databinding.ViewLanguageButtonBinding

typealias LocaleItem = Triple<String, String, Boolean>

class LocaleAdapter(
    private val onItemClickListener: (String) -> Unit
) : BaseAdapter<LocaleItem, ViewLanguageButtonBinding>(
    object : DiffUtil.ItemCallback<LocaleItem>() {
        override fun areItemsTheSame(oldItem: LocaleItem, newItem: LocaleItem): Boolean {
            return oldItem.first == newItem.first
        }

        override fun areContentsTheSame(oldItem: LocaleItem, newItem: LocaleItem): Boolean {
            return oldItem.second == newItem.second &&
                    oldItem.third == newItem.third
        }
    }
) {

    override val bindingInflater: Inflater<ViewLanguageButtonBinding>
        get() = ViewLanguageButtonBinding::inflate

    override val itemBinder: Binder<LocaleItem, ViewLanguageButtonBinding>
        get() = { locale, _ ->

            localeContainer.setOnClickListener {
                onItemClickListener(locale.first)
            }

            displayName.text = locale.second

            if (locale.third) {
                checkedIcon.show()
            } else {
                checkedIcon.fadeOut()
            }
        }
}