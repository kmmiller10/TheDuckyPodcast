package me.kmmiller.theduckypodcast.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.Realm
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.core.findUserById
import me.kmmiller.theduckypodcast.databinding.ProfileFragmentBinding
import me.kmmiller.theduckypodcast.models.UserModel

class ProfileFragment : BaseFragment() {
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

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    companion object {
        const val TAG = "profile_fragment"
    }
}