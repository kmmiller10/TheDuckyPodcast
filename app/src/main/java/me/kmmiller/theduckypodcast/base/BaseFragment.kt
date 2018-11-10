package me.kmmiller.theduckypodcast.base

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import me.kmmiller.theduckypodcast.core.CoreViewModel

abstract class BaseFragment : Fragment() {
    protected var viewModel: CoreViewModel? = null
    protected var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as BaseActivity).viewModel
        auth = (activity as BaseActivity).auth
    }

    protected fun pushFragment(frag: Fragment, replace: Boolean, addToBackStack: Boolean, tag: String) {
        (activity as? BaseActivity)?.pushFragment(frag, replace, addToBackStack, tag)
    }

    protected fun showAlert(title: String, message: String) {
        showAlert(title, message, getString(android.R.string.ok), null)
    }

    protected fun showAlert(title: String, message: String, positiveListener: DialogInterface.OnClickListener) {
        showAlert(title, message, getString(android.R.string.ok), positiveListener)
    }

    protected fun showAlert(title: String,
                            message: String,
                            positiveText: String,
                            positiveListener: DialogInterface.OnClickListener?) {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, positiveListener)
                .show()
        }
    }

    protected fun showCancelableAlert(title: String, message: String, positiveListener: DialogInterface.OnClickListener) {
        showCancelableAlert(title, message, getString(android.R.string.ok), positiveListener)
    }

    protected fun showCancelableAlert(title: String, message: String, positiveText: String, positiveListener: DialogInterface.OnClickListener) {
        showCancelableAlert(title, message, positiveText, positiveListener, getString(android.R.string.cancel), null)
    }

    protected fun showCancelableAlert(title: String,
                            message: String,
                            positiveText: String,
                            positiveListener: DialogInterface.OnClickListener?,
                            cancelText: String,
                            cancelListener: DialogInterface.OnClickListener?) {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, positiveListener)
                .setNegativeButton(cancelText, cancelListener)
                .show()
        }
    }
}