package me.kmmiller.theduckypodcast.main.optionsmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.databinding.AboutFragmentBinding

class AboutFragment : BaseMenuFragment() {
    private lateinit var binding: AboutFragmentBinding

    override fun getTitle(): String = getString(R.string.about)
    override fun getItemId(): Int = R.id.about

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = AboutFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.attributions.setOnClickListener {
            pushFragment(AttributionsFragment(), true, false, AttributionsFragment.TAG)
        }
    }

    companion object {
        const val TAG = "about_fragment"
    }
}