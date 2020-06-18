package com.ail.audioextract


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ail.audioextract.VideoSource.VideoFileInfo
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.video_single_item.view.*


class VideoListRecyclerViewAdapter(private val interaction: Interaction? = null) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<VideoFileInfo>() {

        override fun areItemsTheSame(oldItem: VideoFileInfo, newItem: VideoFileInfo): Boolean {
            return oldItem.file_path == newItem.file_path
        }

        override fun areContentsTheSame(oldItem: VideoFileInfo, newItem: VideoFileInfo): Boolean {
            return oldItem.file_path == newItem.file_path
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return VideoItemHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.video_single_item,
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

    fun submitList(list: List<VideoFileInfo>) {
        differ.submitList(list)
    }

    class VideoItemHolder
    constructor(
            itemView: View,
            private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: VideoFileInfo) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)

            }
            Glide.with(itemView.context).load(item.file_path).thumbnail(0.05f).centerCrop().into(itemView.imageView)
            tv_title.text = item.file_name
//            tv_VideoTime.text = item.duration.toInt().getFile_duration_inDetail()

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: VideoFileInfo)
    }


}


