package com.ail.audioextract.views.fragments

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ail.audioextract.R
import com.ail.audioextract.RECENT_FOLDER_NAME
import com.ail.audioextract.VideoAlbumListAdapter
import com.ail.audioextract.VideoListRecyclerViewAdapter
import com.ail.audioextract.VideoSource.VideoFileInfo
import com.ail.audioextract.VideoSource.VideoFolderinfo
import com.ail.audioextract.data.ITEMS
import com.ail.audioextract.views.activity.HomeAppActivity
import kotlinx.android.synthetic.main.fragment_all_videos.*


class ChooseVideosFragments : Fragment(R.layout.fragment_all_videos), VideoListRecyclerViewAdapter.Interaction, VideoAlbumListAdapter.Interaction {
    private val REQ_PERMISSION = 200

    private val videoListRecyclerViewAdapter = VideoListRecyclerViewAdapter(this)
    private val videoAlbumListAdapter = VideoAlbumListAdapter(this)
    lateinit var alertDialogBuilder: AlertDialog.Builder
    val REQ_PICK_VIDEO = 100

    var videoPath = ""
    var eventType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        alertDialogBuilder = AlertDialog.Builder(requireContext())
        setHasOptionsMenu(true)
        eventType = arguments?.let { ChooseAudioFragmentsArgs.fromBundle(it).eventType }.toString()

        val activity = activity as AppCompatActivity?

        activity?.setSupportActionBar(toolbar)

        activity?.supportActionBar?.title = ""

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24)

        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()

        }

        /* VIDEO LIST  RecyclerViewAdapter */

        rv_videosList.adapter = videoListRecyclerViewAdapter
        rv_videosList.layoutManager = GridLayoutManager(requireContext(), 3)


        /* Video  List RecyclerViewAdapter */

        rv_videosAlbum.adapter = videoAlbumListAdapter
        rv_videosAlbum.layoutManager = LinearLayoutManager(requireContext())


        if (checkStoragePermissionGrantedOrNot() == PackageManager.PERMISSION_GRANTED) {
            (activity as HomeAppActivity).appBaseViewModel.getInternalStorageDataWithCursor(requireContext()).observe(viewLifecycleOwner, Observer {
                if (!it.isNullOrEmpty()) {
                    videoAlbumListAdapter.submitList(it)
                    showOrHIdeAlbumItem.text = RECENT_FOLDER_NAME
                }
            })

            (activity ).appBaseViewModel.listAllVideos?.observe(viewLifecycleOwner, Observer {
                alertDialogBuilder.setView(R.layout.progress_dialog)
                val dialog: Dialog = alertDialogBuilder.create()
                dialog.show()
                videoListRecyclerViewAdapter.submitList(it as List<VideoFileInfo>)
                dialog.dismiss()

            })
            showAlbum.setOnClickListener {
                showOrHideAlbumList()
            }
        }


    }

    private fun checkStoragePermissionGrantedOrNot(): Int {
        val pm = requireContext().packageManager
        val hasPerm = pm.checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                requireContext().packageName)
        return hasPerm
    }


    override fun onStart() {
        super.onStart()

        requestPermissions(
                arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQ_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {

        if (requestCode == REQ_PERMISSION /*&& grantResults[0] != PackageManager.PERMISSION_GRANTED*/) {
            /*     alertDialogBuilder.setView(R.layout.progress_dialog)
                 val dialog: Dialog = alertDialogBuilder.create()
                 dialog.show()*/

            (activity as HomeAppActivity).appBaseViewModel.queryListVideosFromBucket(requireContext(), null).observe(viewLifecycleOwner, Observer {


                videoListRecyclerViewAdapter.submitList(it as List<VideoFileInfo>)
                (activity as HomeAppActivity).appBaseViewModel.queryListVideosFromBucket(requireContext(), null)
                (activity as HomeAppActivity).appBaseViewModel.getInternalStorageDataWithCursor(requireContext()).observe(viewLifecycleOwner, Observer {
                    if (!it.isNullOrEmpty()) {
                        videoAlbumListAdapter.submitList(it)
                        showOrHIdeAlbumItem.text = RECENT_FOLDER_NAME
                    }
                })
                //                dialog.dismiss()

            })


        }
    }


    override fun onItemSelected(position: Int, item: VideoFolderinfo) {
        if (item.bucket_id != null) {
            (activity as HomeAppActivity).appBaseViewModel.queryListVideosFromBucket(requireContext(), arrayOf(item.bucket_id)).observe(viewLifecycleOwner, Observer {
                videoListRecyclerViewAdapter.submitList(it as List<VideoFileInfo>)
            })
        } else {
            /*if bucketId is null then show all videos--->All Videos item Clicked */
            showAllVideosList()

        }
        showOrHIdeAlbumItem.text = item.folderName
        showOrHideAlbumList()

    }

    private fun showOrHideAlbumList() {
        if (rv_videosAlbum.visibility == View.VISIBLE) {
            rv_videosAlbum.visibility = View.GONE
        } else {
            rv_videosAlbum.visibility = View.VISIBLE

        }
    }

    private fun showAllVideosList() {
        (activity as HomeAppActivity).appBaseViewModel.listAllVideos?.observe(viewLifecycleOwner, Observer {
            videoListRecyclerViewAdapter.submitList(it as List<VideoFileInfo>)
        })
    }


    override fun onItemSelected(position: Int, item: VideoFileInfo) {
        sendToTrimFragment(item.file_path)
    }

    override fun noSearchItemFound(noResultFound: Boolean) {
        if (noResultFound) {
            noItemFound.visibility = View.VISIBLE
            rv_videosList.visibility = View.GONE
        } else {
            noItemFound.visibility = View.GONE
            rv_videosList.visibility = View.VISIBLE
        }
    }

    private fun sendToTrimFragment(item: String) {
        if (eventType==ITEMS.VIDEO_TO_AUDIO.events)
        {
        val action =
                ChooseVideosFragmentsDirections.actionAllVideosFragmentToTrimFragment(
                        videoToTrim = item)
        Navigation.findNavController(requireView()).navigate(action)
        }
        else if(eventType==ITEMS.VIDEO_TRIM.events)
        {
            val action =
                    ChooseVideosFragmentsDirections.actionAllVideosFragmentToVideoTrimFragment(
                            videoToTrim = item)
            Navigation.findNavController(requireView()).navigate(action)

        }
    }

    override fun onResume() {
        super.onResume()
        if (view == null) {
            return
        }
        /*Below code maintains on BackPress on AllVideosFragment if album list is open then close it else exit the fragmnet*/

        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { v, keyCode, event ->
            if (event.action === KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (rv_videosAlbum.visibility == View.VISIBLE) {
                    rv_videosAlbum.visibility = View.GONE
                    true

                } else {
                    false
                }
            } else false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        var menuItem = menu.findItem(R.id.action_search_media)

        var searchView: SearchView = menuItem.actionView as SearchView

        val item = menu.findItem(R.id.pickMedia)

        searchView.setOnCloseListener {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24)
//            item.isVisible = true

            false
        }
        searchView.setOnSearchClickListener {
  //          item.isVisible = false
            toolbar.navigationIcon = null
            if (rv_videosAlbum.visibility == View.VISIBLE) {
                rv_videosAlbum.visibility = View.GONE
            }
        }


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    videoListRecyclerViewAdapter.filter.filter(newText)
                }
                return false
            }
        })
        return super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.pickMedia) {

            Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    .apply {
                        type = "video/*"
                    }
                    .also {

                        startActivityForResult(it, REQ_PICK_VIDEO)
                    }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_PICK_VIDEO -> {
                if (resultCode == Activity.RESULT_OK) {
                    videoPath = getRealPathFromMediaData(data?.data)
                    if (videoPath != "") {
                        sendToTrimFragment(videoPath)
                    }
                }
            }
        }
    }

    private fun getRealPathFromMediaData(data: Uri?): String {
        data ?: return ""

        var cursor: Cursor? = null
        try {
            cursor = requireContext().contentResolver.query(
                    data,
                    arrayOf(MediaStore.Video.Media.DATA),
                    null, null, null
            )

            val col = cursor!!.getColumnIndex(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()

            return cursor.getString(col)
        } finally {
            cursor?.close()
        }
    }

}
