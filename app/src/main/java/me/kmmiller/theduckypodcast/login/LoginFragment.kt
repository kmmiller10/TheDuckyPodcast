package me.kmmiller.theduckypodcast.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import me.kmmiller.theduckypodcast.BuildConfig
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseActivity
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.databinding.LoginFragmentBinding
import me.kmmiller.theduckypodcast.utils.onTextChangedListener

class LoginFragment : BaseFragment() {
    private lateinit var binding: LoginFragmentBinding

    override fun getTitle(): String = getString(R.string.login)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUiListeners()

        if(BuildConfig.DEBUG) {
            binding.email.setText("kmille1014+21819@gmail.com")
            binding.password.setText("Mobile1.")
        }
    }

    private fun setUiListeners() {
        binding.loginButton.setOnClickListener {
            doLogin()
        }

        binding.email.setOnEditorActionListener { _, action, _ ->
            if(action == EditorInfo.IME_ACTION_NEXT) {
                // Hitting next should go straight to password edit text
                binding.password.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.email.onTextChangedListener {
            binding.loginError.visibility = View.GONE
        }

        binding.password.setOnEditorActionListener { _, action, _ ->
            if(action == EditorInfo.IME_ACTION_DONE) {
                // Hitting done should do login
                doLogin()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.password.onTextChangedListener {
            binding.loginError.visibility = View.GONE
        }

        binding.forgotPassword.setOnClickListener {
            Snackbar.make(binding.root, "Not implemented yet", Snackbar.LENGTH_LONG)
        }

        binding.signUp.setOnClickListener {
            signUp()
        }
    }

    private fun doLogin() {
        (activity as BaseActivity).hideKeyboard()

        showProgress(getString(R.string.logging_in))

        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        when {
            email.isEmpty() -> {
                binding.loginError.text = getString(R.string.email_empty_error)
                binding.loginError.visibility = View.VISIBLE
            }
            password.isEmpty() -> {
                binding.loginError.text = getString(R.string.password_empty_error)
                binding.loginError.visibility = View.VISIBLE
            }
            else -> auth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) (activity as? LoginActivity)?.logIn(getProgress())
                }
                ?.addOnFailureListener {
                    dismissProgress()
                    handleError(it)

                    if(it is FirebaseAuthInvalidUserException || it is FirebaseAuthInvalidCredentialsException) {
                        binding.loginError.text = getString(R.string.login_error)
                        binding.loginError.visibility = View.VISIBLE
                    }
                }
        }
    }

    private fun signUp() {
        pushFragment(SignUpFragment(), true, true, SignUpFragment.TAG)
    }

    companion object {
        const val TAG = "login_fragment"
    }
}