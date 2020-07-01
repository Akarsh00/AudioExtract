package com.ail.audioextract.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.ail.audioextract.R
import com.ail.audioextract.findFileSizeFromPath
import com.ail.audioextract.mediaaccess.mediaHolders.VideoContent
import com.bumptech.glide.Glide
import idv.luchafang.videotrimmerexample.getFileDurationInDetails
import kotlinx.android.synthetic.main.video_single_row_item.view.*
import java.io.File

class VideoListAdapter(private val interaction: Interaction? = null) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<VideoContent>() {

        override fun areItemsTheSame(oldItem: VideoContent, newItem: VideoContent): Boolean = oldItem.path == newItem.path

        override fun areContentsTheSame(oldItem: VideoContent, newItem: VideoContent): Boolean = oldItem.path == newItem.path

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return VideoItemHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.video_single_row_item,
                        parent,
                        false
                ),
                interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VideoItemHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<VideoContent>) {
        differ.submitList(list)
    }

    class VideoItemHolder
    constructor(
            itemView: View,
            private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: VideoContent) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)

            }
            Glide.with(itemView.context).load(item.path).thumbnail(0.05f).centerCrop().into(itemView.imageView)
            tv_title.text = item.videoName
            tv_VideoTime.text = item.videoDuration.toInt().getFileDurationInDetails()
            tv_Size.text=findFileSizeFromPath(item.path.toString())
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: VideoContent)
    }

}