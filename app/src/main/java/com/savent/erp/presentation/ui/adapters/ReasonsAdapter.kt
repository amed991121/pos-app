package com.savent.erp.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.savent.erp.R
import com.savent.erp.data.common.model.MovementReason
import com.savent.erp.presentation.ui.model.MovementReasonItem

class ReasonsAdapter: RecyclerView.Adapter<ReasonsAdapter.ReasonsViewHolder>() {

    private val reasons = ArrayList<MovementReasonItem>()
    private var _listener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(reason: MovementReasonItem)
    }

    fun setOnClickListener(listener: OnClickListener) {
        _listener = listener
    }

    class ReasonsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReasonsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.string_layout, parent, false)
        return ReasonsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReasonsViewHolder, position: Int) {
        val item = reasons[position]
        holder.name.text = item.name
        holder.name.setOnClickListener {
            _listener?.onClick(item)
        }
    }

    override fun getItemCount(): Int = reasons.size

    override fun getItemId(position: Int): Long = reasons[position].id.toLong()

    fun setData(newReasons: List<MovementReasonItem>) {
        val diffCallback = ReasonsDiffCallBack(reasons, newReasons)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        reasons.clear()
        reasons.addAll(newReasons)
        diffResult.dispatchUpdatesTo(this)
    }
}