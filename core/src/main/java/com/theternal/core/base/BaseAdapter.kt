package com.theternal.core.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

typealias Binder <T, VB> = VB.(T, Int) -> Unit

abstract class BaseAdapter<T, VB : ViewBinding>(
    diffCallBack: DiffUtil.ItemCallback<T>
) : ListAdapter<T, BaseAdapter<T, VB>.ViewHolder<T, VB>>(diffCallBack) {

    abstract val bindingInflater: Inflater<VB>
    abstract val itemBinder: Binder<T, VB>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T, VB> {
        val view = bindingInflater(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view, itemBinder)
    }

    override fun onBindViewHolder(holder: ViewHolder<T, VB>, position: Int) {
        val item = getItem(position)
        holder.bindData(item, position)
    }

    inner class ViewHolder<T, VB : ViewBinding>(
        private val binding: VB,
        private val itemBinder: Binder<T, VB>
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: T, position: Int) {
            itemBinder.invoke(binding, item, position)
        }
    }
}