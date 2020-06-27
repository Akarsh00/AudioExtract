package com.ail.audioextract

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.ail.audioextract.VideoSource.VideoFileInfo
import com.bumptech.glide.Glide
import idv.luchafang.videotrimmerexample.getFileDurationInDetails
import kotlinx.android.synthetic.main.video_single_item.view.*

class VideoListRecyclerViewAdapter(private val interaction: VideoListRecyclerViewAdapter.Interaction? = null) : RecyclerView.Adapter<VideoListRecyclerViewAdapter.VideoItemHolder>(), Filterable {
    var originalList = listOf<VideoFileInfo>()
    var listAllData = listOf<VideoFileInfo>()

    class VideoItemHolder(item: View,val interaction:Interaction?) : RecyclerView.ViewHolder(item) {

        fun bind(item: VideoFileInfo) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)

            }
            Glide.with(itemView.context).load(item.file_path).thumbnail(0.05f).centerCrop().into(itemView.imageView)
            tv_title.text = item.file_name
            if (item.duration!=null)
            {
                tv_VideoTime.text = item.duration.toInt().getFileDurationInDetails()

            }

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

    fun submitList(list: List<VideoFileInfo>, isFiltered: Boolean=false) {
        if (isFiltered) {
            if(!list.isNullOrEmpty())
            {
                interaction?.noSearchItemFound(false)
            }
            else{
                interaction?.noSearchItemFound(true)

            }
            originalList = list
            notifyDataSetChanged()
        } else {
            originalList=list
            listAllData=list

            notifyDataSetChanged()
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: VideoFileInfo)
        fun noSearchItemFound(boolean: Boolean)

    }


    override fun getFilter(): Filter {

        return object : Filter() {
            override fun publishResults(
                    constraint: CharSequence,
                    results: FilterResults
            ) {
                val filteredList=results.values /*as MutableList<VideoFileInfo>*/
                submitList(filteredList as List<VideoFileInfo>,true)
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {
                var filteredResults = mutableListOf<VideoFileInfo>()
                if (listAllData.size>0) {
                    if (constraint.toString().isEmpty()) {
                        filteredResults.addAll(listAllData)
                    } else {

                        for (videoFileInfo in listAllData) {
                            if (videoFileInfo.file_name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                                filteredResults.add(videoFileInfo)
                            }

                        }
                    }
                }
                var filterResultList = FilterResults()
                filterResultList.values = filteredResults

                return filterResultList
            }
        }

    }

}