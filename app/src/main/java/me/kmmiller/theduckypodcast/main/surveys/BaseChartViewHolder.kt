package me.kmmiller.theduckypodcast.main.surveys

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseChartViewHolder(view: View): RecyclerView.ViewHolder(view) {
    abstract fun setTitle(title: String)
    abstract fun setTotalResponseCount(totalResponseCount: Int)
}