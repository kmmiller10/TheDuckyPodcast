package me.kmmiller.theduckypodcast.main.optionsmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.databinding.AttributionsListFragmentBinding
import me.kmmiller.theduckypodcast.main.interfaces.ReturnToFragListener
import me.kmmiller.theduckypodcast.models.AttributionModel

class AttributionsListFragment : BaseMenuFragment(), ReturnToFragListener {
    private lateinit var binding: AttributionsListFragmentBinding

    override fun getTitle(): String = getString(R.string.attributions)
    override fun getItemId(): Int = R.id.attributions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = AttributionsListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val assetsList = requireActivity().assets.list("licenses")

        assetsList?.let { assets ->
            val attributionModels = ArrayList<AttributionModel>(assets.size)

            // Parse the licenses into models
            assets.forEach {
                val model = AttributionModel(it)
                model.parseNameAndType(requireActivity())
                attributionModels.add(model)
            }

            binding.attributionsList.adapter = AttributionsAdapter(attributionModels) {
                // On click, push the attribution fragment for this model
                pushFragment(AttributionFragment.getInstance(it), false, true, AttributionFragment.TAG)
            }
            binding.attributionsList.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onReturnToFrag() {
        activity?.title = getTitle()
    }

    companion object {
        const val TAG = "attr_list_frag"
    }
}