package me.kmmiller.theduckypodcast.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.ui.BottomNavAdapter
import me.kmmiller.theduckypodcast.base.ui.BottomNavItemModel
import me.kmmiller.theduckypodcast.base.ui.BottomNavRecyclerView
import me.kmmiller.theduckypodcast.core.CoreApplication
import me.kmmiller.theduckypodcast.core.CoreViewModel
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

abstract class BaseActivity : AppCompatActivity(), BottomNavAdapter.BottomNavAdapterListener {
    lateinit var viewModel: CoreViewModel
    lateinit var auth: FirebaseAuth

    private lateinit var bottomNavRecyclerView: BottomNavRecyclerView
    abstract var hasBottomNav: Boolean
    protected var currentNavId = 0

    private val navItems = ArrayList<BottomNavItemModel>()
    private var adapter: BottomNavAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CoreViewModel::class.java)
        auth = (application as CoreApplication).getFirebaseAuthInstance()

        setContentView(R.layout.base_activity)
        bottomNavRecyclerView = findViewById(R.id.bottom_nav)
        updateNav()

        KeyboardVisibilityEvent.setEventListener(this) { isOpen ->
            if(hasBottomNav) {
                bottomNavRecyclerView.visibility = if(isOpen) View.GONE else View.VISIBLE
            }
        }

        savedInstanceState?.let {
            currentNavId = it.getInt(NAV_ID, R.id.nav_home)
        }

        if(savedInstanceState == null && hasBottomNav) {
            // Select home initially
            onNavItemSelected(R.id.nav_home)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(NAV_ID, currentNavId)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount >= 1) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    private fun updateNav() {
        if(hasBottomNav) {
            bottomNavRecyclerView.visibility = View.VISIBLE
            setupItems()
            adapter = BottomNavAdapter(R.id.nav_home, navItems)
            adapter?.let {
                bottomNavRecyclerView.setRecyclerViewAdapter(it)
            }
        } else {
            bottomNavRecyclerView.visibility = View.GONE
        }
    }

    private fun setupItems() {
        val home = BottomNavItemModel(R.id.nav_home, R.drawable.ic_home_active, R.drawable.ic_home_inactive, R.string.home)
        navItems.add(home)
        val survey = BottomNavItemModel(R.id.nav_survey, R.drawable.ic_question_answer_active,  R.drawable.ic_question_answer_inactive, R.string.survey)
        navItems.add(survey)
    }

    override fun onNavItemSelected(itemId: Int) {
        currentNavId = itemId
        navItemSelected(itemId)
        adapter?.notifyDataSetChanged()
    }

    protected fun updateSelected(itemId: Int) {
        adapter?.updateSelected(this, itemId)
    }

    protected abstract fun navItemSelected(itemId: Int)

    fun pushFragment(frag: Fragment, replace: Boolean, addToBackStack: Boolean, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()

        if(replace)
            transaction.replace(getFragContainerId(), frag, tag)
        else
            transaction.add(getFragContainerId(), frag, tag)

        if(addToBackStack) transaction.addToBackStack(tag)

        transaction.commit()
    }

    open fun finishFragment() {
        supportFragmentManager?.let {
            if(it.backStackEntryCount >= 1) it.popBackStack()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Deliver results to fragments in case anything needs to be handled by the frag
        supportFragmentManager?.fragments?.forEach {
            if(it is BaseFragment) {
                it.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    /**
     * Override this method if the fragment container to push fragment in is nested or in a custom view.
     * Otherwise use default container
     */
    @IdRes
    protected fun getFragContainerId(): Int = R.id.fragment_container

    companion object {
        const val NAV_ID = "nav_id"
    }
}
