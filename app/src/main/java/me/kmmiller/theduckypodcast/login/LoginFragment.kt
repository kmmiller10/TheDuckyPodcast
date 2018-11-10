package me.kmmiller.theduckypodcast.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.databinding.LoginFragmentBinding

class LoginFragment : BaseFragment() {
    private lateinit var binding: LoginFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUiListeners()
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

        binding.email.setOnKeyListener { _, _, _ ->
            binding.loginError.visibility = View.GONE
            false
        }

        binding.password.setOnEditorActionListener { _, action, _ ->
            if(action == EditorInfo.IME_ACTION_DONE) {
                // Hitting done should do login
                doLogin()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.password.setOnKeyListener { _, _, _ ->
            binding.loginError.visibility = View.GONE
            false
        }

        binding.signUp.setOnClickListener {
            signUp()
        }
    }

    private fun doLogin() {
        binding.email.clearFocus()
        binding.password.clearFocus()

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
                    if (it.isSuccessful) {
                        Log.d(TAG, "Successfully logged in")
                        viewModel?.user = auth?.currentUser

                    } else {
                        Log.d(TAG, "Login failed")
                        binding.loginError.text = getString(R.string.login_error)
                        binding.loginError.visibility = View.VISIBLE
                    }
                }
                ?.addOnFailureListener {
                    it.printStackTrace()
                    showAlert(getString(R.string.error), getString(R.string.error_logging_in))
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