package me.kmmiller.theduckypodcast.models

abstract class BaseChartData(val title: String, val totalResponseCount: Int) {

    abstract fun getType(): Type

    // Types of charts used in this app
    enum class Type(private val type: Int) {
        BAR(0),
        PIE(1)
    }
}