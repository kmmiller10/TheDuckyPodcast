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
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.databinding.WeekliesResultsFragmentBinding
import me.kmmiller.theduckypodcast.main.interfaces.NavItem
import me.kmmiller.theduckypodcast.models.ResultsAnswers
import me.kmmiller.theduckypodcast.models.ResultsQuestionAnswerModel
import me.kmmiller.theduckypodcast.utils.ColorblindPreferences

class WeekliesResultsFragment : BaseFragment(), NavItem {
    private lateinit var binding: WeekliesResultsFragmentBinding

    override fun getTitle(): String = getString(R.string.weekly_results)
    override fun getNavId(): Int = R.id.nav_weeklies

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = WeekliesResultsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onRefresh()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            onRefresh()
        }
    }


    private fun onRefresh() {
        viewModel?.weeklyId?.let { weeklyId ->
            getCollectedResponses(weeklyId) {
                drawAllCharts(it)
            }
        }
    }

    private fun getCollectedResponses(weeklyId: String, onSuccess: (model: ResultsAnswers) -> Unit) {
        fb.collection("weeklies-responses")
            .document("collected-responses")
            .get()
            .addOnSuccessListener { doc ->
                val model = ResultsAnswers()
                model.id = weeklyId
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

    private fun drawAllCharts(resultsAnswers: ResultsAnswers) {
        val titles = ArrayList<String>()
        val charts = ArrayList<BarChart>(resultsAnswers.answers.size)

        resultsAnswers.answers.forEach { answers ->
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
                    entries.add(BarEntry(i.toFloat() + 1f, otherCount))
                    if(otherCount > maxValue) maxValue = otherCount.toInt()
                } else {
                    val entryValue = answersMap[i.toLong() + 1]
                    if(entryValue != null) {
                        if(entryValue > maxValue) maxValue = entryValue.toInt()
                        entries.add(BarEntry(i.toFloat() + 1f, entryValue))
                    } else {
                        entries.add(BarEntry(i.toFloat() + 1f, 0f))
                    }
                }
            }

            titles.add(answers.question)

            val chart = makeChart(entries, answers, maxValue)
            charts.add(chart)
        }

        binding.swipeRefreshLayout.isRefreshing = false
        binding.chartList.adapter = BarChartAdapter(charts, titles)
        binding.chartList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun makeChart(entries: ArrayList<BarEntry>, answers: ResultsQuestionAnswerModel, maxValue: Int): BarChart {
        val barDataSet = BarDataSet(entries, answers.question)
        val colors = DailiesResultsFragment.getCbChartColors(
            requireContext(),
            ColorblindPreferences.getCbMode(requireActivity())
        )
        barDataSet.setDrawValues(false)
        barDataSet.colors = colors
        barDataSet.isHighlightEnabled = false
        val barData = BarData(barDataSet)
        barData.barWidth = .8f

        val xAxisTextValues = Array(answers.answerDescriptions.size + 2) { "" }
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
                legendEntry.label = label
                legendEntry.form = Legend.LegendForm.CIRCLE
                legendEntry.formSize = 12f
                legendEntry.formColor = colors[index]

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

        return chart
    }

    companion object {
        const val TAG = "weeklies_results_frag"
    }
}