package me.kmmiller.theduckypodcast.base.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.kmmiller.theduckypodcast.R

class BottomNavRecyclerView : FrameLayout {
    private lateinit var recyclerView: RecyclerView

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int): super(context, attrs, defStyle) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.bottom_nav_recycler_view, this)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = HorizontalGridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
    }

    fun setRecyclerViewAdapter(adapter: BottomNavAdapter) {
        recyclerView.adapter = adapter
    }

    // https://gist.github.com/heinrichreimer/2fcb22f160eefee6f07b
    private inner class HorizontalGridLayoutManager(
        context: Context,
        spanCount: Int,
        orientation: Int,
        reverseLayout: Boolean
    ) : GridLayoutManager(context, spanCount, orientation, reverseLayout) {
        private val horizontalSpace: Int
            get() = width - paddingRight - paddingLeft
        private val verticalSpace: Int
            get() = height - paddingBottom - paddingTop

        override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
            return spanLayoutSize(super.generateDefaultLayoutParams())
        }

        override fun generateLayoutParams(c: Context?, attrs: AttributeSet?): RecyclerView.LayoutParams {
            return spanLayoutSize(super.generateLayoutParams(c, attrs))
        }

        override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): RecyclerView.LayoutParams {
            return spanLayoutSize(super.generateLayoutParams(lp))
        }

        private fun spanLayoutSize(layoutParams: RecyclerView.LayoutParams): RecyclerView.LayoutParams {
            if(orientation == LinearLayoutManager.HORIZONTAL) {
                layoutParams.width = Math.round(horizontalSpace / Math.ceil(itemCount.toDouble() / spanCount)).toInt()
            } else if(orientation == LinearLayoutManager.VERTICAL) {
                layoutParams.height = Math.round(verticalSpace / Math.ceil(itemCount.toDouble() / spanCount)).toInt()
            }
            return layoutParams
        }

        override fun canScrollHorizontally(): Boolean  = false
        override fun canScrollVertically(): Boolean = false
    }
}