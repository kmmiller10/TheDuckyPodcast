package me.kmmiller.theduckypodcast.core

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class CoreViewModel : ViewModel() {
    var user: FirebaseUser? = null
}
