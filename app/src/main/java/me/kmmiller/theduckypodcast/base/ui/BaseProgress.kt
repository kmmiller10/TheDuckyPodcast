package me.kmmiller.theduckypodcast.base.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import me.kmmiller.theduckypodcast.R

class BaseProgress(context: Context) : Dialog(context) {
    private var progressBar: ProgressBar

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.progress_wheel_layout)
        progressBar = findViewById(R.id.progress_bar)
        progressBar.isIndeterminate = true
    }

    fun setMessage(message: CharSequence) {
        findViewById<AppCompatTextView>(R.id.progress_message).text = message
    }

    fun setProgress(percentage: Int) {
        progressBar.isIndeterminate = false
        progressBar.progress = percentage
    }
}