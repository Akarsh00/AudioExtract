package com.ail.audioextract.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.ail.audioextract.R
import com.ail.audioextract.mediaaccess.mediaHolders.AudioFolderContent
import kotlinx.android.synthetic.main.audio_single_item.view.*

class AudioAlbumAdapter(private val interaction: Interaction? = null) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AudioFolderContent>() {

        override fun areItemsTheSame(oldItem: AudioFolderContent, newItem: AudioFolderContent): Boolean = oldItem.folderName == newItem.folderName

        override fun areContentsTheSame(oldItem: AudioFolderContent, newItem: AudioFolderContent): Boolean = oldItem.folderName == newItem.folderName
    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ItemHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.audio_album_single_item,
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

    fun submitList(list: List<AudioFolderContent>) {
        differ.submitList(list)
    }

    class ItemHolder
    constructor(
            itemView: View,
            private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: AudioFolderContent) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onAlbumSelected(adapterPosition, item)
            }
            itemView.tv_title.text = item.folderName
            itemView.tv_album_count.text = item.musicFiles.size.toString()
            
        }
    }

    interface Interaction {
        fun onAlbumSelected(position: Int, item: AudioFolderContent)
    }
}