package me.kmmiller.theduckypodcast.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseActivity
import me.kmmiller.theduckypodcast.base.ui.BottomNavItemModel
import me.kmmiller.theduckypodcast.login.LoginActivity

class MainActivity : BaseActivity(), FirebaseAuth.AuthStateListener {
    override var hasBottomNav: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(auth.currentUser == null) {
            logOut()
        } else {
            auth.addAuthStateListener(this)
        }
    }

    override fun firstNavItem(): Int = R.id.nav_home

    override fun getNavItems(): ArrayList<BottomNavItemModel> {
        val navItems = ArrayList<BottomNavItemModel>()
        val home = BottomNavItemModel(R.id.nav_home, R.drawable.ic_home_active, R.drawable.ic_home_inactive, R.string.home)
        navItems.add(home)
        val dailies = BottomNavItemModel(R.id.nav_dailies, R.drawable.ic_dailies_active,  R.drawable.ic_dailies_inactive, R.string.dailies)
        navItems.add(dailies)
        val weeklies = BottomNavItemModel(R.id.nav_weeklies, R.drawable.ic_weeklies_active,  R.drawable.ic_weeklies_inactive, R.string.weeklies)
        navItems.add(weeklies)
        return navItems
    }

    override fun navItemSelected(itemId: Int) {
        when (itemId) {
            R.id.nav_home -> pushFragmentSynchronous(HomeFragment(), true, HomeFragment.TAG)
            R.id.nav_dailies -> pushFragmentSynchronous(DailiesFragment(), true, DailiesFragment.TAG)
            R.id.nav_weeklies -> pushFragmentSynchronous(DailiesFragment(), true, DailiesFragment.TAG)
        }
    }

    /**
     * Overriding so nav fragment can be pushed if coming from a non-NavItem fragment
     */
    override fun onNavItemSelected(itemId: Int) {
        val frag = supportFragmentManager.fragments.firstOrNull()
        if(frag != null && frag !is NavItem && itemId == currentNavId) {
            navItemSelected(itemId)
        }
        super.onNavItemSelected(itemId)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.fragments.firstOrNull()
        val pushNavFrag = frag != null && frag !is NavItem
        when {
            pushNavFrag -> navItemSelected(currentNavId)
            currentNavId != R.id.nav_home -> updateSelected(R.id.nav_home)
            else -> super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.profile -> {
                pushFragmentSynchronous(ProfileFragment(), true, ProfileFragment.TAG)
                true
            }
            R.id.settings -> {
                Log.d("MainActivity", "Settings clicked")
                true
            }
            R.id.log_out -> {
                logOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onAuthStateChanged(fbAuth: FirebaseAuth) {
        if(fbAuth.currentUser == null) {
            // TODO Reauth
            logOut()
        }
    }

    fun logOut() {
        auth.signOut()
        auth.removeAuthStateListener(this)

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.removeAuthStateListener(this)
    }
}