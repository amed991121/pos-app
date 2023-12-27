package com.savent.erp.presentation.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.savent.erp.presentation.ui.model.EmployeeItem
import com.savent.erp.presentation.ui.model.PurchaseItem

class PurchasesDiffCallBack(
    private val oldList: List<PurchaseItem>, private val newList: List<PurchaseItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].hashCode() == newList[newItemPosition].hashCode()

    }
}