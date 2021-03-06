package me.kmmiller.theduckypodcast.main.optionsmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.databinding.AttributionFragmentBinding
import me.kmmiller.theduckypodcast.main.MainActivity
import me.kmmiller.theduckypodcast.models.AttributionModel
import me.kmmiller.theduckypodcast.utils.nonNullString

class AttributionFragment : BaseMenuFragment() {
    private lateinit var binding: AttributionFragmentBinding
    var attributionTitle = ""

    override fun getTitle(): String = if(attributionTitle.isBlank()) getString(R.string.attributions) else attributionTitle
    override fun getItemId(): Int = R.id.attributions

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = AttributionFragmentBinding.inflate(inflater, container, false)
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

        val attribution = getArgAttrModel()
        attributionTitle = attribution.name
        binding.attributionName.text = attribution.name
        binding.licenseType.text = attribution.licenseType
        binding.license.text = attribution.license
    }

    private fun getArgAttrModel(): AttributionModel {
        val fileName = arguments?.getString(ARG_ATTR_MODEL ,"").nonNullString()
        val model = AttributionModel(fileName)
        model.parseFromFile(requireActivity())
        return model
    }

    companion object {
        const val TAG = "attribution_frag"
        private const val ARG_ATTR_MODEL = "attribution_model"

        @JvmStatic
        fun getInstance(fileName: String): AttributionFragment {
            val frag = AttributionFragment()
            val bundle = Bundle()
            bundle.putString(ARG_ATTR_MODEL, fileName)
            frag.arguments = bundle
            return frag
        }
    }
}