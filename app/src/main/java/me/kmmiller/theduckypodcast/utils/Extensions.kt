package me.kmmiller.theduckypodcast.utils

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.github.mikephil.charting.charts.BarChart
import com.google.firebase.firestore.DocumentSnapshot
import me.kmmiller.theduckypodcast.R
import java.lang.Exception

fun DocumentSnapshot?.getStringArrayList(element: String) : ArrayList<String> {
    val ary = ArrayList<String>()

    this?.apply {
        this[element]?.let {
            return try {
                ary.addAll(it as ArrayList<String>)
                ary
            } catch(e: Exception) {
                ary
            }
        }
    }

    return ary
}

fun DocumentSnapshot?.getHashMap(element: String) : HashMap<String, ArrayList<String>> {
    var map = HashMap<String, ArrayList<String>>()

    this?.apply {
        this[element]?.let {
            return try {
                map = it as HashMap<String, ArrayList<String>>
                map
            } catch(e: Exception) {
                map
            }
        }
    }

    return map
}

fun AppCompatEditText.onTextChangedListener(onTextChanged: (e: Editable?) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(e: Editable?) {
            onTextChanged.invoke(e)
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
    })
}

fun Any?.nonNullString(): String {
    if(this == null) return ""

    return try {
        (this as? String) ?: ""
    } catch (e: Exception) {
        ""
    }
}

fun Any?.nonNullLong(): Long {
    if(this == null) return 0L

    return try {
        (this as? Long) ?: 0L
    } catch (e: Exception) {
        0L
    }
}

fun BarChart.set(chart: BarChart) {
    // Set up x-axis
    xAxis.apply {
        granularity = chart.xAxis.granularity
        valueFormatter = chart.xAxis.valueFormatter
        position = chart.xAxis.position
        setDrawGridLines(chart.xAxis.isDrawGridLinesEnabled)
    }

    // Set up y-axis
    axisLeft.apply {
        granularity = chart.axisLeft.granularity
        valueFormatter = chart.axisLeft.valueFormatter
        setDrawGridLines(chart.axisLeft.isDrawGridLinesEnabled)
        setDrawZeroLine(chart.axisLeft.isDrawZeroLineEnabled)
        axisMinimum = chart.axisLeft.mAxisMinimum

    }
    axisRight.isEnabled = false // Forcing right y-axis to be disabled

    // Set up legend
    legend.apply {
        isEnabled = true
        setDrawInside(chart.legend.isDrawInsideEnabled)
        verticalAlignment = chart.legend.verticalAlignment
        horizontalAlignment = chart.legend.horizontalAlignment
        orientation = chart.legend.orientation
        setCustom(chart.legend.entries)
        yOffset = chart.legend.yOffset
    }

    description.isEnabled = chart.description.isEnabled

    data = chart.barData
    setFitBars(true)
}

fun String.validatePassword(activity: Activity, confirmPassword: String) : String {
    return when {
        this != confirmPassword -> {
            activity.getString(R.string.passwords_do_not_match)
        }
        this.length < 7 -> {
            activity.getString(R.string.password_minimum_characters)
        }
        this.toLowerCase() == this -> {
            activity.getString(R.string.password_one_capital)
        }
        !this.contains(Regex(".*\\d+.*")) -> {
            activity.getString(R.string.password_one_number)
        }
        else -> ""
    }
}