package com.savent.erp.presentation.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.savent.erp.presentation.ui.model.ProductItem

class ProductsDiffCallBack (
    private val oldList: List<ProductItem>, private val newList: List<ProductItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return newList[newItemPosition]
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].hashCode() == newList[newItemPosition].hashCode()
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val (_, value, name) = oldList[oldItemPosition]
        val (_, value1, name1) = newList[newItemPosition]
        return name == name1 && value == value1
    }

}