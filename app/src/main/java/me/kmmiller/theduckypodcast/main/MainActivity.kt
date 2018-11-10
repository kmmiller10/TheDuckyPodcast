package me.kmmiller.theduckypodcast.main

import android.content.Intent
import android.os.Bundle
import me.kmmiller.theduckypodcast.base.BaseActivity
import me.kmmiller.theduckypodcast.login.LoginActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pushFragment(HomeFragment(), true, false, HomeFragment.TAG)
    }

    fun logOut() {
        auth.signOut()
        viewModel.user = null

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
        startActivity(intent)
    }
}