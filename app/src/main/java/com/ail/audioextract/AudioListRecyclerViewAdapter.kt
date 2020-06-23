package com.ail.audioextract

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.synthetic.main.audio_single_item.view.*

class AudioListRecyclerViewAdapter(private val interaction: Interaction? = null) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AudioTrackBean>() {

        override fun areItemsTheSame(oldItem: AudioTrackBean, newItem: AudioTrackBean): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: AudioTrackBean, newItem: AudioTrackBean): Boolean {
            return false
        }

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

    fun submitList(list: List<AudioTrackBean>) {
        differ.submitList(list)
    }

    class ItemHolder
    constructor(
            itemView: View,
            private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: AudioTrackBean) = with(itemView) {
            tv_title.text=item.mTitle
            tv_Size.text=item.mDuration
            tv_duration.text=findFileSizeFromPath(item.mPath)
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: AudioTrackBean)
    }



}


