package me.kmmiller.theduckypodcast.main.optionsmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.databinding.AttributionsFragmentBinding
import me.kmmiller.theduckypodcast.models.AttributionModel

class AttributionsFragment : BaseMenuFragment() {
    private lateinit var binding: AttributionsFragmentBinding

    override fun getTitle(): String = getString(R.string.attributions)
    override fun getItemId(): Int = R.id.about

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = AttributionsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val assetsList = requireActivity().assets.list("licenses")

        assetsList?.let { assets ->
            val attributionModels = ArrayList<AttributionModel>(assets.size)

            // Parse the licenses into models
            assets.forEach {
                val model = AttributionModel()
                model.parseFromFile(requireActivity(), it)
                attributionModels.add(model)
            }

            binding.attributionsList.adapter = AttributionsAdapter(attributionModels)
            binding.attributionsList.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    companion object {
        const val TAG = "attributions_frag"
    }
}