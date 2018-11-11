package me.kmmiller.theduckypodcast.base.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import me.kmmiller.theduckypodcast.R
import me.kmmiller.theduckypodcast.databinding.BottomNavViewHolderBinding

class BottomNavAdapter(initialItem: Int, private val navItems: ArrayList<BottomNavItemModel>) : RecyclerView.Adapter<BottomNavAdapter.BottomNavViewHolder>() {

    private var currentNavId = initialItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomNavViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BottomNavViewHolder(BottomNavViewHolderBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: BottomNavViewHolder, position: Int) {
        val item = navItems[position]


        holder.setLabel(item.label)

        val imageView = (holder.itemView as ViewGroup).getChildAt(0) as AppCompatImageView

        if(item.navId == currentNavId) {
            holder.setIcon(item.iconActive)
            val color = ColorStateList.valueOf(getColor(holder.itemView.context, R.color.colorPrimary))
            holder.setTextColor(color)
        } else {
            holder.setIcon(item.iconInactive)
            val textColor = ColorStateList.valueOf(getColor(holder.itemView.context, R.color.dark_gray))
            holder.setTextColor(textColor)
        }

        holder.itemView.setOnClickListener {
            if(it.context is BottomNavAdapterListener) {
                currentNavId = item.navId
                (it.context as BottomNavAdapterListener).onNavItemSelected(navItems[holder.adapterPosition].navId)
            }
        }
    }

    override fun getItemCount(): Int = navItems.size

    inner class BottomNavViewHolder(private val binding: BottomNavViewHolderBinding) : RecyclerView.ViewHolder(binding.root) {
        private val context = itemView.context

        fun setIcon(@DrawableRes drawableRes: Int) {
            binding.itemIcon.setImageDrawable(ContextCompat.getDrawable(context, drawableRes))
        }

        fun setLabel(@StringRes labelRes: Int) {
            binding.itemLabel.text = context.getString(labelRes)
        }

        fun setTextColor(color: ColorStateList) {
            binding.itemLabel.setTextColor(color)
        }
    }

    interface BottomNavAdapterListener {
        fun onNavItemSelected(@IdRes itemId: Int)
    }
}