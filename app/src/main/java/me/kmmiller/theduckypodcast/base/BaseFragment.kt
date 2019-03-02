package me.kmmiller.theduckypodcast.base

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.Realm
import me.kmmiller.theduckypodcast.main.MainViewModel
import me.kmmiller.theduckypodcast.main.interfaces.ICancel
import me.kmmiller.theduckypodcast.main.interfaces.ReturnToFragListener
import me.kmmiller.theduckypodcast.utils.Progress
import java.lang.Exception

abstract class BaseFragment : Fragment(), ICancel {
    protected var viewModel: MainViewModel? = null
        get() = (activity as? BaseActivity)?.viewModel

    protected var auth: FirebaseAuth? = null
        get() = (activity as? BaseActivity)?.auth

    private lateinit var progress: Progress

    protected lateinit var fb: FirebaseFirestore
    protected var realm: Realm? = null

    abstract fun getTitle(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
        fb = FirebaseFirestore.getInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            progress = Progress(it as BaseActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.title = getTitle()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm?.close()
        realm = null
    }

    protected fun pushFragment(frag: Fragment, replace: Boolean, addToBackStack: Boolean, tag: String) {
        (activity as? BaseActivity)?.pushFragment(frag, replace, addToBackStack, tag)
    }

    protected fun pushFragmentSynchronous(frag: Fragment, replace: Boolean, tag: String) {
        (activity as? BaseActivity)?.pushFragmentSynchronous(frag, replace, tag)
    }

    protected fun handleError(e: Exception) {
        (activity as? BaseActivity)?.handleError(e)
    }

    protected fun showAlert(title: String, message: String) {
        (activity as? BaseActivity)?.showAlert(title, message)
    }

    protected fun showAlert(title: String, message: String, positiveListener: DialogInterface.OnClickListener) {
        (activity as? BaseActivity)?.showAlert(title, message, positiveListener)
    }

    protected fun showAlert(title: String,
                            message: String,
                            positiveText: String,
                            positiveListener: DialogInterface.OnClickListener?) {
        (activity as? BaseActivity)?.showAlert(title, message, positiveText, positiveListener)
    }

    protected fun showCancelableAlert(title: String, message: String, positiveListener: DialogInterface.OnClickListener) {
        (activity as? BaseActivity)?.showCancelableAlert(title, message, positiveListener)
    }

    protected fun showCancelableAlert(title: String, message: String, positiveText: String, positiveListener: DialogInterface.OnClickListener) {
        (activity as? BaseActivity)?.showCancelableAlert(title, message, positiveText, positiveListener)
    }

    protected fun showCancelableAlert(title: String,
                            message: String,
                            positiveText: String,
                            positiveListener: DialogInterface.OnClickListener?,
                            cancelText: String,
                            cancelListener: DialogInterface.OnClickListener?) {
        (activity as? BaseActivity)?.showCancelableAlert(title, message, positiveText, positiveListener, cancelText, cancelListener)
    }

    protected fun getProgress(): Progress {
        return progress
    }

    protected fun showProgress(message: String) {
        if(context != null && !isDetached && !isRemoving) progress.progress(message)
    }

    protected fun showCancelableProgress(message: String) {
        if(context != null && !isDetached && !isRemoving) progress.progress(message, this)
    }

    protected fun showCancelableProgress(message: String, canceler: ICancel) {
        if(context != null && !isDetached && !isRemoving) progress.progress(message, canceler)
    }

    protected fun dismissProgress() {
        if(context != null && !isDetached && !isRemoving) progress.dismiss()
    }

    override fun onCancel() {
        // If anything needs to be done when a progress spinner is canceled, override this method in the inheriting class
    }
}