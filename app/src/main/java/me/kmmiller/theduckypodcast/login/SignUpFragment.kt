package me.kmmiller.theduckypodcast.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.google.firebase.firestore.FirebaseFirestore
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.databinding.SignUpFragmentBinding
import me.kmmiller.theduckypodcast.models.UserModel

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

        binding.password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.signUpError.visibility = View.GONE

                if(binding.password.text.toString() == binding.confirmPassword.text.toString()) {
                    binding.passwordsDoNotMatch.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        binding.password.setOnKeyListener { _, _, _ ->
            binding.signUpError.visibility = View.GONE
            false
        }

        binding.confirmPassword.setOnEditorActionListener { _, action, _ ->
            if(action == EditorInfo.IME_ACTION_DONE) {
                // Hitting done should attempt sign up if T&C is accepted
                if(binding.termsAndConditionsCheckbox.isChecked)
                    signUp()
                else
                    binding.termsAndConditionsCheckbox.clearFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.confirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                binding.signUpError.visibility = View.GONE

                if(binding.password.text.toString() != binding.confirmPassword.text.toString()) {
                    binding.passwordsDoNotMatch.visibility = View.VISIBLE
                } else {
                    binding.passwordsDoNotMatch.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

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
            else -> {
                binding.signUpError.visibility = View.GONE

                auth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener {
                        Log.d(TAG, "Successfully registered")

                        auth?.currentUser?.let { firebaseUser ->
                            val user = UserModel()
                            user.id = firebaseUser.uid

                            val fb = FirebaseFirestore.getInstance()
                            fb.collection("users").document(user.id)
                                .set(user.fromRealmModel())
                                .addOnSuccessListener {
                                    Log.d(TAG, "Document successfully created")
                                    (activity as? LoginActivity)?.logIn()
                                }
                                .addOnFailureListener { e ->
                                    e.printStackTrace()
                                    Log.e(TAG, "Failed to create document for user")
                                }
                        }
                    }
                    ?.addOnFailureListener {
                        it.printStackTrace()
                        showAlert(getString(R.string.error), getString(R.string.error_signing_up))
                    }
            }
        }
    }

    companion object {
        const val TAG = "sign_up_fragment"
    }
}