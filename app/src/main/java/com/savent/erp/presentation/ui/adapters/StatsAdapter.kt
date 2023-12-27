package com.savent.erp.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil

import androidx.recyclerview.widget.RecyclerView
import com.savent.erp.R
import com.savent.erp.presentation.ui.model.StatItem

class StatsAdapter():RecyclerView.Adapter<StatsAdapter.StatsViewHolder>() {

    private val stats = ArrayList<StatItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.stats_item, parent, false)
        return StatsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
    }

    override fun getItemCount(): Int =
        stats.size

    fun setData(newStats: List<StatItem>) {
        val diffCallback = StatDiffCallBack(stats, newStats)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        stats.clear()
        stats.addAll(newStats)
        diffResult.dispatchUpdatesTo(this)
    }

    class StatsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.findViewById(R.id.stat_icon)

    }
}