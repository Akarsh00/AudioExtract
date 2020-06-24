package com.ail.audioextract

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.ail.rocksvideoediting.data.HomeItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.home_item_card.view.*

class HomeItemRecyclerViewAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HomeItem>() {

        override fun areItemsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean =
            oldItem == newItem


        override fun areContentsTheSame(oldItem: HomeItem, newItem: HomeItem): Boolean =
            oldItem == newItem
    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ItemHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.home_item_card,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<HomeItem>) {
        differ.submitList(list)
    }

    class ItemHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: HomeItem) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            itemView.tv_title.text=item.title
            Glide.with(itemView.context).load(item.imageDrawable).into(itemView.iv_image)
            itemView.layout.background= ContextCompat.getDrawable(context, item.backgroundGradientDrawable)

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: HomeItem)
    }
}


