package com.savent.erp.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.savent.erp.R
import com.savent.erp.presentation.ui.model.ClientItem

class ClientsAdapter : RecyclerView.Adapter<ClientsAdapter.ClientsViewHolder>() {

    private val clients = ArrayList<ClientItem>()

    private var _listener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(id: Int)
    }

    fun setOnClickListener(listener: OnClickListener) {
        _listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.client_item, parent, false)
        return ClientsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientsViewHolder, position: Int) {
        val clientItem = clients[position]
        clientItem.image?.let { holder.image }
        holder.name.text = clientItem.name
        holder.address.text = clientItem.address
        holder.check.visibility = if(clientItem.isSelected) View.VISIBLE else View.INVISIBLE

        holder.itemView.setOnClickListener {
            _listener?.onClick(clientItem.localId)
        }
    }

    override fun getItemCount(): Int =
        clients.size

    override fun getItemId(position: Int): Long {
        return clients[position].localId.toLong()
    }

    fun setData(newClients: List<ClientItem>) {
        val diffCallback = ClientDiffCallBack(clients, newClients)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        clients.clear()
        clients.addAll(newClients)
        diffResult.dispatchUpdatesTo(this)
    }

    class ClientsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val address: TextView = itemView.findViewById(R.id.location_tv)
        val image: LinearLayout = itemView.findViewById(R.id.image)
        val check: ImageView = itemView.findViewById(R.id.check)
    }
}