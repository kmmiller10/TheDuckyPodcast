package me.kmmiller.theduckypodcast.core

import com.google.firebase.firestore.DocumentSnapshot
import java.lang.Exception

fun DocumentSnapshot?.getStringArrayList(element: String) : ArrayList<String> {
    val ary = ArrayList<String>()

    this?.apply {
        this[element]?.let {
            return try {
                ary.addAll(it as ArrayList<String>)
                ary
            } catch(e: Exception) {
                ary
            }
        }
    }

    return ary
}