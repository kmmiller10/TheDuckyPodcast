package me.kmmiller.theduckypodcast.main.optionsmenu

import android.os.Bundle
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.main.MainActivity

abstract class BaseMenuFragment : BaseFragment() {
    abstract fun getItemId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide this option from the menu while viewing it
        (activity as MainActivity).hideMenuItem(getItemId())
    }

    override fun onDestroy() {
        super.onDestroy()
        // Show the option again when the frag is destroyed
        (activity as MainActivity).showMenuItem(getItemId())
    }
}