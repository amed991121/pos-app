package com.savent.erp.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.savent.erp.R
import com.savent.erp.presentation.ui.model.EmployeeItem
import com.savent.erp.presentation.ui.model.PurchaseItem

class PurchasesAdapter : RecyclerView.Adapter<PurchasesAdapter.PurchasesViewHolder>(){

    val purchases = ArrayList<PurchaseItem>()
    private var _listener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(purchase: PurchaseItem)
    }

    fun setOnClickListener(listener: OnClickListener) {
        _listener = listener
    }

    class PurchasesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val buyer: TextView = itemView.findViewById(R.id.buyer)
        val date: TextView = itemView.findViewById(R.id.date)
        val provider: TextView = itemView.findViewById(R.id.provider)
        val remainingProducts: TextView = itemView.findViewById(R.id.remaining_products)
        val amount: TextView = itemView.findViewById(R.id.amount)
        val time: TextView = itemView.findViewById(R.id.time)
        val id: TextView = itemView.findViewById(R.id.info_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchasesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.purchase_item, parent, false)
        return PurchasesViewHolder(view)
    }

    override fun onBindViewHolder(holder: PurchasesViewHolder, position: Int) {
        val item = purchases[position]
        holder.buyer.text = item.buyer
        holder.date.text = item.date
        holder.provider.text = item.provider
        holder.remainingProducts.text = item.remainingUnits.toString()
        holder.amount.text = item.amount
        holder.time.text = item.time
        holder.id.text = item.id.toString()

        holder.itemView.setOnClickListener {
            _listener?.onClick(item)
        }
    }

    override fun getItemCount(): Int = purchases.size

    override fun getItemId(position: Int): Long = purchases[position].id.toLong()

    fun setData(newPurchases: List<PurchaseItem>) {
        val diffCallback = PurchasesDiffCallBack(purchases, newPurchases)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        purchases.clear()
        purchases.addAll(newPurchases)
        diffResult.dispatchUpdatesTo(this)
    }

}