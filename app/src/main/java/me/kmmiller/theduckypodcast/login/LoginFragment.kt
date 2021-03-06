package me.kmmiller.theduckypodcast.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import me.kmmiller.baseui.hideKeyboard
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
            binding.email.setText("")
            binding.password.setText("")
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
            val email = binding.email.text.toString()
            pushFragment(ForgotPasswordFragment.getInstance(email), true, true, ForgotPasswordFragment.TAG)
        }

        binding.signUp.setOnClickListener {
            pushFragment(SignUpFragment(), true, true, SignUpFragment.TAG)
        }
    }

    private fun doLogin() {
        requireContext().hideKeyboard()

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

    companion object {
        const val TAG = "login_fragment"
    }
}