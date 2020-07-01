package com.ail.audioextract.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.ail.audioextract.R
import com.ail.audioextract.adapters.VideoListAdapter
import com.ail.audioextract.mediaaccess.MediaStorage
import com.ail.audioextract.mediaaccess.VideoGet
import com.ail.audioextract.mediaaccess.mediaHolders.VideoContent
import kotlinx.android.synthetic.main.fragment_saved_edited_videos.*


class SavedEditedVideosFragment : Fragment(),VideoListAdapter.Interaction {

    var folderName=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            folderName = arguments?.getString(EDITED_VIDEO_FOLDER_NAME).toString()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_saved_edited_videos, container, false)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
       var adapter= VideoListAdapter()
        recycler_view.layoutManager=GridLayoutManager(requireContext(),1)
            var allVideoAlbumList=MediaStorage.withVideoContex(requireContext()).getVideoFolders(VideoGet.externalContentUri)
            var bucketId=0
            var listOfVideosInsideFolder=allVideoAlbumList.forEach { if(it.folderName==folderName){
                bucketId=it.bucket_id
            }
            recycler_view.adapter=adapter
            adapter.submitList(MediaStorage.withVideoContex(requireContext()).getAllVideoContentByBucket_id(bucketId))
        }

        Toast.makeText(requireContext(),""+listOfVideosInsideFolder,Toast.LENGTH_LONG).show()
    }

    companion object {
        val EDITED_VIDEO_FOLDER_NAME = "folderName"

        @JvmStatic
        fun newInstance(param1: String) =
                SavedEditedVideosFragment().apply {
                    arguments = Bundle().apply {
                        putString(EDITED_VIDEO_FOLDER_NAME, param1)
                    }
                }
    }

    override fun onItemSelected(position: Int, item: VideoContent) {
        Toast.makeText(requireContext(),"clicked",Toast.LENGTH_LONG).show()
    }
}