package me.kmmiller.theduckypodcast.main.optionsmenu

import android.content.pm.PackageManager
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

        try {
            val version = context?.packageManager?.getPackageInfo(requireActivity().packageName, 0)?.versionName
            if(version != null) binding.buildVersion.text = String.format(getString(R.string.build_version), version)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val TAG = "about_fragment"
    }
}