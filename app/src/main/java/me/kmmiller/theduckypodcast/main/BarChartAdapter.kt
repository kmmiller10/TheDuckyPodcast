package me.kmmiller.theduckypodcast.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import me.kmmiller.theduckypodcast.databinding.BarChartViewHolderBinding
import me.kmmiller.theduckypodcast.utils.set

class BarChartAdapter(private val items: ArrayList<BarChart>, private val titles: ArrayList<String>) : RecyclerView.Adapter<BarChartAdapter.BarChartViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarChartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BarChartViewHolder(BarChartViewHolderBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: BarChartViewHolder, position: Int) {
        val item = items[position]
        holder.setChart(item)
        holder.setTitle(titles[position])
    }

    override fun getItemCount(): Int = items.size

    inner class BarChartViewHolder(private val binding: BarChartViewHolderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setTitle(title: String) {
            binding.title.text = title
        }

        fun setChart(chart: BarChart) {
            binding.chart.set(chart)
        }
    }
}