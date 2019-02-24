package me.kmmiller.theduckypodcast.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseResultsFragment
import me.kmmiller.theduckypodcast.databinding.DailiesResultsFragmentBinding
import me.kmmiller.theduckypodcast.main.interfaces.NavItem
import me.kmmiller.theduckypodcast.models.ResultsAnswers

class DailiesResultsFragment : BaseResultsFragment(), NavItem {
    private lateinit var binding: DailiesResultsFragmentBinding

    override fun getTitle(): String = getString(R.string.daily_results)
    override fun getNavId(): Int = R.id.nav_dailies

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DailiesResultsFragmentBinding.inflate(inflater, container, false)
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
        viewModel?.dailyId?.let { dailyId ->
            getCollectedResponses("dailies-responses", dailyId) {
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
        const val TAG = "dailies_results_frag"
    }
}