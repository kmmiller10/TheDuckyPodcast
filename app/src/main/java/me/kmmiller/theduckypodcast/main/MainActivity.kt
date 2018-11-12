package me.kmmiller.theduckypodcast.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseActivity
import me.kmmiller.theduckypodcast.login.LoginActivity

class MainActivity : BaseActivity(), FirebaseAuth.AuthStateListener {
    override var hasBottomNav: Boolean = true
    var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(auth.currentUser == null) {
            logOut()
        } else {
            auth.addAuthStateListener(this)
        }
    }

    override fun navItemSelected(itemId: Int) {
        if(itemId == R.id.nav_home) {
            pushFragment(HomeFragment(), true, false, HomeFragment.TAG)
        } else if(itemId == R.id.nav_survey) {
            pushFragment(SurveyFragment(), true, false, SurveyFragment.TAG)
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

    override fun onBackPressed() {
        setEditableFragment(false)
        when {
            supportFragmentManager.backStackEntryCount >= 1 -> supportFragmentManager.popBackStack()
            currentNavId != R.id.nav_home -> updateSelected(R.id.nav_home)
            else -> super.onBackPressed()
        }
    }

    /**
     * Set to true in onResume of editable frags, otherwise MainActivity will set to false whenever possible
     * i.e. onBackPressed() or finishFragment()
     */
    fun setEditableFragment(isEditable: Boolean) {
        if(isEditable) {
            menu?.findItem(R.id.edit)?.isVisible = true
            menu?.findItem(R.id.save)?.isVisible = false
            menu?.findItem(R.id.cancel)?.isVisible = false
        } else {
            menu?.findItem(R.id.edit)?.isVisible = false
            menu?.findItem(R.id.save)?.isVisible = false
            menu?.findItem(R.id.cancel)?.isVisible = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.edit -> {
                menu?.findItem(R.id.edit)?.isVisible = false
                menu?.findItem(R.id.save)?.isVisible = true
                menu?.findItem(R.id.cancel)?.isVisible = true
                supportFragmentManager.fragments.filter { it is EditableFragment }.forEach { (it as EditableFragment).onEdit() }
                true
            }
            R.id.save -> {
                menu?.findItem(R.id.edit)?.isVisible = true
                menu?.findItem(R.id.save)?.isVisible = false
                menu?.findItem(R.id.cancel)?.isVisible = false
                supportFragmentManager.fragments.filter { it is EditableFragment }.forEach { (it as EditableFragment).onSave() }
                true
            }
            R.id.cancel -> {
                menu?.findItem(R.id.edit)?.isVisible = true
                menu?.findItem(R.id.save)?.isVisible = false
                menu?.findItem(R.id.cancel)?.isVisible = false
                supportFragmentManager.fragments.filter { it is EditableFragment }.forEach { (it as EditableFragment).onCancel() }
                true
            }
            R.id.profile -> {
                pushFragment(ProfileFragment(), true, true, ProfileFragment.TAG)
                true
            }
            R.id.settings -> {
                Log.d("MainActivity", "Settings clicked")
                true
            }
            R.id.log_out -> {
                Log.d("MainActivity", "Log out clicked")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun finishFragment() {
        setEditableFragment(false)
        super.finishFragment()
    }

    override fun onAuthStateChanged(fbAuth: FirebaseAuth) {
        if(fbAuth.currentUser == null) {
            // TODO Reauth
            logOut()
        }
    }
}