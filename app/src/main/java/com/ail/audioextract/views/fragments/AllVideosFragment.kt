package com.ail.audioextract.views.fragments

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ail.audioextract.R
import com.ail.audioextract.VLRecyclerViewAdapter
import com.ail.audioextract.VideoAlbumListAdapter
import com.ail.audioextract.VideoSource.VideoFileInfo
import com.ail.audioextract.VideoSource.VideoFolderinfo
import com.ail.audioextract.accessmedia.BaseMainViewModel
import kotlinx.android.synthetic.main.fragment_all_videos.*
import kotlinx.android.synthetic.main.main_app__toolbar.*


class AllVideosFragment : Fragment(R.layout.fragment_all_videos), VLRecyclerViewAdapter.Interaction, VideoAlbumListAdapter.Interaction {
    private val REQ_PERMISSION = 200

    //    val adapter = VideoListRecyclerViewAdapter(this)
    val adapter = VLRecyclerViewAdapter(this)
    val videoAlbumListAdapter = VideoAlbumListAdapter(this)
    lateinit var builder: AlertDialog.Builder

    lateinit var viewModel: BaseMainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BaseMainViewModel::class.java)
        val pm1 = requireContext().packageManager
        val hasPerm1 = pm1.checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                requireContext().packageName)
        if (hasPerm1 == PackageManager.PERMISSION_GRANTED) {
            viewModel.queryListVideosFromBucket(requireContext(), null)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        builder = AlertDialog.Builder(requireContext())

        home.setOnClickListener {

            if (rv_videosAlbum.visibility == View.VISIBLE) {
                rv_videosAlbum.visibility = View.GONE
            } else {
                rv_videosList.visibility = View.VISIBLE

            }
        }
        val pm = requireContext().packageManager
        val hasPerm = pm.checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                requireContext().packageName)
        if (hasPerm == PackageManager.PERMISSION_GRANTED) {
            viewModel.getInternalStorageDataWithCursor(requireContext())?.observe(viewLifecycleOwner, Observer {
                if (!it.isNullOrEmpty()) {
                    rv_videosAlbum.adapter = videoAlbumListAdapter
                    rv_videosAlbum.layoutManager = LinearLayoutManager(requireContext())
                    videoAlbumListAdapter.submitList(it)
                    showOrHIdeAlbumItem.text = "All Videos"
                }
            })

        }

        rv_videosList.adapter = adapter
        rv_videosList.layoutManager = GridLayoutManager(requireContext(), 3)
        val pm1 = requireContext().packageManager
        val hasPerm1 = pm1.checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                requireContext().packageName)
        if (hasPerm1 == PackageManager.PERMISSION_GRANTED) {


            viewModel.listAllVideos?.observe(viewLifecycleOwner, Observer {
                builder.setView(R.layout.progress_dialog)
                val dialog: Dialog = builder.create()
                dialog.show()

                adapter.submitList(it as List<VideoFileInfo>)
                dialog.dismiss()


            })

        }


    }


    override fun onStart() {
        super.onStart()

        ActivityCompat.requestPermissions(
                requireActivity(),
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQ_PERMISSION && grantResults.firstOrNull() != PackageManager.PERMISSION_GRANTED) {

            viewModel.queryListVideosFromBucket(requireContext(), null)?.observe(viewLifecycleOwner, Observer {
                builder.setView(R.layout.progress_dialog)
                val dialog: Dialog = builder.create()
                dialog.show()

                adapter.submitList(it as List<VideoFileInfo>)
                dialog.dismiss()


            })

        }
    }


    override fun onItemSelected(position: Int, item: VideoFolderinfo) {
        if (item.bucket_id != null) {
            viewModel.queryListVideosFromBucket(requireContext(), arrayOf(item.bucket_id)).observe(viewLifecycleOwner, Observer {
                adapter.submitList(it as List<VideoFileInfo>)
            })
        } else {
            /*if bucketId is null then show all videos--->All Videos item Clicked */
            showAllVideosList()

        }


        showOrHIdeAlbumItem.text = item.folderName
        if (rv_videosAlbum.visibility == View.VISIBLE) {
            rv_videosAlbum.visibility = View.GONE
        } else {
            rv_videosAlbum.visibility = View.VISIBLE
        }

    }

    private fun showAllVideosList() {
        viewModel.listAllVideos?.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it as List<VideoFileInfo>)
        })
    }


    override fun onItemSelected(position: Int, item: VideoFileInfo) {
        val action =
                AllVideosFragmentDirections.actionAllVideosFragmentToTrimFragment(
                        videoToTrim = item.file_path)
        Navigation.findNavController(requireView()).navigate(action)
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

}
