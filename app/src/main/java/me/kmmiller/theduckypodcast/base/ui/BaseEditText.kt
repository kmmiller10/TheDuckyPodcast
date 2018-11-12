package me.kmmiller.theduckypodcast.base.ui

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import me.kmmiller.theduckypodcast.R

class BaseEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : this(context, attrs) {
        init()
    }

    init {
        init()
    }

    private fun init() {
        setBackgroundResource(R.drawable.bordered_edit_text)

        // Adjust padding ints to density pixels
        val scale = resources.displayMetrics.density
        val dp = (8*scale + 0.5f).toInt()
        setPadding(dp, dp, dp, dp)
        setSingleLine(true)
    }


}