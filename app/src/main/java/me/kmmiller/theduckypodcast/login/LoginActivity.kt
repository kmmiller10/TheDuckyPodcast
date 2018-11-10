package me.kmmiller.theduckypodcast.login

import android.content.Intent
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.Realm
import me.kmmiller.theduckypodcast.base.BaseActivity
import me.kmmiller.theduckypodcast.main.MainActivity
import me.kmmiller.theduckypodcast.models.UserModel

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(auth.currentUser != null)
            logIn()
        else
            pushFragment(LoginFragment(), true, false, LoginFragment.TAG)
    }

    fun logIn() {
        findOrCreateUser()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK )
        startActivity(intent)
    }

    private fun findOrCreateUser() {
        auth.currentUser?.let {
            getDatabaseUser(it.uid) { model ->
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction { rm ->
                    rm.copyToRealmOrUpdate(model)
                }
                realm.close()
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