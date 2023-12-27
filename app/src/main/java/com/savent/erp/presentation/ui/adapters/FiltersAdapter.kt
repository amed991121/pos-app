package com.savent.erp.presentation.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.savent.erp.R
import com.savent.erp.presentation.ui.model.FilterItem

class FiltersAdapter: RecyclerView.Adapter<FiltersAdapter.FilterViewHolder>() {

    private val filters = ArrayList<FilterItem>()

    private var _listener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(filter: String)
    }

    fun setOnClickListener(listener: OnClickListener) {
        _listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.filter_item, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filterItem = filters[position]
        holder.filter.text = filterItem.filter
        holder.check.visibility = if(filterItem.isSelected) View.VISIBLE else View.INVISIBLE

        holder.itemView.setOnClickListener {
            _listener?.onClick(filterItem.filter)
        }

    }

    override fun getItemCount(): Int =
        filters.size

    override fun getItemId(position: Int): Long {
        return filters[position].filter.hashCode().toLong()
    }

    fun setData(newFilters: List<FilterItem>) {
        val diffCallback = FilterDiffCallBack(filters, newFilters)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        filters.clear()
        filters.addAll(newFilters)
        diffResult.dispatchUpdatesTo(this)
    }

    class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filter: TextView = itemView.findViewById(R.id.filter)
        val check: ImageView = itemView.findViewById(R.id.check)
    }

}