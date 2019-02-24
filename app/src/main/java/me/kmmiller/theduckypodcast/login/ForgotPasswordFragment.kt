package me.kmmiller.theduckypodcast.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.databinding.ForgotPasswordFragmentBinding
import me.kmmiller.theduckypodcast.utils.nonNullString

class ForgotPasswordFragment : BaseFragment() {
    private lateinit var binding: ForgotPasswordFragmentBinding

    override fun getTitle(): String = getString(R.string.password_reset)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ForgotPasswordFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(getArgEmail().isNotEmpty()) binding.email.setText(getArgEmail())

        binding.sendButton.setOnClickListener {
            showProgress(getString(R.string.sending))

            val email = binding.email.text.toString()
            auth?.sendPasswordResetEmail(email)
                ?.addOnSuccessListener{
                    dismissProgress()
                    Snackbar.make(binding.root, getString(R.string.email_sent_successful), Snackbar.LENGTH_LONG).show()
                }
                ?.addOnFailureListener {
                    dismissProgress()
                    handleError(it)
                }
        }
    }

    private fun getArgEmail(): String = arguments?.getString(EMAIL, "").nonNullString()

    companion object {
        const val TAG = "forgot_password_frag"
        private const val EMAIL = "email"

        @JvmStatic
        fun getInstance(email: String): ForgotPasswordFragment {
            val frag = ForgotPasswordFragment()
            val bundle = Bundle()
            bundle.putString(EMAIL, email)
            frag.arguments = bundle
            return frag
        }
    }
}