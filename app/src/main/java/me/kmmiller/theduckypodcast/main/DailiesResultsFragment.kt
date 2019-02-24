package me.kmmiller.theduckypodcast.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
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

        if(isNewSubmission()) {
            Snackbar.make(binding.root, getString(R.string.new_submission_info), Snackbar.LENGTH_LONG).show()
            arguments?.putBoolean(NEW_SUBMISSION, false) // Toggle the value off so the user doesn't get the snackbar on rotation
        }

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

    private fun isNewSubmission(): Boolean = arguments?.getBoolean(NEW_SUBMISSION, false) ?: false

    companion object {
        const val TAG = "dailies_results_frag"
        private const val NEW_SUBMISSION = "new_submission"

        @JvmStatic
        fun getInstance(isNewSubmission: Boolean): DailiesResultsFragment {
            val frag = DailiesResultsFragment()
            val bundle = Bundle()
            bundle.putBoolean(NEW_SUBMISSION, isNewSubmission)
            frag.arguments = bundle
            return frag
        }
    }
}