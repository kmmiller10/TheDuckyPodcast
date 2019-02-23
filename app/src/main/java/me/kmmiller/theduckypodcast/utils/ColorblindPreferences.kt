package me.kmmiller.theduckypodcast.utils

import android.content.Context
import android.preference.PreferenceManager
import me.kmmiller.theduckypodcast.R

object ColorblindPreferences {
    private const val COLORBLIND_PREF = "colorblind_pref"

    fun getCbMode(context: Context): ColorblindMode {
        val intValue = PreferenceManager.getDefaultSharedPreferences(context).getInt(COLORBLIND_PREF, 0)
        return ColorblindMode.values().firstOrNull { it.value == intValue } ?: ColorblindPreferences.ColorblindMode.OFF
    }

    fun setCbMode(context: Context, colorblindMode: ColorblindMode) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(COLORBLIND_PREF, colorblindMode.value).apply()
    }

    fun getResId(cbMode: ColorblindMode): Int {
        return when(cbMode) {
            ColorblindPreferences.ColorblindMode.OFF -> -1
            ColorblindPreferences.ColorblindMode.DEUTERANOMALY -> R.id.deut
            ColorblindPreferences.ColorblindMode.PROTANOMALY -> R.id.prot
            ColorblindPreferences.ColorblindMode.TRITANOMOLY -> R.id.trit
        }
    }

    enum class ColorblindMode(val value: Int) {
        OFF(0),
        DEUTERANOMALY(1),
        PROTANOMALY(2),
        TRITANOMOLY(3);
    }
}