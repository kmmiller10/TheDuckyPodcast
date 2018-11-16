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
import me.kmmiller.theduckypodcast.core.findSeriesModel
import me.kmmiller.theduckypodcast.databinding.HomeFragmentBinding
import me.kmmiller.theduckypodcast.models.SeriesModel
import me.kmmiller.theduckypodcast.utils.Progress
import me.kmmiller.theduckypodcast.utils.nonNullString

class HomeFragment : BaseFragment() {
    private lateinit var binding: HomeFragmentBinding
    var realm: Realm? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        realm = Realm.getDefaultInstance()

        // TODO: Hook up play button to podcast mp3 using MediaPlayer
        val progress = Progress(requireActivity() as BaseActivity)

        viewModel?.let {
            if(it.seriesId.isNotEmpty()) {
                load(progress, it.dailyId)
            }
        }

        if(viewModel?.seriesId.nonNullString().isEmpty()) {
            progress.progress(getString(R.string.loading))
        }


        getCurrentSeries(progress) {
            load(progress, it)
        }
    }

    private fun load(progress: Progress, id: String) {
        realm?.findSeriesModel(id)?.let { model ->
            setSeriesDetails(model)
        }
        progress.dismiss()
    }

    private fun setSeriesDetails(model: SeriesModel) {
        binding.title.text = model.title
        binding.seriesNumber.text = String.format(getString(R.string.series_number), model.season)
        binding.description.text = model.description
        binding.expandedDescription.text = model.expandedDescription
    }

    override fun onDestroy() {
        super.onDestroy()
        realm?.close()
        realm = null
    }

    private fun getCurrentSeries(progress: Progress, onSuccess: (String) -> Unit) {
        val fb = FirebaseFirestore.getInstance()
        fb.collection("series")
            .document("current-series")
            .get()
            .addOnSuccessListener {
                val seriesId = it.get("id").nonNullString()
                if(seriesId.isNotEmpty()) {
                    val series = realm?.findSeriesModel(seriesId)
                    if(series != null) {
                        // User already has series loaded, don't need to grab it again
                        onSuccess.invoke(seriesId)
                    } else {
                        progress.progress(getString(R.string.loading))
                        // New series, get and add to realm
                        fb.collection("series")
                            .document(seriesId)
                            .get()
                            .addOnSuccessListener { doc ->
                                val model = SeriesModel()
                                model.toRealmModel(doc)

                                realm?.executeTransaction { rm ->
                                    rm.copyToRealmOrUpdate(model)
                                }

                                onSuccess.invoke(seriesId)
                            }
                            .addOnFailureListener { e ->
                                handleError(e)
                            }
                    }
                }
            }
            .addOnFailureListener {
                handleError(it)
            }
    }

    companion object {
        const val TAG = "home_fragment"
    }
}