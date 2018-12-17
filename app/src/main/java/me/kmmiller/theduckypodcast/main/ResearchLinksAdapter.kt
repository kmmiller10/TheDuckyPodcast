package me.kmmiller.theduckypodcast.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.databinding.ResearchLinkViewHolderBinding

class ResearchLinksAdapter(private val links: ArrayList<String>) : RecyclerView.Adapter<ResearchLinksAdapter.ResearchLinkViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResearchLinkViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ResearchLinkViewHolder(ResearchLinkViewHolderBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ResearchLinkViewHolder, position: Int) {
        val link = links[position]

        holder.bind(position+1, link)
    }

    override fun getItemCount(): Int = links.size

    inner class ResearchLinkViewHolder(private val binding: ResearchLinkViewHolderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(index: Int, link: String) {
            val indexFormatted = String.format(binding.root.context.getString(R.string.research_link_index), index)
            binding.index.text = indexFormatted
            binding.link.text = link
        }
    }
}