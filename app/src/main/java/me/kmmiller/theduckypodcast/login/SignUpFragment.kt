package me.kmmiller.theduckypodcast.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.databinding.SignUpFragmentBinding

class SignUpFragment : BaseFragment() {
    private lateinit var binding: SignUpFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SignUpFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUiListeners()
    }

    private fun setUiListeners() {

        binding.email.setOnEditorActionListener { _, action, _ ->
            if(action == EditorInfo.IME_ACTION_NEXT) {
                // Hitting next should go straight to password edit text
                binding.password.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.email.setOnKeyListener { _, _, _ ->
            binding.signUpError.visibility = View.GONE
            false
        }

        binding.password.setOnEditorActionListener { _, action, _ ->
            if(action == EditorInfo.IME_ACTION_NEXT) {
                // Hitting next should go to confirm password edit text
                binding.confirmPassword.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.password.setOnKeyListener { _, _, _ ->
            binding.signUpError.visibility = View.GONE
            false
        }

        binding.confirmPassword.setOnEditorActionListener { _, action, _ ->
            if(action == EditorInfo.IME_ACTION_DONE) {
                // Hitting done should attempt sign up
                signUp()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.confirmPassword.setOnKeyListener { _, _, _ ->
            binding.signUpError.visibility = View.GONE

            if(binding.password.text.toString() != binding.confirmPassword.text.toString()) {
                binding.passwordsDoNotMatch.visibility = View.VISIBLE
            } else {
                binding.passwordsDoNotMatch.visibility = View.GONE
            }
            false
        }

        binding.signUpButton.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val confirmPassword = binding.confirmPassword.text.toString()

        when {
            email.isEmpty() -> {
                binding.signUpError.text = getString(R.string.email_empty_error)
                binding.signUpError.visibility = View.VISIBLE
            }
            password != confirmPassword -> binding.passwordsDoNotMatch.visibility = View.VISIBLE
            password.length < 7 -> {
                binding.signUpError.text = getString(R.string.password_minimum_characters)
                binding.signUpError.visibility = View.VISIBLE
            }
            !binding.termsAndConditionsCheckbox.isChecked -> {
                binding.signUpError.text = getString(R.string.terms_and_conditions_required)
                binding.signUpError.visibility = View.VISIBLE
            }
            else -> auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener {
                    Log.d(TAG, "Successfully registered")
                    viewModel?.user = auth?.currentUser

                    (activity as? LoginActivity)?.logIn()
                }
                ?.addOnFailureListener {
                    it.printStackTrace()
                    showAlert(getString(R.string.error), getString(R.string.error_signing_up))
                }
        }
    }

    companion object {
        const val TAG = "sign_up_fragment"
    }
}