package me.kmmiller.theduckypodcast.login

import android.os.Bundle
import me.kmmiller.theduckypodcast.base.BaseActivity

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth.signOut()
        pushFragment(LoginFragment(), true, false, LoginFragment.TAG)
    }
}