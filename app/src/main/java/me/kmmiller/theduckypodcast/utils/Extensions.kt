package me.kmmiller.theduckypodcast.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
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

fun AppCompatEditText.onTextChangedListener(onTextChanged: (e: Editable?) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(e: Editable?) {
            onTextChanged.invoke(e)
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
    })
}