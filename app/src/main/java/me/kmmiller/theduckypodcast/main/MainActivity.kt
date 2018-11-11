package me.kmmiller.theduckypodcast.main

import android.content.Intent
import android.os.Bundle
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseActivity
import me.kmmiller.theduckypodcast.login.LoginActivity

class MainActivity : BaseActivity() {
    override var hasBottomNav: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(auth.currentUser == null) {
            logOut()
        }
    }

    override fun navItemSelected(itemId: Int) {
        if(itemId == R.id.nav_home) {
            pushFragment(HomeFragment(), true, false, HomeFragment.TAG)
        } else if(itemId == R.id.nav_survey) {
            pushFragment(SurveyFragment(), true, false, SurveyFragment.TAG)
        }
    }

    override fun onBackPressed() {
        if(currentNavId != R.id.nav_home) {
            updateSelected(R.id.nav_home)
        } else {
            super.onBackPressed()
        }
    }

    fun logOut() {
        auth.signOut()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
        startActivity(intent)
    }
}