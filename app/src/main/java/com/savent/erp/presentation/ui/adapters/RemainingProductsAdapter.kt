package com.savent.erp.presentation.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.savent.erp.R
import com.savent.erp.presentation.ui.model.ProductItem

class RemainingProductsAdapter (private val context: Context?) :
    RecyclerView.Adapter<RemainingProductsAdapter.ProductsViewHolder>() {

    private val products = ArrayList<ProductItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.product_item, parent, false)
        return ProductsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = products[position]

        product.image?.let { holder.image }
        holder.name.text = product.name
        holder.price.text = context?.getString(R.string.price)?.format("${product.price}")
        holder.remainingUnits.visibility = View.VISIBLE
        holder.remainingUnits.text = context?.getString(R.string.units)
            ?.format("${product.remainingUnits}")

        holder.unitsSelected.visibility = View.GONE
        holder.addUnit.visibility = View.GONE
        holder.removeUnit.visibility = View.GONE

    }

    override fun getItemCount(): Int =
        products.size

    override fun getItemId(position: Int): Long {
        return products[position].id.toLong()
    }

    fun setData(newProducts: List<ProductItem>) {
        val diffCallback = ProductsDiffCallBack(products, newProducts)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        products.clear()
        products.addAll(newProducts)
        diffResult.dispatchUpdatesTo(this)
    }

    class ProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: LinearLayout = itemView.findViewById(R.id.image)
        val name: TextView = itemView.findViewById(R.id.product_name)
        val price: TextView = itemView.findViewById(R.id.price)
        val remainingUnits: TextView = itemView.findViewById(R.id.stock)
        val unitsSelected: EditText = itemView.findViewById(R.id.units_selected)
        val addUnit: LinearLayout = itemView.findViewById(R.id.add_button)
        val removeUnit: LinearLayout = itemView.findViewById(R.id.remove_button)
        val div: View = itemView.findViewById(R.id.div)
    }


}