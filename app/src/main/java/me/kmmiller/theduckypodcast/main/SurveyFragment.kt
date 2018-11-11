package me.kmmiller.theduckypodcast.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.databinding.SurveyFragmentBinding

class SurveyFragment : BaseFragment() {
    private lateinit var binding: SurveyFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SurveyFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        const val TAG = "survey_fragment"
    }
}