package com.savent.erp.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.savent.erp.R
import com.savent.erp.presentation.ui.model.EmployeeItem

class EmployeesAdapter : RecyclerView.Adapter<EmployeesAdapter.EmployeesViewHolder>(){

    val employees = ArrayList<EmployeeItem>()
    private var _listener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(employee: EmployeeItem)
    }

    fun setOnClickListener(listener: OnClickListener) {
        _listener = listener
    }

    class EmployeesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.string_layout, parent, false)
        return EmployeesViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeesViewHolder, position: Int) {
        val employeeItem = employees[position]
        holder.name.text = employeeItem.name
        holder.name.setOnClickListener {
            _listener?.onClick(employeeItem)
        }
    }

    override fun getItemCount(): Int = employees.size

    override fun getItemId(position: Int): Long = employees[position].id.toLong()

    fun setData(newEmployees: List<EmployeeItem>) {
        val diffCallback = EmployeesDiffCallBack(employees, newEmployees)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        employees.clear()
        employees.addAll(newEmployees)
        diffResult.dispatchUpdatesTo(this)
    }

}