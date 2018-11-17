package me.kmmiller.theduckypodcast.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.Realm
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseActivity
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.core.findDailiesModel
import me.kmmiller.theduckypodcast.databinding.DailiesFragmentBinding
import me.kmmiller.theduckypodcast.models.DailiesModel
import me.kmmiller.theduckypodcast.utils.Progress
import me.kmmiller.theduckypodcast.utils.nonNullString
import java.lang.Exception

class DailiesFragment : BaseFragment(), NavItem {
    private lateinit var binding: DailiesFragmentBinding
    var realm: Realm? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DailiesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        realm = Realm.getDefaultInstance()

        val progress = Progress(requireActivity() as BaseActivity)

        viewModel?.let {
            if(it.dailyId.isNotEmpty()) {
                load(progress, it.dailyId)
            }
        }

        if(viewModel?.dailyId.nonNullString().isEmpty()) {
            progress.progress(getString(R.string.loading))
        }

        val fb = FirebaseFirestore.getInstance()
        getDailyId(progress, fb) {
            if(it != viewModel?.dailyId.nonNullString()) {
                viewModel?.dailyId = it

                progress.progress(getString(R.string.loading))
                fb.collection("dailies")
                    .document(it)
                    .get()
                    .addOnSuccessListener { doc ->
                        val model = DailiesModel()
                        model.toRealmModel(doc)
                        realm?.executeTransaction { rm ->
                            rm.copyToRealmOrUpdate(model)
                        }
                        load(progress, it)
                    }
                    .addOnFailureListener { e ->
                        handleError(e)
                        progress.dismiss()
                    }
            } else {
                progress.dismiss()
            }
        }
    }

    private fun load(progress: Progress, id: String) {
        try {
            realm?.findDailiesModel(id)?.let {
                binding.title.text = it.title

                val adapter = QuestionAnswerAdapter(ArrayList(it.items))
                binding.questionAnswerList.adapter = adapter
                binding.questionAnswerList.layoutManager = LinearLayoutManager(requireContext())
                binding.questionAnswerList.isNestedScrollingEnabled = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Context probably null")
        }
        progress.dismiss()
    }

    private fun getDailyId(progress: Progress, fb: FirebaseFirestore, onComplete: (String) -> Unit) {
        fb.collection("dailies")
            .document("get-daily-id")
            .get()
            .addOnSuccessListener {
                val id = it.get("id").nonNullString()
                onComplete.invoke(id)
            }
            .addOnFailureListener {
                progress.dismiss()
                handleError(it)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm?.close()
        realm = null
    }

    override fun getNavId(): Int = R.id.nav_home

    companion object {
        const val TAG = "dailies_fragment"
    }
}