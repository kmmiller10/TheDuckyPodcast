package me.kmmiller.theduckypodcast.main

import android.os.Bundle
import me.kmmiller.theduckypodcast.base.BaseFragment

abstract class MainMenuFragment : BaseFragment() {
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