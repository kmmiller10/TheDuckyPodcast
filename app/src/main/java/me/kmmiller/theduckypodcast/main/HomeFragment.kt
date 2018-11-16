package me.kmmiller.theduckypodcast.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.Realm
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.core.findSeriesModel
import me.kmmiller.theduckypodcast.databinding.HomeFragmentBinding
import me.kmmiller.theduckypodcast.models.SeriesModel
import me.kmmiller.theduckypodcast.utils.nonNullString

class HomeFragment : BaseFragment() {
    private lateinit var binding: HomeFragmentBinding
    var realm: Realm? = null
    var series: SeriesModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        realm = Realm.getDefaultInstance()

        // TODO: Hook up play button to podcast mp3 using MediaPlayer

        getCurrentSeries {
            series?.let { model ->
                setSeriesDetails(model)
            }
        }
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

    private fun getCurrentSeries(onSuccess: () -> Unit) {
        val fb = FirebaseFirestore.getInstance()
        fb.collection("series")
            .document("current-series")
            .get()
            .addOnSuccessListener {
                val seriesId = it.get("id").nonNullString()
                if(seriesId.isNotEmpty()) {
                    series = realm?.findSeriesModel(seriesId)
                    if(series != null) {
                        // User already has series loaded, don't need to grab it again
                        onSuccess.invoke()
                    } else {
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

                                series = realm?.findSeriesModel(model.id)
                                onSuccess.invoke()
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