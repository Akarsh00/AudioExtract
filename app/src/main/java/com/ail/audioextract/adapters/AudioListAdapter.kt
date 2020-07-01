package com.ail.audioextract.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.ail.audioextract.R
import com.ail.audioextract.findFileSizeFromPath
import com.ail.audioextract.mediaaccess.mediaHolders.AudioContent
import kotlinx.android.synthetic.main.audio_single_item.view.*

class AudioListAdapter(private val interaction: Interaction? = null) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AudioContent>() {

        override fun areItemsTheSame(oldItem: AudioContent, newItem: AudioContent): Boolean=
            oldItem.filePath==newItem.filePath

        override fun areContentsTheSame(oldItem: AudioContent, newItem: AudioContent): Boolean =
            oldItem.filePath==newItem.filePath

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ItemHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.audio_single_item,
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

    fun submitList(list: List<AudioContent>) {
        differ.submitList(list)
    }

    class ItemHolder
    constructor(
            itemView: View,
            private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: AudioContent) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            itemView.tv_title.text=item.name
            itemView.tv_album_count.text=item.duration.toString()
            itemView.tv_Size.text= findFileSizeFromPath(item.filePath.toString())
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: AudioContent)
    }
}