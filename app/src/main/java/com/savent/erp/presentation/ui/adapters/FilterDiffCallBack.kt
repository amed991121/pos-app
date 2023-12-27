package com.savent.erp.presentation.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.savent.erp.presentation.ui.model.FilterItem


class FilterDiffCallBack(
    private val oldList: List<FilterItem>, private val newList: List<FilterItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].hashCode() == newList[newItemPosition].hashCode()
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val (value, name) = oldList[oldItemPosition]
        val (value1, name1) = newList[newItemPosition]
        return name == name1 && value == value1

    }

}