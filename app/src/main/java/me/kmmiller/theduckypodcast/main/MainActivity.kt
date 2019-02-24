package me.kmmiller.theduckypodcast.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import io.realm.Realm
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseActivity
import me.kmmiller.theduckypodcast.base.ui.BottomNavItemModel
import me.kmmiller.theduckypodcast.login.LoginActivity
import me.kmmiller.theduckypodcast.main.interfaces.NavItem
import me.kmmiller.theduckypodcast.main.interfaces.ReturnToFragListener
import me.kmmiller.theduckypodcast.main.optionsmenu.AboutFragment
import me.kmmiller.theduckypodcast.main.optionsmenu.AttributionsListFragment
import me.kmmiller.theduckypodcast.main.optionsmenu.ProfileFragment
import me.kmmiller.theduckypodcast.main.optionsmenu.SettingsFragment
import me.kmmiller.theduckypodcast.models.*

class MainActivity : BaseActivity(), FirebaseAuth.AuthStateListener {
    override var hasBottomNav: Boolean = true
    private var menuItemToggle: Pair<Int, Boolean>? = null

    override fun firstNavItem(): Int = R.id.nav_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(auth.currentUser == null) {
            logOut()
        } else {
            auth.addAuthStateListener(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.removeAuthStateListener(this)
    }

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
            R.id.nav_dailies -> {
                if(checkAnsweredStatus())
                    pushFragmentSynchronous(DailiesResultsFragment(), true, DailiesResultsFragment.TAG)
                else
                    pushFragmentSynchronous (DailiesFragment(), true, DailiesFragment.TAG)
            }
            R.id.nav_weeklies -> pushFragmentSynchronous(WeekliesFragment(), true, WeekliesFragment.TAG)
        }
    }

    /**
     * Overriding so nav fragment can be pushed if coming from a non-NavItem fragment
     */
    override fun onNavItemSelected(itemId: Int) {
        val frag = supportFragmentManager.fragments.firstOrNull()
        if(frag != null && frag !is NavItem && itemId == currentNavId) navItemSelected(itemId)
        super.onNavItemSelected(itemId)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.fragments.firstOrNull()
        val pushNavFrag = frag != null && frag !is NavItem
        when {
            supportFragmentManager.backStackEntryCount > 0 -> {
                supportFragmentManager.popBackStack()
                supportFragmentManager.fragments
                    .filter { it is ReturnToFragListener}
                    .forEach { (it as ReturnToFragListener).onReturnToFrag() }
            }
            pushNavFrag -> navItemSelected(currentNavId)
            currentNavId != R.id.nav_home -> updateSelected(R.id.nav_home)
            else -> super.onBackPressed()
        }
    }

    private fun checkAnsweredStatus(): Boolean {
        val realm = Realm.getDefaultInstance()
        val dailyModel = realm?.findDailiesModel(viewModel.dailyId)
        if(dailyModel != null && dailyModel.isSubmitted) {
            return true
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        menuItemToggle?.let { toggle ->
            val itemId = toggle.first
            val visibility = toggle.second
            menu.findItem(itemId).isVisible = visibility
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.profile -> {
                pushFragmentSynchronous(ProfileFragment(), true, ProfileFragment.TAG)
                true
            }
            R.id.settings -> {
                pushFragmentSynchronous(SettingsFragment(), true, SettingsFragment.TAG)
                true
            }
            R.id.about -> {
                pushFragmentSynchronous(AboutFragment(), true, AboutFragment.TAG)
                true
            }
            R.id.attributions -> {
                pushFragmentSynchronous(AttributionsListFragment(), true, AttributionsListFragment.TAG)
                true
            }
            R.id.log_out -> {
                logOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun hideMenuItem(itemId: Int) {
        menuItemToggle = Pair(itemId, false)
        invalidateOptionsMenu()
    }

    fun showMenuItem(itemId: Int) {
        menuItemToggle = Pair(itemId, true)
        invalidateOptionsMenu()
    }

    override fun onAuthStateChanged(fbAuth: FirebaseAuth) {
        // Detects firebase changes to login status and logs out if no longer logged into fb
        if(fbAuth.currentUser == null) logOut()
    }

    fun logOut() {
        // Sign out of firebase
        auth.signOut()
        auth.removeAuthStateListener(this)

        // Clear realm
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            realm.findAllSeries().deleteAllFromRealm()
            realm.findAllDailies().deleteAllFromRealm()
            realm.findAllWeeklies().deleteAllFromRealm()
            realm.findAllUsers().deleteAllFromRealm()
        }
        realm.close()

        // Clear view model
        viewModel.clear()

        // Go to login activity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
        startActivity(intent)
    }

    companion object {
        const val TAG = "MainActivity"
    }
}