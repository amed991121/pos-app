package com.savent.erp.presentation.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.savent.erp.R
import com.savent.erp.presentation.ui.model.DebtPaymentItem
import com.savent.erp.utils.DecimalFormat
import com.savent.erp.utils.PaymentMethod

class DebtPaymentsAdapter(private val context: Context?) :
    RecyclerView.Adapter<DebtPaymentsAdapter.DebtPaymentsViewHolder>() {

    private val debtPayments = ArrayList<DebtPaymentItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtPaymentsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.debt_payment_item, parent, false)
        return DebtPaymentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: DebtPaymentsViewHolder, position: Int) {
        val debtPaymentItem = debtPayments[position]

        holder.clientName.text = debtPaymentItem.clientName

        holder.date.text = debtPaymentItem.saleDateTime

        holder.time.text = debtPaymentItem.time

        when (debtPaymentItem.paymentMethod) {
            PaymentMethod.Cash -> {
                holder.paymentMethod.text = context?.getString(R.string.cash)
                holder.payImage.setImageResource(R.drawable.money_draw)
            }
            PaymentMethod.Credit -> {
                holder.paymentMethod.text = context?.getString(R.string.credit)
                holder.payImage.setImageResource(R.drawable.credit_card_draw)
            }
            PaymentMethod.Debit -> {
                holder.paymentMethod.text = context?.getString(R.string.debit)
                holder.payImage.setImageResource(R.drawable.credit_card_draw)
            }
            PaymentMethod.Transfer -> {
                holder.paymentMethod.text = context?.getString(R.string.transfer)
                holder.payImage.setImageResource(R.drawable.electronic_transfer_draw)
            }
        }

        holder.collected.text = context?.getString(R.string.price)
            ?.format(DecimalFormat.format(debtPaymentItem.collected))

        holder.debt.text = context?.getString(R.string.price)
            ?.format(DecimalFormat.format(debtPaymentItem.toPay))

        if (debtPaymentItem.isPendingSending) {
            holder.infoIcon.setImageResource(R.drawable.ic_baseline_error_24)
            holder.infoTv.text = context?.getString(R.string.pending_sending)
        } else {
            holder.infoIcon.setImageResource(R.drawable.hastag)
            holder.infoTv.text = "${debtPaymentItem.id}"
        }

    }

    override fun getItemCount(): Int =
        debtPayments.size

    override fun getItemId(position: Int): Long {
        return debtPayments[position].id.toLong()
    }

    fun setData(newItems: List<DebtPaymentItem>) {
        val diffCallback = DebtPaymentDiffCallBack(debtPayments, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        debtPayments.clear()
        debtPayments.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    class DebtPaymentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clientName: TextView = itemView.findViewById(R.id.client_name)
        val date: TextView = itemView.findViewById(R.id.date)
        val time: TextView = itemView.findViewById(R.id.time)
        val paymentMethod: TextView = itemView.findViewById(R.id.payment_method)
        val payImage: ImageView = itemView.findViewById(R.id.pay_image)
        val collected: TextView = itemView.findViewById(R.id.collected)
        val debt: TextView = itemView.findViewById(R.id.debt)
        val infoIcon: ImageView = itemView.findViewById(R.id.info_icon)
        val infoTv: TextView = itemView.findViewById(R.id.info_tv)
    }

}