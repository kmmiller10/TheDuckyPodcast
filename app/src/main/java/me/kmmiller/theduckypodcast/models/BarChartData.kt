package me.kmmiller.theduckypodcast.models

import com.github.mikephil.charting.charts.BarChart

class BarChartData(title: String, totalResponseCount: Int, val chart: BarChart): BaseChartData(title, totalResponseCount) {
    override fun getType(): Type = Type.BAR
}