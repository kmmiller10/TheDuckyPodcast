package me.kmmiller.theduckypodcast.main.optionsmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.databinding.SettingsFragmentBinding
import me.kmmiller.theduckypodcast.utils.ColorblindPreferences
import me.kmmiller.theduckypodcast.utils.ColorblindPreferences.ColorblindMode

class SettingsFragment : BaseMenuFragment() {
    private lateinit var binding: SettingsFragmentBinding

    override fun getItemId(): Int = R.id.settings
    override fun getTitle(): String = getString(R.string.settings)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the initial colorblind mode settings
        val colorblindMode = ColorblindPreferences.getCbMode(requireActivity())
        binding.cbSwitch.isChecked = colorblindMode != ColorblindMode.OFF
        binding.cbGroup.visibility = if(colorblindMode != ColorblindMode.OFF) View.VISIBLE else View.GONE
        if(colorblindMode != ColorblindMode.OFF) binding.cbGroup.check(ColorblindPreferences.getResId(colorblindMode))

        // Listen for changes to the colorblind mode switch
        binding.cbSwitch.setOnCheckedChangeListener { _, isChecked ->
            try {
                if(isChecked) {
                    binding.cbGroup.visibility = View.VISIBLE
                    binding.cbGroup.check(R.id.deut)
                    ColorblindPreferences.setCbMode(requireActivity(), ColorblindMode.DEUTERANOMALY)
                } else {
                    binding.cbGroup.visibility = View.GONE
                    ColorblindPreferences.setCbMode(requireActivity(), ColorblindPreferences.ColorblindMode.OFF)
                }

            } catch(e: Exception) {
                // Probably no longer attached to the activity
                e.printStackTrace()
            }
        }

        binding.cbGroup.setOnCheckedChangeListener { _, resId ->
            try {
                when (resId) {
                    R.id.deut -> ColorblindPreferences.setCbMode(requireActivity(), ColorblindMode.DEUTERANOMALY)
                    R.id.prot -> ColorblindPreferences.setCbMode(requireActivity(), ColorblindMode.PROTANOMALY)
                    R.id.trit -> ColorblindPreferences.setCbMode(requireActivity(), ColorblindMode.TRITANOMOLY)
                }
            } catch(e: Exception) {
                // Probably no longer attached to the activity
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val TAG = "settings_frag"
    }
}