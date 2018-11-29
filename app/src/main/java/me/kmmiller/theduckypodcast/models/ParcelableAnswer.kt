package me.kmmiller.theduckypodcast.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ParcelableAnswer(val answerPosition: Int, val otherInput: String?): Parcelable