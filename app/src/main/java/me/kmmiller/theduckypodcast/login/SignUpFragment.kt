package me.kmmiller.theduckypodcast.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import me.kmmiller.baseui.hideKeyboard
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseActivity
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.databinding.SignUpFragmentBinding
import me.kmmiller.theduckypodcast.models.UserModel
import me.kmmiller.theduckypodcast.utils.onTextChangedListener
import me.kmmiller.theduckypodcast.utils.validatePassword

class SignUpFragment : BaseFragment() {
    private lateinit var binding: SignUpFragmentBinding

    override fun getTitle(): String = getString(R.string.sign_up)

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

        binding.email.onTextChangedListener {
            binding.signUpError.visibility = View.GONE
        }

        binding.password.setOnEditorActionListener { _, action, _ ->
            if(action == EditorInfo.IME_ACTION_NEXT) {
                // Hitting next should go to confirm password edit text
                binding.confirmPassword.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.password.onTextChangedListener {
            binding.signUpError.visibility = View.GONE

            if(binding.password.text.toString() == binding.confirmPassword.text.toString()) {
                binding.passwordsDoNotMatch.visibility = View.GONE
            } else if(!binding.confirmPassword.text?.toString().isNullOrEmpty()) {
                // Show error if confirm password field is not empty
                binding.passwordsDoNotMatch.visibility = View.VISIBLE
            }
        }

        binding.confirmPassword.setOnEditorActionListener { _, action, _ ->
            if(action == EditorInfo.IME_ACTION_DONE) {
                // Hitting done should attempt sign up if T&C is accepted
                if(binding.termsAndConditionsCheckbox.isChecked) {
                    clearFocus()
                    signUp()
                } else {
                    clearFocus()
                }
                return@setOnEditorActionListener true
            }
            false
        }

        binding.confirmPassword.onTextChangedListener {
            binding.signUpError.visibility = View.GONE

            if(binding.password.text.toString() != binding.confirmPassword.text.toString()) {
                binding.passwordsDoNotMatch.visibility = View.VISIBLE
            } else {
                binding.passwordsDoNotMatch.visibility = View.GONE
            }
        }

        binding.signUpButton.setOnClickListener {
            signUp()
        }
    }

    private fun clearFocus() {
        requireContext().hideKeyboard()
    }

    private fun validateEmail(): Boolean {
        val email = binding.email.text.toString()
        return when {
            email.isEmpty() -> {
                binding.signUpError.text = getString(R.string.email_empty_error)
                binding.signUpError.visibility = View.VISIBLE
                false
            }
            !email.contains(Regex(".+[@].+")) -> {
                binding.signUpError.text = getString(R.string.email_invalid_error)
                binding.signUpError.visibility = View.VISIBLE
                false
            }
            else -> true
        }
    }

    private fun validatePassword(): Boolean {
        val password = binding.password.text.toString()
        val confirmPassword = binding.confirmPassword.text.toString()

        val errorString = password.validatePassword(requireActivity(), confirmPassword)

        if(errorString.isEmpty()) return true

        if(errorString == getString(R.string.passwords_do_not_match)) {
            binding.passwordsDoNotMatch.visibility = View.VISIBLE
        } else {
            binding.signUpError.visibility = View.VISIBLE
            binding.signUpError.text = errorString
        }

        return false
    }

    private fun signUp() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        when {
            !validateEmail() -> {
                // Error text handled in validateEmail(). Don't need to do anything other than catch validation failure
            }
            !validatePassword() -> {
                // Error text handled in validatePassword(). Don't need to do anything other than catch validation failure
            }
            !binding.termsAndConditionsCheckbox.isChecked -> {
                binding.signUpError.text = getString(R.string.terms_and_conditions_required)
                binding.signUpError.visibility = View.VISIBLE
            }
            else -> {
                binding.signUpError.visibility = View.GONE

                showProgress(getString(R.string.signing_up))

                auth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnSuccessListener {
                        Log.d(TAG, "Successfully registered")

                        if(auth?.currentUser == null) {
                            // Should never reach here, but just in case handle null user so user isn't stuck spinning
                            dismissProgress()
                            showAlert(getString(R.string.error), getString(R.string.error_signing_up))
                        }

                        auth?.currentUser?.let { firebaseUser ->
                            val user = UserModel()
                            user.id = firebaseUser.uid
                            user.email = email

                            fb.collection("users").document(user.id)
                                .set(user.fromRealmModel())
                                .addOnSuccessListener {
                                    Log.d(TAG, "Document successfully created")
                                    (activity as? LoginActivity)?.logIn(getProgress())
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Failed to create document for user")
                                    dismissProgress()
                                    signUpErrorHandler(e)
                                }
                        }
                    }
                    ?.addOnFailureListener {
                        dismissProgress()
                        signUpErrorHandler(it)
                    }
            }
        }
    }

    /**
     * Sign up has special error cases to handle so instead of calling BaseActivity.handleError(), use this method
     */
    private fun signUpErrorHandler(e: Exception) {
        e.printStackTrace()

        val title = getString(R.string.error)
        when (e) {
            is FirebaseNetworkException -> showAlert(title, getString(R.string.error_no_connection))
            is FirebaseAuthUserCollisionException -> showAlert(title, getString(R.string.email_already_exists))
            else -> showAlert(title, getString(R.string.error_signing_up))
        }
    }

    companion object {
        const val TAG = "sign_up_fragment"
    }
}