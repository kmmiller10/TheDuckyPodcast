package me.kmmiller.theduckypodcast.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.Realm
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseActivity
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.core.findUserById
import me.kmmiller.theduckypodcast.databinding.ProfileFragmentBinding
import me.kmmiller.theduckypodcast.models.UserModel
import me.kmmiller.theduckypodcast.models.equalTo
import me.kmmiller.theduckypodcast.utils.Progress
import me.kmmiller.theduckypodcast.utils.nonNullString
import me.kmmiller.theduckypodcast.utils.onTextChangedListener

class ProfileFragment : BaseFragment(), EditableFragment {
    private lateinit var binding: ProfileFragmentBinding
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gender.isEnabled = false
        resetProfile()

        binding.usState.onTextChangedListener {
            val text = binding.usState.text?.toString().nonNullString()
            binding.usStateError.visibility =
                    if(UserModel.stateAbbreviationsList.contains(text.toUpperCase()) || text.isEmpty())
                        View.GONE
                    else
                        View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setEditableFragment(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun resetProfile() {
        val realm = Realm.getDefaultInstance()
        auth?.currentUser?.let { authUser ->
            val user = realm.findUserById(authUser.uid)
            user?.let {
                binding.email.setText(user.email)
                binding.age.setText(if(user.age == 0L) "" else user.age.toString())
                binding.gender.setSelection(UserModel.getPositionOfGender(user.gender))
                binding.usState.setText(user.state)
            }
        }
    }

    override fun onEdit() {
        binding.age.isEnabled = true
        binding.gender.isEnabled = true
        binding.usState.isEnabled = true
    }

    override fun onSave() {
        binding.age.isEnabled = false
        binding.gender.isEnabled = false
        binding.usState.isEnabled = false

        val age = binding.age.text?.toString()?.toLong() ?: 0
        val gender = binding.gender.selectedItem.toString()
        val stateText = binding.usState.text?.toString().nonNullString()

        val authUser = auth?.currentUser ?: return // Get authenticated user from firebase
        val realmUser = realm.findUserById(authUser.uid)?: return // Get local realm user

        val detachedUser = realm.copyFromRealm(realmUser) // Create a detached copy of realm user to manipulate
        detachedUser.age = age
        detachedUser.gender = gender
        detachedUser.state = stateText

        if(realmUser.equalTo(detachedUser)) {
            (activity as? MainActivity)?.setEditableFragment(true)
            resetProfile()
            return
        }

        // Validate
        if(stateText.isEmpty() || UserModel.stateAbbreviationsList.contains(stateText.toUpperCase())) {
            val progress = Progress(requireActivity() as BaseActivity)
            progress.progress(getString(R.string.saving))

            val fb = FirebaseFirestore.getInstance()
            fb.collection("users").document(realmUser.id)
                .set(detachedUser.fromRealmModel())
                .addOnSuccessListener { response ->
                    // Update realm model
                    realm.executeTransaction {
                        realmUser.age = detachedUser.age
                        realmUser.gender = detachedUser.gender
                        realmUser.state = detachedUser.state
                    }

                    progress.dismiss()
                    resetProfile()
                    (activity as? MainActivity)?.setEditableFragment(true)
                }
                .addOnFailureListener { e ->
                    progress.dismiss()
                    handleError(e)
                }
        }
    }

    override fun onCancel() {
        resetProfile()

        binding.age.isEnabled = false
        binding.gender.isEnabled = false
        binding.usState.isEnabled = false
    }

    companion object {
        const val TAG = "profile_fragment"
    }
}