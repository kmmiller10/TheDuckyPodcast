package me.kmmiller.theduckypodcast.login

import android.content.Intent
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.Realm
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseActivity
import me.kmmiller.theduckypodcast.core.Progress
import me.kmmiller.theduckypodcast.main.MainActivity
import me.kmmiller.theduckypodcast.models.UserModel

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(auth.currentUser != null) {
            val progress = Progress(this)
            progress.progress(getString(R.string.logging_in))
            logIn(progress)
        } else {
            pushFragment(LoginFragment(), true, false, LoginFragment.TAG)
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
    }
}