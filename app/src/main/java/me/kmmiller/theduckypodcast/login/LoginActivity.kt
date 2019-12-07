package me.kmmiller.theduckypodcast.login

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.Realm
import me.kmmiller.baseui.navigation.BottomNavItemModel
import me.kmmiller.baseui.views.Progress
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseActivity
import me.kmmiller.theduckypodcast.main.MainActivity
import me.kmmiller.theduckypodcast.models.UserModel

class LoginActivity : BaseActivity() {
    private val progress = Progress(this)
    override var hasBottomNav: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(auth.currentUser != null) {
            progress.progress(getString(R.string.logging_in))
            logIn(progress)
        } else {
            // In case the user logged out while not connected to internet, always force logout when pushing the login frag
            auth.signOut()
            pushFragment(LoginFragment(),
                replace = true,
                addToBackStack = false,
                tag = LoginFragment.TAG
            )
        }
    }

    fun logIn(progress: Progress) {
        findOrCreateUser {
            progress.dismiss()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
            startActivity(intent)
        }
    }

    private fun findOrCreateUser(onSuccess: () -> Unit) {
        auth.currentUser?.let {
            getDatabaseUser(it.uid) { model ->
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction { rm ->
                    rm.copyToRealmOrUpdate(model)
                    if(it.email != null && model.email.isBlank()) {
                        model.email = it.email!!
                    }
                }
                realm.close()
                onSuccess.invoke()
            }
        }
    }

    private fun getDatabaseUser(uId: String, onSuccess: (UserModel) -> Unit ) {
        val fb = FirebaseFirestore.getInstance()
        fb.collection("users").document(uId)
            .get()
            .addOnSuccessListener {
                val model = UserModel()
                model.toRealmModel(it)
                onSuccess.invoke(model)
            }
            .addOnFailureListener {
                progress.dismiss()
                handleError(it)
            }
    }

    // Not applicable for this activity
    override fun defaultNavItem(): Int = 0
    override fun getNavItems(): ArrayList<BottomNavItemModel> = ArrayList()
    override fun navItemSelected(itemId: Int) {}
    override fun getHighlightColor(): Int = ContextCompat.getColor(this, R.color.colorPrimary)
}