package com.theternal.uikit.adapters

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.theternal.common.extensions.capitalize
import com.theternal.common.extensions.format
import com.theternal.common.extensions.hide
import com.theternal.common.extensions.show
import com.theternal.core.base.BaseAdapter
import com.theternal.core.base.Binder
import com.theternal.core.base.Inflater
import com.theternal.domain.entities.base.IncomeCategory
import com.theternal.uikit.databinding.ViewCategoryItemBinding
import com.theternal.uikit.utility.getIconDrawable
import java.math.BigDecimal

class CategoryAdapter(
    val onItemClickListener: (CategoryItem) -> Unit
) : BaseAdapter<CategoryAdapter.CategoryItem, ViewCategoryItemBinding>(
    object : DiffUtil.ItemCallback<CategoryItem>() {
        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem.category == newItem.category
        }
        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem.isSelected == newItem.isSelected &&
                    oldItem.amount == newItem.amount
        }
    }
) {

    data class CategoryItem(
        val category: Enum<*>,
        val isSelected: Boolean = false,
        val amount: BigDecimal? = null,
    )

    override val bindingInflater: Inflater<ViewCategoryItemBinding>
        get() = ViewCategoryItemBinding::inflate

    override val itemBinder: Binder<CategoryItem, ViewCategoryItemBinding>
        @SuppressLint("SetTextI18n")
        get() = { item, _ ->
            container.setOnClickListener {
                onItemClickListener(item)
            }

            label.text = item.category.name.capitalize()

            icon.setImageDrawable(
                ContextCompat.getDrawable(
                    icon.context, getIconDrawable(item.category)
                )
            )

            if(item.amount != null) {
                val prefix = if(item.category is IncomeCategory) "+" else "-"
                amount.text = "$prefix${item.amount.format()} $"
                amount.show()
            }

            if(item.isSelected) checkIcon.show() else checkIcon.hide()
        }
}