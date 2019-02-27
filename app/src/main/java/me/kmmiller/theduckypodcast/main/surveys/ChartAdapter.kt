package me.kmmiller.theduckypodcast.main.surveys

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.databinding.BarChartViewHolderBinding
import me.kmmiller.theduckypodcast.databinding.PieChartViewHolderBinding
import me.kmmiller.theduckypodcast.models.BarChartData
import me.kmmiller.theduckypodcast.models.BaseChartData
import me.kmmiller.theduckypodcast.models.PieChartData
import me.kmmiller.theduckypodcast.utils.set

class ChartAdapter(private val items: ArrayList<BaseChartData>) : RecyclerView.Adapter<BaseChartViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseChartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            BaseChartData.Type.BAR.ordinal -> BarChartViewHolder(BarChartViewHolderBinding.inflate(inflater, parent, false))
            BaseChartData.Type.PIE.ordinal -> PieChartViewHolder(PieChartViewHolderBinding.inflate(inflater, parent, false))
            else -> BarChartViewHolder(BarChartViewHolderBinding.inflate(inflater, parent, false)) // Default to bar chart
        }
    }

    override fun onBindViewHolder(holder: BaseChartViewHolder, position: Int) {
        val item = items[position]
        holder.setTitle(item.title)
        holder.setTotalResponseCount(item.totalResponseCount)

        when(item.getType()) {
            BaseChartData.Type.BAR -> {
                val bar = item as BarChartData
                (holder as BarChartViewHolder).setChart(bar.chart)
            }
            BaseChartData.Type.PIE -> {
                val pie = item as PieChartData
                (holder as PieChartViewHolder).setChart(pie.chart)
            }
        }
    }

    override fun getItemViewType(position: Int): Int = items[position].getType().ordinal

    override fun getItemCount(): Int = items.size

    inner class BarChartViewHolder(private val binding: BarChartViewHolderBinding) : BaseChartViewHolder(binding.root) {
        override fun setTitle(title: String) {
            binding.title.text = title
        }

        override fun setTotalResponseCount(totalResponseCount: Int) {
            binding.yAxisLabel.text = String.format(binding.root.context.getString(R.string.responses), totalResponseCount)
        }

        fun setChart(chart: BarChart) {
            binding.chart.set(chart)
        }
    }

    inner class PieChartViewHolder(private val binding: PieChartViewHolderBinding) : BaseChartViewHolder(binding.root) {
        override fun setTitle(title: String) {
            binding.title.text = title
        }

        override fun setTotalResponseCount(totalResponseCount: Int) {
            binding.responseCountLabel.text = String.format(binding.root.context.getString(R.string.responses), totalResponseCount)
        }

        fun setChart(chart: PieChart) {
            binding.chart.set(chart)
        }
    }

}