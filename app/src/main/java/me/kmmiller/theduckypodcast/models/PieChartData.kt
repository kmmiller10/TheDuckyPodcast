package me.kmmiller.theduckypodcast.models

import com.github.mikephil.charting.charts.PieChart

class PieChartData(title: String, totalResponseCount: Int, val chart: PieChart): BaseChartData(title, totalResponseCount) {
    override fun getType(): Type = Type.PIE
}