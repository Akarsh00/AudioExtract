package com.ail.audioextract

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ail.audioextract.VideoSource.VideoFileInfo
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.video_single_item.view.*

class VLRecyclerViewAdapter(private val interaction: VLRecyclerViewAdapter.Interaction? = null) : RecyclerView.Adapter<VLRecyclerViewAdapter.VideoItemHolder>() {
    var originalList = listOf<VideoFileInfo>()

    class VideoItemHolder(item: View,val interaction:Interaction?) : RecyclerView.ViewHolder(item) {

        fun bind(item: VideoFileInfo) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)

            }
            Glide.with(itemView.context).load(item.file_path).thumbnail(0.05f).centerCrop().into(itemView.imageView)
            tv_title.text = item.file_name
//            tv_VideoTime.text = item.duration.toInt().getFile_duration_inDetail()

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoItemHolder {

        return VideoItemHolder(
                LayoutInflater.from(parent.context).inflate(
                        R.layout.video_single_item,
                        parent,
                        false
                ),
                interaction
        )
    }

    override fun getItemCount(): Int {
      return  originalList.size
    }

    override fun onBindViewHolder(holder: VideoItemHolder, position: Int) {
        holder.bind(originalList.get(position))
    }

    fun submitList(list: List<VideoFileInfo>) {
        originalList = list
        notifyDataSetChanged()

    }

    interface Interaction {
        fun onItemSelected(position: Int, item: VideoFileInfo)
    }

}