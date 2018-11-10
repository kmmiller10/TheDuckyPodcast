package me.kmmiller.theduckypodcast.base

import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    protected var viewModel: BaseViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as BaseActivity).viewModel
    }

    protected fun pushFragment(frag: Fragment, replace:Boolean, addToBackStack: Boolean, tag: String) {
        (activity as? BaseActivity)?.pushFragment(frag, replace, addToBackStack, tag)
    }
}