package me.kmmiller.theduckypodcast.models

import com.github.mikephil.charting.charts.BarChart

data class ResultChartData(val title: String, val chart: BarChart, val totalResponseCount: Int)