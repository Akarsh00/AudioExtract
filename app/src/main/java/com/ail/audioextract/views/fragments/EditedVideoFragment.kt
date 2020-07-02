package com.ail.audioextract.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.ail.audioextract.R
import com.ail.audioextract.VideoListRecyclerViewAdapter
import com.ail.audioextract.VideoSource.VideoFileInfo
import com.ail.audioextract.views.activity.HomeAppActivity
import kotlinx.android.synthetic.main.fragment_edited_video.*


class EditedVideoFragment : Fragment(), VideoListRecyclerViewAdapter.Interaction {
    private val VIDEO_FOLDER_NAME = ""
    private val videoListRecyclerViewAdapter = VideoListRecyclerViewAdapter(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val folderName = arguments?.getString(VIDEO_FOLDER_NAME)
        folderName?.let { (activity as HomeAppActivity).savedViewModel.getAllVideosFromFolder(folderName) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edited_video, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rv_videosList.adapter = videoListRecyclerViewAdapter
        rv_videosList.layoutManager = GridLayoutManager(requireContext(), 3)
        (activity as HomeAppActivity).savedViewModel.listOfVideos.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                videoListRecyclerViewAdapter.submitList(it)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
                EditedVideoFragment().apply {
                    arguments = Bundle().apply {
                        putString(VIDEO_FOLDER_NAME, param1)
                    }
                }
    }

    override fun onItemSelected(position: Int, item: VideoFileInfo) {

    }

    override fun noSearchItemFound(boolean: Boolean) {

    }

}