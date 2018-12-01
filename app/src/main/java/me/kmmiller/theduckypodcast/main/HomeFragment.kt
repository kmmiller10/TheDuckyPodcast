package me.kmmiller.theduckypodcast.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.base.BaseFragment
import me.kmmiller.theduckypodcast.models.findSeriesModel
import me.kmmiller.theduckypodcast.databinding.HomeFragmentBinding
import me.kmmiller.theduckypodcast.main.interfaces.NavItem
import me.kmmiller.theduckypodcast.models.SeriesModel
import me.kmmiller.theduckypodcast.utils.nonNullString

class HomeFragment : BaseFragment(), NavItem {
    private lateinit var binding: HomeFragmentBinding

    override fun getTitle(): String = getString(R.string.home)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Hook up play button to podcast mp3 using MediaPlayer

        viewModel?.let {
            if(it.seriesId.isNotEmpty()) {
                load(it.seriesId)
            }
        }

        if(viewModel?.seriesId.nonNullString().isEmpty()) {
            showCancelableProgress(getString(R.string.loading))
        }

        getCurrentSeries {
            viewModel?.seriesId = it
            load(it)
        }
    }

    private fun load(id: String) {
        realm?.findSeriesModel(id)?.let { model ->
            setSeriesDetails(model)
        }
        dismissProgress()
    }

    private fun setSeriesDetails(model: SeriesModel) {
        binding.title.text = model.title
        binding.seriesNumber.text = String.format(getString(R.string.series_number), model.season)
        binding.description.text = model.description
        binding.expandedDescription.text = model.expandedDescription
    }

    private fun getCurrentSeries(onSuccess: (String) -> Unit) {
        fb.collection("series")
            .document("current-series")
            .get()
            .addOnSuccessListener {
                val seriesId = it.get("id").nonNullString()
                if(seriesId.isNotEmpty() && context != null) {
                    val series = realm?.findSeriesModel(seriesId)
                    if(series != null) {
                        // User already has series loaded, don't need to grab it again
                        onSuccess.invoke(seriesId)
                    } else {
                        showCancelableProgress(getString(R.string.loading))
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
                                dismissProgress()
                            }
                    }
                }
            }
            .addOnFailureListener {
                handleError(it)
                dismissProgress()
            }
    }

    override fun getNavId(): Int = R.id.nav_home

    companion object {
        const val TAG = "home_fragment"
    }
}