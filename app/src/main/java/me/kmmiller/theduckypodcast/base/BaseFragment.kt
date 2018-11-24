package me.kmmiller.theduckypodcast.base

import android.content.DialogInterface
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import me.kmmiller.theduckypodcast.main.MainViewModel
import java.lang.Exception

abstract class BaseFragment : Fragment() {
    protected var viewModel: MainViewModel? = null
        get() = (activity as? BaseActivity)?.viewModel

    protected var auth: FirebaseAuth? = null
        get() = (activity as? BaseActivity)?.auth

    abstract fun getTitle(): String

    override fun onResume() {
        super.onResume()
        activity?.title = getTitle()
    }

    protected fun pushFragment(frag: Fragment, replace: Boolean, addToBackStack: Boolean, tag: String) {
        (activity as? BaseActivity)?.pushFragment(frag, replace, addToBackStack, tag)
    }

    protected fun pushFragmentSynchronous(frag: Fragment, replace: Boolean, tag: String) {
        (activity as? BaseActivity)?.pushFragmentSynchronous(frag, replace, tag)
    }

    protected fun handleError(e: Exception) {
        (activity as BaseActivity).handleError(e)
    }

    protected fun showAlert(title: String, message: String) {
        (activity as BaseActivity).showAlert(title, message)
    }

    protected fun showAlert(title: String, message: String, positiveListener: DialogInterface.OnClickListener) {
        (activity as BaseActivity).showAlert(title, message, positiveListener)
    }

    protected fun showAlert(title: String,
                            message: String,
                            positiveText: String,
                            positiveListener: DialogInterface.OnClickListener?) {
        (activity as BaseActivity).showAlert(title, message, positiveText, positiveListener)
    }

    protected fun showCancelableAlert(title: String, message: String, positiveListener: DialogInterface.OnClickListener) {
        (activity as BaseActivity).showCancelableAlert(title, message, positiveListener)
    }

    protected fun showCancelableAlert(title: String, message: String, positiveText: String, positiveListener: DialogInterface.OnClickListener) {
        (activity as BaseActivity).showCancelableAlert(title, message, positiveText, positiveListener)
    }

    protected fun showCancelableAlert(title: String,
                            message: String,
                            positiveText: String,
                            positiveListener: DialogInterface.OnClickListener?,
                            cancelText: String,
                            cancelListener: DialogInterface.OnClickListener?) {
        (activity as BaseActivity).showCancelableAlert(title, message, positiveText, positiveListener, cancelText, cancelListener)
    }
}