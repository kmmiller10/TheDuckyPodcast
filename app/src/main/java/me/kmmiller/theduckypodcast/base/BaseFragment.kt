package me.kmmiller.theduckypodcast.base

import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.Realm
import me.kmmiller.baseui.KmmBaseFragment
import me.kmmiller.theduckypodcast.main.MainViewModel
import me.kmmiller.theduckypodcast.main.interfaces.ICancel

abstract class BaseFragment : KmmBaseFragment(), ICancel {
    protected val viewModel: MainViewModel?
        get() = (activity as? BaseActivity)?.viewModel

    protected val auth: FirebaseAuth?
        get() = (activity as? BaseActivity)?.auth

    protected lateinit var fb: FirebaseFirestore
    protected var realm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
        fb = FirebaseFirestore.getInstance()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm?.close()
        realm = null
    }
}