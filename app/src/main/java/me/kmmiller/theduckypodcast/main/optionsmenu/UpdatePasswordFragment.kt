package me.kmmiller.theduckypodcast.main.optionsmenu

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import com.google.firebase.auth.EmailAuthProvider
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseActivity
import me.kmmiller.theduckypodcast.databinding.UpdatePasswordFragmentBinding
import me.kmmiller.theduckypodcast.main.MainActivity
import me.kmmiller.theduckypodcast.main.interfaces.ReturnToFragListener
import me.kmmiller.theduckypodcast.main.interfaces.SavableFragment
import me.kmmiller.theduckypodcast.utils.nonNullString
import me.kmmiller.theduckypodcast.utils.onTextChangedListener
import me.kmmiller.theduckypodcast.utils.validatePassword

class UpdatePasswordFragment : BaseMenuFragment(), SavableFragment {
    private lateinit var binding: UpdatePasswordFragmentBinding

    override fun getItemId(): Int = R.id.profile
    override fun getTitle(): String = getString(R.string.update_password)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = UpdatePasswordFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.apply {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setDisplayShowHomeEnabled(true)
            }
        }
        setHasOptionsMenu(true)

        binding.currentPassword.setOnEditorActionListener { _, action, _ ->
            if(action == EditorInfo.IME_ACTION_NEXT) {
                // Hitting next should go to confirm password edit text
                binding.newPassword.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.currentPassword.onTextChangedListener {
            binding.passwordInvalid.visibility = View.GONE
        }

        binding.newPassword.setOnEditorActionListener { _, action, _ ->
            if(action == EditorInfo.IME_ACTION_NEXT) {
                // Hitting next should go to confirm password edit text
                binding.confirmPassword.requestFocus()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.newPassword.onTextChangedListener {
            if(binding.newPassword.text.toString() == binding.confirmPassword.text.toString()) {
                binding.passwordsDoNotMatch.visibility = View.GONE
            } else if(!binding.confirmPassword.text?.toString().isNullOrEmpty()) {
                // Show error if confirm password field is not empty
                binding.passwordsDoNotMatch.visibility = View.VISIBLE
            }
        }

        binding.confirmPassword.setOnEditorActionListener { _, action, _ ->
            if(action == EditorInfo.IME_ACTION_DONE) {
                clearFocus()
                onSave()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.confirmPassword.onTextChangedListener {
            if(binding.newPassword.text.toString() != binding.confirmPassword.text.toString()) {
                binding.passwordsDoNotMatch.visibility = View.VISIBLE
            } else {
                binding.passwordsDoNotMatch.visibility = View.GONE
            }
        }
    }

    private fun clearFocus() {
        (activity as BaseActivity).hideKeyboard()
    }

    private fun validatePassword(): Boolean {
        val password = binding.newPassword.text.toString()
        val confirmPassword = binding.confirmPassword.text.toString()

        val errorString = password.validatePassword(requireActivity(), confirmPassword)

        if(errorString.isEmpty()) return true

        binding.passwordsDoNotMatch.text = errorString
        binding.passwordsDoNotMatch.visibility = View.VISIBLE

        return false
    }

    override fun onSave() {
        if(validatePassword() && binding.currentPassword.text.toString().isNotEmpty()) {
            if(auth == null || auth?.currentUser == null) (activity as? MainActivity)?.logOut()
            auth?.currentUser?.let { user ->
                // Get auth credentials from the user for re-authentication, see: https://firebase.google.com/docs/auth/android/manage-users?authuser=0#re-authenticate_a_user
                val email = user.email
                if(email != null) {
                    val credential = EmailAuthProvider.getCredential(email, binding.currentPassword.text.toString())

                    showProgress(getString(R.string.updating))
                    changeFieldsEnabled(false)

                    user.reauthenticate(credential)
                        .addOnSuccessListener {
                            dismissProgress()
                            user.updatePassword(binding.newPassword.text.toString())
                                .addOnCompleteListener {
                                    binding.currentPassword.setText("")
                                    binding.newPassword.setText("")
                                    binding.confirmPassword.setText("")
                                    changeFieldsEnabled(true)

                                    activity?.supportFragmentManager?.fragments
                                        ?.filter { it is ReturnToFragListener }
                                        ?.forEach { (it as ReturnToFragListener).onReturnToFrag() }
                                    activity?.supportFragmentManager?.popBackStack()
                                }
                        }
                        .addOnFailureListener {
                            dismissProgress()
                            changeFieldsEnabled(true)
                            // TODO error exception type handling
                            binding.passwordInvalid.visibility = View.VISIBLE
                        }
                }

            }
        }
    }

    private fun changeFieldsEnabled(isEnabled: Boolean) {
        binding.currentPassword.isEnabled = isEnabled
        binding.newPassword.isEnabled = isEnabled
        binding.confirmPassword.isEnabled = isEnabled
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.savable_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item != null && item.itemId == R.id.save) {
            onSave()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    companion object {
        const val TAG = "update_pass_frag"
    }
}