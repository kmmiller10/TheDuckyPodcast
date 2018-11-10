package me.kmmiller.theduckypodcast.base

import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import me.kmmiller.theduckypodcast.R

abstract class BaseActivity : AppCompatActivity() {
    lateinit var viewModel: BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BaseViewModel::class.java)
        setContentView(R.layout.base_activity)
    }

    fun pushFragment(frag: Fragment, replace: Boolean, addToBackStack: Boolean, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()

        if(replace)
            transaction.replace(getFragContainerId(), frag, tag)
        else
            transaction.add(getFragContainerId(), frag, tag)

        if(addToBackStack) transaction.addToBackStack(tag)

        transaction.commit()
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

    /**
     * Override this method if the fragment container to push fragment in is nested or in a custom view.
     * Otherwise use default container
     */
    @IdRes
    protected fun getFragContainerId(): Int {
        return R.id.fragment_container
    }
}
