package com.savent.erp.presentation.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.savent.erp.R
import com.savent.erp.presentation.ui.model.IncompletePaymentItem
import com.savent.erp.utils.DecimalFormat

class IncompletePaymentsAdapter(private val context: Context?)
    : RecyclerView.Adapter<IncompletePaymentsAdapter.IncompletePaymentsViewHolder>() {

    private val incompletePayments = ArrayList<IncompletePaymentItem>()

    private var _payListener: OnPayClickListener? = null

    interface OnPayClickListener {
        fun onPayClick(saleId: Int, debt: Float, dateTime: String)
    }

    fun setOnPayClickListener(listener: OnPayClickListener) {
        _payListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IncompletePaymentsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.incomplete_payment_item, parent, false)
        return IncompletePaymentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncompletePaymentsViewHolder, position: Int) {

        val incompletePayment = incompletePayments[position]
        holder.date.text = incompletePayment.dateTime
        holder.articles.text = incompletePayment.productsUnits.toString()

        holder.subtotal.text = context?.getString(R.string.price)
            ?.format(DecimalFormat.format(incompletePayment.subtotal))
        holder.taxes.text = context?.getString(R.string.price)
            ?.format(DecimalFormat.format(incompletePayment.taxes))
        holder.discounts.text = context?.getString(R.string.price)
            ?.format(DecimalFormat.format(incompletePayment.discounts))
        holder.toPay.text = context?.getString(R.string.price)
            ?.format(DecimalFormat.format(incompletePayment.toPay))
        holder.collected.text = context?.getString(R.string.price)
            ?.format(DecimalFormat.format(incompletePayment.collected))
        holder.debt.text = context?.getString(R.string.price)
            ?.format(DecimalFormat.format(incompletePayment.debts))

        holder.payBtn.setOnClickListener {
            _payListener?.onPayClick(
                incompletePayment.saleId,
                incompletePayment.debts,
                incompletePayment.dateTime
            )
        }

    }

    override fun getItemCount(): Int =
        incompletePayments.size

    override fun getItemId(position: Int): Long {
        return incompletePayments[position].localId.toLong()
    }

    fun setData(newIncompletePayments: List<IncompletePaymentItem>) {
        val diffCallback = IncompletePaymentDiffCallBack(incompletePayments, newIncompletePayments)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        incompletePayments.clear()
        incompletePayments.addAll(newIncompletePayments)
        diffResult.dispatchUpdatesTo(this)
    }

    class IncompletePaymentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date)
        val articles: TextView = itemView.findViewById(R.id.articles)
        val subtotal: TextView = itemView.findViewById(R.id.subtotal)
        val taxes: TextView = itemView.findViewById(R.id.tax)
        val discounts: TextView = itemView.findViewById(R.id.discounts)
        val toPay: TextView = itemView.findViewById(R.id.to_pay)
        val collected: TextView = itemView.findViewById(R.id.collected)
        val debt: TextView = itemView.findViewById(R.id.debt)
        val payBtn: LinearLayout = itemView.findViewById(R.id.pay_btn)
    }

}