package me.kmmiller.theduckypodcast.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                val answers = it.answers.firstOrNull()

                if(answers != null) {

                    val entries = ArrayList<BarEntry>()
                    val answersMap = HashMap<Long, Float>()
                    var otherCount = 0f // Keep track of "other" separately so it can be appended to the end of the bar chart

                    for(answer in answers.answers) {

                        val value = answersMap[answer]

                        if(value != null) {
                            if(answer == -1L) {
                                // Skip adding other to the map so it can be appended at the end instead of the first position
                                otherCount++
                            } else {
                                answersMap[answer] = value.plus(1)
                            }
                        } else {
                            answersMap[answer] = 1f
                        }
                    }

                    var maxValue = 0
                    var index = 0
                    answersMap.forEach { entry ->
                        // Add the regular answers (not other)
                        entries.add(BarEntry(entry.key.toFloat(), entry.value))
                        if(entry.value > maxValue) maxValue = entry.value.toInt()
                        index++
                    }
                    if(otherCount > 0f) {
                        // Now add other answers
                        entries.add(BarEntry(entries.size.toFloat().plus(1), otherCount))
                        if(otherCount > maxValue) maxValue = otherCount.toInt()
                    }

                    val barDataSet = BarDataSet(entries, answers.question)
                    barDataSet.axisDependency = YAxis.AxisDependency.LEFT
                    val barData = BarData(barDataSet)
                    barData.barWidth =.8f

                    val xAxisTextValues = Array(answers.answerDescriptions.size) { i -> "${i+1}"}
                    val xAxisFormatter = IAxisValueFormatter { value, _ -> xAxisTextValues[value.toInt() - 1] }

                    val yAxisTextValues = Array(maxValue+1) { i -> "$i" }
                    val yAxisFormatter = IAxisValueFormatter { value, _ -> yAxisTextValues[value.toInt()] }

                    val chart = binding.chart
                    chart.apply {
                        xAxis.apply {
                            granularity = 1f
                            valueFormatter = xAxisFormatter
                            position = XAxis.XAxisPosition.BOTTOM
                            setDrawGridLines(true)
                        }

                        axisLeft.apply {
                            granularity = 1f
                            valueFormatter = yAxisFormatter
                            setDrawGridLines(false)
                            setDrawZeroLine(true)
                            axisMinimum = 0f

                        }
                        axisRight.isEnabled = false

                        data = barData
                        setFitBars(true)
                        invalidate()
                    }
                }

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