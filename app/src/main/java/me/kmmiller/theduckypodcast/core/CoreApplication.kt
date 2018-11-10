package me.kmmiller.theduckypodcast.core

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class CoreApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

    fun getFirebaseAuthInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}