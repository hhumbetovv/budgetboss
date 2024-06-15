package com.theternal.uikit.adapters

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.theternal.common.constants.MINUS
import com.theternal.common.constants.PLUS
import com.theternal.common.extensions.format
import com.theternal.common.extensions.hide
import com.theternal.common.extensions.show
import com.theternal.core.base.BaseAdapter
import com.theternal.core.base.Binder
import com.theternal.core.base.Inflater
import com.theternal.domain.entities.base.IncomeCategory
import com.theternal.uikit.databinding.ViewCategoryItemBinding
import com.theternal.uikit.utility.getCategoryIcon
import com.theternal.uikit.utility.getCategoryName
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
    ) {
        fun showAmount(): String {
            val prefix = if(category is IncomeCategory) PLUS else MINUS
            return "$prefix${amount?.format(true)}"
        }
    }

    override val bindingInflater: Inflater<ViewCategoryItemBinding>
        get() = ViewCategoryItemBinding::inflate

    override val itemBinder: Binder<CategoryItem, ViewCategoryItemBinding>
        get() = { item, _ ->

            container.setOnClickListener {
                onItemClickListener(item)
            }

            label.text = label.context.getString(getCategoryName(item.category))

            icon.setImageDrawable(
                ContextCompat.getDrawable(
                    icon.context, getCategoryIcon(item.category)
                )
            )

            if(item.amount != null) {
                amount.text = item.showAmount()
                amount.show()
            }

            if(item.isSelected) checkIcon.show() else checkIcon.hide()
        }
}