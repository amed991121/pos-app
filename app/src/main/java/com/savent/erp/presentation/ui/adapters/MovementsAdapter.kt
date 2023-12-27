package com.savent.erp.presentation.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.savent.erp.R
import com.savent.erp.data.common.model.MovementType
import com.savent.erp.presentation.ui.model.MovementItem

class MovementsAdapter(private val context: Context) :
    RecyclerView.Adapter<MovementsAdapter.MovementsViewHolder>() {

    val movements = ArrayList<MovementItem>()
    private var _listener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(movement: MovementItem)
    }

    fun setOnClickListener(listener: OnClickListener) {
        _listener = listener
    }

    class MovementsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val movementType: TextView = itemView.findViewById(R.id.movement_type)
        val date: TextView = itemView.findViewById(R.id.date)
        val reason: TextView = itemView.findViewById(R.id.reason)
        val employee: TextView = itemView.findViewById(R.id.employee)
        val purchaseIcon: ImageView = itemView.findViewById(R.id.purchase_icon)
        val purchaseLabel: TextView = itemView.findViewById(R.id.purchase_label)
        val purchaseId: TextView = itemView.findViewById(R.id.purchase_id)
        val inputStoreKeeperIcon: ImageView = itemView.findViewById(R.id.person_icon)
        val inputStoreKeeperLabel: TextView = itemView.findViewById(R.id.input_store_keeper_label)
        val inputStoreKeeper: TextView = itemView.findViewById(R.id.input_store_keeper)
        val inputStoreIcon: ImageView = itemView.findViewById(R.id.store_icon)
        val inputStoreLabel: TextView = itemView.findViewById(R.id.input_store_label)
        val inputStore: TextView = itemView.findViewById(R.id.input_store)
        val outputStoreKeeperIcon: ImageView = itemView.findViewById(R.id.person_icon2)
        val outputStoreKeeperLabel: TextView = itemView.findViewById(R.id.output_store_keeper_label)
        val outputStoreKeeper: TextView = itemView.findViewById(R.id.output_store_keeper)
        val outputStoreIcon: ImageView = itemView.findViewById(R.id.store_icon2)
        val outputStoreLabel: TextView = itemView.findViewById(R.id.output_store_label)
        val outputStore: TextView = itemView.findViewById(R.id.output_store)
        val productsUnits: TextView = itemView.findViewById(R.id.product_units)
        val time: TextView = itemView.findViewById(R.id.time)
        val infoIcon: ImageView = itemView.findViewById(R.id.info_icon)
        val infoTv: TextView = itemView.findViewById(R.id.info_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovementsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.movement_item, parent, false)
        return MovementsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovementsViewHolder, position: Int) {
        val item = movements[position]
        if (item.type == MovementType.INPUT) {
            holder.image.setImageResource(R.drawable.login)
            holder.movementType.text = context.getString(R.string.input).uppercase()
        } else {
            holder.image.setImageResource(R.drawable.logout_draw)
            holder.movementType.text = context.getString(R.string.output).uppercase()
        }
        holder.date.text = item.date
        holder.reason.text = item.reason
        holder.time.text = item.time
        holder.productsUnits.text = item.productsUnits.toString()

        if (item.purchaseId != null) {
            holder.purchaseIcon.visibility = View.VISIBLE
            holder.purchaseLabel.visibility = View.VISIBLE
            holder.purchaseId.visibility = View.VISIBLE
            holder.purchaseId.text = item.purchaseId.toString()
        } else {
            holder.purchaseIcon.visibility = View.GONE
            holder.purchaseLabel.visibility = View.GONE
            holder.purchaseId.visibility = View.GONE
        }

        if (item.inputStore != null) {
            holder.inputStoreIcon.visibility = View.VISIBLE
            holder.inputStoreLabel.visibility = View.VISIBLE
            holder.inputStore.visibility = View.VISIBLE
            holder.inputStore.text = item.inputStore
        } else {
            holder.inputStoreIcon.visibility = View.GONE
            holder.inputStoreLabel.visibility = View.GONE
            holder.inputStore.visibility = View.GONE
        }

        if (item.outputStore != null) {
            holder.outputStoreIcon.visibility = View.VISIBLE
            holder.outputStoreLabel.visibility = View.VISIBLE
            holder.outputStore.visibility = View.VISIBLE
            holder.outputStore.text = item.outputStore
        } else {
            holder.outputStoreIcon.visibility = View.GONE
            holder.outputStoreLabel.visibility = View.GONE
            holder.outputStore.visibility = View.GONE
        }

        if (item.inputStoreKeeper != null) {
            holder.inputStoreKeeperIcon.visibility = View.VISIBLE
            holder.inputStoreKeeperLabel.visibility = View.VISIBLE
            holder.inputStoreKeeper.visibility = View.VISIBLE
            holder.inputStoreKeeper.text = item.inputStoreKeeper
        } else {
            holder.inputStoreKeeperIcon.visibility = View.GONE
            holder.inputStoreKeeperLabel.visibility = View.GONE
            holder.inputStoreKeeper.visibility = View.GONE
        }

        if (item.outputStoreKeeper != null) {
            holder.outputStoreKeeperIcon.visibility = View.VISIBLE
            holder.outputStoreKeeperLabel.visibility = View.VISIBLE
            holder.outputStoreKeeper.visibility = View.VISIBLE
            holder.outputStoreKeeper.text = item.outputStoreKeeper
        } else {
            holder.outputStoreKeeperIcon.visibility = View.GONE
            holder.outputStoreKeeperLabel.visibility = View.GONE
            holder.outputStoreKeeper.visibility = View.GONE
        }

        if (item.isPendingSending) {
            holder.infoIcon.setImageResource(R.drawable.ic_baseline_error_24)
            holder.infoTv.text = context.getString(R.string.pending_sending)
        } else {
            holder.infoIcon.setImageResource(R.drawable.hastag)
            holder.infoTv.text = "${item.remoteId}"
        }
    }

    override fun getItemCount(): Int = movements.size

    override fun getItemId(position: Int): Long = movements[position].localId.toLong()

    fun setData(newMovements: List<MovementItem>) {
        val diffCallback = MovementsDiffCallBack(movements, newMovements)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        movements.clear()
        movements.addAll(newMovements)
        diffResult.dispatchUpdatesTo(this)
    }

}