package com.savent.erp.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.savent.erp.R
import com.savent.erp.data.local.model.StoreEntity
import com.savent.erp.data.remote.model.Store
import com.savent.erp.utils.NameFormat

class StoresAdapter(): RecyclerView.Adapter<StoresAdapter.StoresViewHolder>() {

    val stores = ArrayList<StoreEntity>()
    private var _listener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(store: StoreEntity)
    }

    fun setOnClickListener(listener: OnClickListener) {
        _listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoresViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.string_layout, parent, false)
        return StoresViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoresViewHolder, position: Int) {
        val storeItem = stores[position]
        holder.name.text = NameFormat.format(storeItem.name)
        holder.name.setOnClickListener {
            _listener?.onClick(storeItem)
        }
    }

    override fun getItemCount(): Int = stores.size

    override fun getItemId(position: Int): Long = stores[position].id.toLong()

    fun setData(newStores: List<StoreEntity>) {
        val diffCallback = StoresDiffCallBack(stores, newStores)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        stores.clear()
        stores.addAll(newStores)
        diffResult.dispatchUpdatesTo(this)
    }

    class StoresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text)
    }
}