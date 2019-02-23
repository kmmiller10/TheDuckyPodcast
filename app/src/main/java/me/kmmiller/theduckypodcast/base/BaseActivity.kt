package me.kmmiller.theduckypodcast.base

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.ui.BottomNavAdapter
import me.kmmiller.theduckypodcast.base.ui.BottomNavItemModel
import me.kmmiller.theduckypodcast.base.ui.BottomNavRecyclerView
import me.kmmiller.theduckypodcast.core.CoreApplication
import me.kmmiller.theduckypodcast.main.MainViewModel
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import android.app.Activity
import android.view.inputmethod.InputMethodManager
import com.google.firebase.firestore.FirebaseFirestoreException


abstract class BaseActivity : AppCompatActivity(), BottomNavAdapter.BottomNavAdapterListener {
    lateinit var viewModel: MainViewModel
    lateinit var auth: FirebaseAuth

    private lateinit var bottomNavRecyclerView: BottomNavRecyclerView
    abstract var hasBottomNav: Boolean
    protected var currentNavId = 0

    private val navItems = ArrayList<BottomNavItemModel>()
    private var adapter: BottomNavAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
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
            adapter?.updateSelected(this, currentNavId)
            adapter?.notifyDataSetChanged()
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
            navItems.addAll(getNavItems())
            adapter = BottomNavAdapter(firstNavItem(), navItems)
            adapter?.let {
                bottomNavRecyclerView.setRecyclerViewAdapter(it)
            }
        } else {
            bottomNavRecyclerView.visibility = View.GONE
        }
    }

    abstract fun firstNavItem(): Int
    abstract fun getNavItems(): ArrayList<BottomNavItemModel>

    override fun onNavItemSelected(itemId: Int) {
        if(currentNavId != itemId) {
            currentNavId = itemId
            adapter?.notifyDataSetChanged()
            navItemSelected(itemId)
        }
    }

    protected abstract fun navItemSelected(itemId: Int)

    protected fun updateSelected(itemId: Int) {
        adapter?.updateSelected(this, itemId)
    }

    fun hideKeyboard() {
        //https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Override this method if the fragment container to push fragment in is nested or in a custom view.
     * Otherwise use default container
     */
    @IdRes
    protected fun getFragContainerId(): Int = R.id.fragment_container

    fun pushFragment(frag: Fragment, replace: Boolean, addToBackStack: Boolean, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()

        if(replace)
            transaction.replace(getFragContainerId(), frag, tag)
        else
            transaction.add(getFragContainerId(), frag, tag)

        if(addToBackStack) transaction.addToBackStack(tag)

        transaction.commit()
    }

    /**
     * Synchronously pushes a fragment, i.e. the transaction is committed immediately. addToBackStack cannot be used
     * when committing synchronously
     */
    fun pushFragmentSynchronous(frag: Fragment, replace: Boolean, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()

        if(replace)
            transaction.replace(getFragContainerId(), frag, tag)
        else
            transaction.add(getFragContainerId(), frag, tag)

        transaction.commitNow()
    }

    fun handleError(e: Exception) {
        e.printStackTrace()

        val title = getString(R.string.error)
        if(e is FirebaseNetworkException) {
            showAlert(title, getString(R.string.error_no_connection))
        } else if(e is FirebaseAuthInvalidUserException || e is FirebaseAuthInvalidCredentialsException) {
            showAlert(title, getString(R.string.error_logging_in))
        } else if(e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
            showAlert(title, getString(R.string.error_submission))
        }
    }

    fun showAlert(title: String, message: String) {
        showAlert(title, message, getString(android.R.string.ok), null)
    }

    fun showAlert(title: String, message: String, positiveListener: DialogInterface.OnClickListener) {
        showAlert(title, message, getString(android.R.string.ok), positiveListener)
    }

    fun showAlert(title: String,
                            message: String,
                            positiveText: String,
                            positiveListener: DialogInterface.OnClickListener?) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText, positiveListener)
            .show()
    }

    fun showCancelableAlert(title: String, message: String, positiveListener: DialogInterface.OnClickListener) {
        showCancelableAlert(title, message, getString(android.R.string.ok), positiveListener)
    }

    fun showCancelableAlert(title: String, message: String, positiveText: String, positiveListener: DialogInterface.OnClickListener) {
        showCancelableAlert(title, message, positiveText, positiveListener, getString(android.R.string.cancel), null)
    }

    fun showCancelableAlert(title: String,
                                      message: String,
                                      positiveText: String,
                                      positiveListener: DialogInterface.OnClickListener?,
                                      cancelText: String,
                                      cancelListener: DialogInterface.OnClickListener?) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText, positiveListener)
            .setNegativeButton(cancelText, cancelListener)
            .show()
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

    companion object {
        const val NAV_ID = "nav_id"
    }
}
