package com.savent.erp.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.savent.erp.R
import com.savent.erp.presentation.ui.model.ClientDebt
import com.savent.erp.presentation.ui.model.ClientWithDebtItem
import com.savent.erp.utils.DecimalFormat

class ClientsWithDebtsAdapter : RecyclerView.Adapter<ClientsWithDebtsAdapter.ClientsViewHolder>() {

    private val clients = ArrayList<ClientWithDebtItem>()

    private var _listener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(clientId: Int, clientDebt: ClientDebt)
    }

    fun setOnClickListener(listener: OnClickListener) {
        _listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.client_with_debt_item, parent, false)
        return ClientsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientsViewHolder, position: Int) {
        val clientItem = clients[position]
        clientItem.image?.let { holder.image }
        holder.name.text = clientItem.name
        holder.address.text = clientItem.address
        holder.debts.text = "$${DecimalFormat.format(clientItem.debt)}"
        holder.itemView.setOnClickListener {
            _listener?.onClick(
                clientItem.remoteId,
                ClientDebt(clientItem.name, holder.debts.text.toString())
            )
        }
    }

    override fun getItemCount(): Int =
        clients.size

    override fun getItemId(position: Int): Long {
        return clients[position].remoteId.toLong()
    }

    fun setData(newClients: List<ClientWithDebtItem>) {
        val diffCallback = ClientsWithDebtsDiffCallBack(clients, newClients)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        clients.clear()
        clients.addAll(newClients)
        diffResult.dispatchUpdatesTo(this)
    }

    class ClientsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val address: TextView = itemView.findViewById(R.id.location_tv)
        val image: LinearLayout = itemView.findViewById(R.id.image)
        val debts: TextView = itemView.findViewById(R.id.debts)
    }
}