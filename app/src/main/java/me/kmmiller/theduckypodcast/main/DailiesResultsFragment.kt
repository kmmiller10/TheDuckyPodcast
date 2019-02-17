package me.kmmiller.theduckypodcast.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.databinding.DailiesResultsFragmentBinding
import me.kmmiller.theduckypodcast.models.ResultsAnswers

class DailiesResultsFragment : BaseFragment() {
    private lateinit var binding: DailiesResultsFragmentBinding

    override fun getTitle(): String = getString(R.string.results)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DailiesResultsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel?.dailyId?.let { dailyId ->
            getCollectedResponses(dailyId) {


                val charts = ArrayList<BarChart>(it.answers.size)

                it.answers.forEach { answers ->
                    val entries = ArrayList<BarEntry>()
                    val answersMap = HashMap<Long, Float>()
                    var otherCount = 0f // Keep track of "other" separately so it can be appended to the end of the bar chart

                    for(answer in answers.answers) {
                        val value = answersMap[answer]
                        when {
                            answer < 0L -> otherCount++ // Skip adding other to the map so it can be appended at the end instead of the first position
                            value != null -> answersMap[answer] = value.plus(1)
                            else -> answersMap[answer] = 1f
                        }
                    }

                    var maxValue = 0
                    for(i in 0 until answers.answerDescriptions.size) {
                        if(i == answers.answerDescriptions.size - 1 && otherCount > 0) {
                            // Last answer and other count > 0, so there are "other" responses to tally
                            entries.add(BarEntry(i.toFloat(), otherCount))
                            if(otherCount > maxValue) maxValue = otherCount.toInt()
                        } else {
                            val entryValue = answersMap[i.toLong()]
                            if(entryValue != null) {
                                if(entryValue > maxValue) maxValue = entryValue.toInt()
                                entries.add(BarEntry(i.toFloat(), entryValue))
                            } else {
                                entries.add(BarEntry(i.toFloat(), 0f))
                            }
                        }
                    }

                    val barDataSet = BarDataSet(entries, answers.question)
                    barDataSet.setDrawValues(false)
                    barDataSet.axisDependency = YAxis.AxisDependency.LEFT
                    val barData = BarData(barDataSet)
                    barData.barWidth = .8f

                    val xAxisTextValues = Array(answers.answerDescriptions.size + 2) { i -> "(${i+1})" }
                    val xAxisFormatter = IAxisValueFormatter { value, _ -> xAxisTextValues[value.toInt()] }

                    val yAxisTextValues = Array(maxValue + 2) { i -> "$i" }
                    val yAxisFormatter = IAxisValueFormatter { value, _ -> yAxisTextValues[value.toInt()] }

                    val chart = BarChart(requireContext())
                    chart.apply {

                        // Set up x-axis
                        xAxis.apply {
                            granularity = 1f
                            valueFormatter = xAxisFormatter
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(false)
                            setFitBars(true)
                            axisMinimum = 0f
                            axisMaximum = answers.answerDescriptions.size.toFloat()
                        }

                        // Set up y-axis
                        axisLeft.apply {
                            granularity = 1f
                            valueFormatter = yAxisFormatter
                            setDrawGridLines(true)
                            setDrawZeroLine(true)
                            axisMinimum = 0f

                        }
                        axisRight.isEnabled = false

                        // Set up legend
                        var index = 0
                        val legendLabels = ArrayList<LegendEntry>(answers.answerDescriptions.size)
                        answers.answerDescriptions.forEach { label ->
                            val legendEntry = LegendEntry()
                            legendEntry.label = "(${index+1}) $label"
                            legendLabels.add(legendEntry)
                            index++
                        }
                        legend.apply {
                            isEnabled = true
                            setDrawInside(false)
                            verticalAlignment = Legend.LegendVerticalAlignment.TOP
                            horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                            orientation = Legend.LegendOrientation.VERTICAL
                            setCustom(legendLabels)
                        }

                        description.isEnabled = false

                        data = barData
                        setFitBars(true)
                    }

                    charts.add(chart)
                }

                binding.chartList.adapter = BarChartAdapter(charts)
                binding.chartList.layoutManager = LinearLayoutManager(requireContext())
            }
        }

    }

    private fun getCollectedResponses(dailyId: String, onSuccess: (model: ResultsAnswers) -> Unit) {
        fb.collection("dailies-responses")
            .document("collected-responses")
            .get()
            .addOnSuccessListener { doc ->
                val model = ResultsAnswers()
                model.id = dailyId
                model.toRealmModel(doc)
                realm?.executeTransaction { rm ->
                    rm.copyToRealmOrUpdate(model)
                }
                onSuccess.invoke(model)
            }
            .addOnFailureListener { e ->
                handleError(e)
            }
    }

    companion object {
        const val TAG = "dailies_results_frag"
    }
}