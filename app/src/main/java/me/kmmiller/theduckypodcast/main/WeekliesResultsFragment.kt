package me.kmmiller.theduckypodcast.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseResultsFragment
import me.kmmiller.theduckypodcast.databinding.WeekliesResultsFragmentBinding
import me.kmmiller.theduckypodcast.main.interfaces.NavItem
import me.kmmiller.theduckypodcast.models.ResultsAnswers

class WeekliesResultsFragment : BaseResultsFragment(), NavItem {
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
            getCollectedResponses("weeklies-responses", weeklyId) {
                drawAllCharts(it)
            }
        }
    }

    private fun drawAllCharts(resultsAnswers: ResultsAnswers) {
        binding.swipeRefreshLayout.isRefreshing = false
        binding.chartList.adapter = getChartsAdapter(resultsAnswers)
        binding.chartList.layoutManager = LinearLayoutManager(requireContext())
    }

    companion object {
        const val TAG = "weeklies_results_frag"
    }
}