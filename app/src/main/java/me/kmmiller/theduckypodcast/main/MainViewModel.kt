package me.kmmiller.theduckypodcast.main

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var seriesId = ""
    var dailyId = ""
    var weeklyId = ""

    fun clear() {
        seriesId = ""
        dailyId = ""
        weeklyId = ""
    }
}
