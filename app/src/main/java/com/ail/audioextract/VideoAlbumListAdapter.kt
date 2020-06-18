package com.ail.audioextract

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.ail.audioextract.VideoSource.VideoFolderinfo
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.album_single_item.view.*

class VideoAlbumListAdapter(private val interaction: Interaction? = null) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<VideoFolderinfo>() {

        override fun areItemsTheSame(oldItem: VideoFolderinfo, newItem: VideoFolderinfo): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: VideoFolderinfo, newItem: VideoFolderinfo): Boolean {
            return true
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ItemHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.album_single_item,
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

    fun submitList(list: List<VideoFolderinfo>) {
        differ.submitList(list)
    }

    class ItemHolder
    constructor(
            itemView: View,
            private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: VideoFolderinfo) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            albumName.text = item.folderName
            if (item.firstVideoPath == "") {
                Glide.with(itemView.context).load(R.drawable.ic_album_white_24).fitCenter().centerCrop().into(video_thumb)

            }
            else{
                Glide.with(itemView.context).load(item.firstVideoPath).thumbnail(0.05f).centerCrop().into(video_thumb)

            }

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: VideoFolderinfo)
    }
}


