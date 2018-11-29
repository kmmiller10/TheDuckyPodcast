package me.kmmiller.theduckypodcast.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.databinding.DailiesResultsFragmentBinding

class DailiesResultsFragment : BaseFragment() {
    private lateinit var binding: DailiesResultsFragmentBinding
    private lateinit var fb: FirebaseFirestore

    override fun getTitle(): String = getString(R.string.results)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DailiesResultsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        const val TAG = "dailies_results_frag"
    }
}