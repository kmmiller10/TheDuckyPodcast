package me.kmmiller.theduckypodcast.main.optionsmenu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.kmmiller.theduckypodcast.databinding.AttributionsViewHolderBinding
import me.kmmiller.theduckypodcast.models.AttributionModel

class AttributionsAdapter(private val items: ArrayList<AttributionModel>,
                          private val clickListener: (String) -> Unit) : RecyclerView.Adapter<AttributionsAdapter.AttributionsViewHolder>() {
    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AttributionsViewHolder(AttributionsViewHolderBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: AttributionsViewHolder, position: Int) {
        val item = items[position]
        holder.setData(item)

    }

    inner class AttributionsViewHolder(private val binding: AttributionsViewHolderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(attribution: AttributionModel) {
            binding.attributionName.text = attribution.name
            binding.licenseType.text = attribution.licenseType

            binding.root.setOnClickListener {
                clickListener.invoke(attribution.fileName)
            }
        }
    }
}