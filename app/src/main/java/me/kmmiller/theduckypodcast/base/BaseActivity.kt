package me.kmmiller.theduckypodcast.base

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestoreException
import me.kmmiller.baseui.KmmBaseActivity
import me.kmmiller.theduckypodcast.R

import me.kmmiller.theduckypodcast.main.MainViewModel


abstract class BaseActivity : KmmBaseActivity(){
    lateinit var viewModel: MainViewModel
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        auth = (application as BaseApplication).getFirebaseAuthInstance()
    }

    override fun handleError(e: Exception) {
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
}
