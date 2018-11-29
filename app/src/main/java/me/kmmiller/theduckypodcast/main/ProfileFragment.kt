package me.kmmiller.theduckypodcast.main

import android.os.Bundle
import android.view.*
import com.google.firebase.firestore.FirebaseFirestore
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.models.findUserById
import me.kmmiller.theduckypodcast.databinding.ProfileFragmentBinding
import me.kmmiller.theduckypodcast.main.interfaces.EditableFragment
import me.kmmiller.theduckypodcast.models.UserModel
import me.kmmiller.theduckypodcast.models.equalTo
import me.kmmiller.theduckypodcast.utils.nonNullString
import me.kmmiller.theduckypodcast.utils.onTextChangedListener

class ProfileFragment : MainMenuFragment(), EditableFragment {
    private lateinit var binding: ProfileFragmentBinding

    private var isEditing = false
    private var menu: Menu? = null

    override fun getTitle(): String = getString(R.string.profile)
    override fun getItemId(): Int = R.id.profile

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        binding.usState.onTextChangedListener {
            val text = binding.usState.text?.toString().nonNullString()

            binding.usStateError.visibility =
                    if(UserModel.stateAbbreviationsList.contains(text.toUpperCase()) || text.isEmpty())
                        View.GONE
                    else
                        View.VISIBLE
        }

        savedInstanceState?.let {
            isEditing = it.getBoolean(IS_EDITING, false)
            if(isEditing) {
                binding.age.setText(it.getString(AGE, ""))
                binding.gender.setSelection(it.getInt(GENDER,0))
                binding.usState.setText(it.getString(STATE, ""))
                // onEdit will enable all fields
                onEdit()
            }
        }

        if(!isEditing) {
            // onCancel will disable all the fields and reset the profile to the realm model
            onCancel()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.apply {
            putBoolean(IS_EDITING, isEditing)
            if(isEditing) {
                putString(AGE, binding.age.text?.toString().nonNullString())
                putInt(GENDER, binding.gender.selectedItemPosition)
                putString(STATE, binding.usState.text?.toString().nonNullString())
            }
            super.onSaveInstanceState(outState)
        }
    }

    private fun resetProfile() {
        auth?.currentUser?.let { authUser ->
            val user = realm?.findUserById(authUser.uid)
            user?.let {
                binding.email.setText(user.email)
                binding.age.setText(if(user.age == 0L) "" else user.age.toString())
                binding.gender.setSelection(UserModel.getPositionOfGender(user.gender))
                binding.usState.setText(user.state)
            }
        }
    }

    override fun onEdit() {
        isEditing = true
        binding.age.isEnabled = true
        binding.gender.isEnabled = true
        binding.usState.isEnabled = true
    }

    override fun onSave() {
        val age = binding.age.text?.toString()?.toLong() ?: 0
        val gender = binding.gender.selectedItem.toString()
        val stateText = binding.usState.text?.toString().nonNullString()

        val authUser = auth?.currentUser ?: return // Get authenticated user from firebase
        val realmUser = realm?.findUserById(authUser.uid)?: return // Get local realm user

        val detachedUser = realm?.copyFromRealm(realmUser) // Create a detached copy of realm user to manipulate
        detachedUser?.age = age
        detachedUser?.gender = gender
        detachedUser?.state = stateText

        if(detachedUser != null && realmUser.equalTo(detachedUser)) {
            // No changes made, don't bother saving
            onCancel()
            return
        }

        // Validate
        if(detachedUser != null && (stateText.isEmpty() || UserModel.stateAbbreviationsList.contains(stateText.toUpperCase()))) {
            showProgress(getString(R.string.saving))

            val fb = FirebaseFirestore.getInstance()
            fb.collection("users").document(realmUser.id)
                .set(detachedUser.fromRealmModel())
                .addOnSuccessListener {
                    // Update realm model
                    realm?.executeTransaction {
                        realmUser.age = detachedUser.age
                        realmUser.gender = detachedUser.gender
                        realmUser.state = detachedUser.state
                    }

                    dismissProgress()
                    onCancel() // Disables all fields
                    isEditing = false
                }
                .addOnFailureListener { e ->
                    dismissProgress()
                    handleError(e)
                }
        }
    }

    override fun onCancel() {
        resetProfile()

        isEditing = false
        binding.age.isEnabled = false
        binding.gender.isEnabled = false
        binding.usState.isEnabled = false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.editable_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        menu?.findItem(R.id.edit)?.isVisible = !isEditing
        menu?.findItem(R.id.save)?.isVisible = isEditing
        menu?.findItem(R.id.cancel)?.isVisible = isEditing
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.edit -> {
                menu?.apply {
                    findItem(R.id.edit)?.isVisible = false
                    findItem(R.id.save)?.isVisible = true
                    findItem(R.id.cancel)?.isVisible = true
                }
                onEdit()
                true
            }
            R.id.save -> {
                menu?.apply {
                    findItem(R.id.edit)?.isVisible = true
                    findItem(R.id.save)?.isVisible = false
                    findItem(R.id.cancel)?.isVisible = false
                }
                onSave()
                true
            }
            R.id.cancel -> {
                menu?.apply {
                    findItem(R.id.edit)?.isVisible = true
                    findItem(R.id.save)?.isVisible = false
                    findItem(R.id.cancel)?.isVisible = false
                }
                onCancel()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val TAG = "profile_fragment"
        const val IS_EDITING = "is_editing"
        const val AGE = "age"
        const val GENDER = "gender"
        const val STATE = "state"
    }
}