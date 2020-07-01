package com.ail.audioextract.views.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.ail.audioextract.R
import com.ail.audioextract.adapters.AudioAlbumAdapter
import com.ail.audioextract.adapters.AudioListAdapter
import com.ail.audioextract.mediaaccess.AudioGet
import com.ail.audioextract.mediaaccess.MediaStorage
import com.ail.audioextract.mediaaccess.mediaHolders.AudioContent
import com.ail.audioextract.mediaaccess.mediaHolders.AudioFolderContent
import com.ail.audioextract.viewmodels.AllAudiosViewModel
import kotlinx.android.synthetic.main.fragment_all_audio.*
import kotlinx.android.synthetic.main.fragment_all_audio.showAlbum
import kotlinx.android.synthetic.main.fragment_all_videos.toolbar


class ChooseAudioFragments : Fragment(R.layout.fragment_all_audio), AudioAlbumAdapter.Interaction, AudioListAdapter.Interaction {
    val REQ_PICK_AUDIO = 0
    var albumAdapter = AudioAlbumAdapter(this)
    var adapter = AudioListAdapter(this)


    lateinit var viewModel: AllAudiosViewModel
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = activity as AppCompatActivity?

        activity?.setSupportActionBar(toolbar)

        activity?.supportActionBar?.title = ""
//        (getActivity() as AppCompatActivity).getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24)

        toolbar.setNavigationOnClickListener {
            if (album_recycler_view.visibility == View.VISIBLE) {
                album_recycler_view.visibility = View.GONE
            }
            activity?.onBackPressed()

        }
        setHasOptionsMenu(true)

        audio_recycler_view.layoutManager = GridLayoutManager(requireContext(), 1)
        audio_recycler_view.adapter = adapter
        adapter.submitList(MediaStorage.withAudioContex(requireContext()).getAllAudioContent(AudioGet.externalContentUri))

        album_recycler_view.layoutManager = GridLayoutManager(requireContext(), 1)
        album_recycler_view.adapter = albumAdapter
        albumAdapter.submitList(MediaStorage.withAudioContex(requireContext()).allAudioFolderContent)
        showAlbum.setOnClickListener {
            showOrHideAlbumList()
        }
    }

    private fun showOrHideAlbumList() {
        if (album_recycler_view.visibility == View.VISIBLE) {
            album_recycler_view.visibility = View.GONE
        } else {
            album_recycler_view.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        var menuItem = menu.findItem(R.id.action_search_media)

        var searchView: SearchView = menuItem.actionView as SearchView
        searchView.setOnCloseListener {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24)
//            item.isVisible = true

            false
        }
        searchView.setOnSearchClickListener {
            //          item.isVisible = false
            toolbar.navigationIcon = null
            if (album_recycler_view.visibility == View.VISIBLE) {
                album_recycler_view.visibility = View.GONE
            }
        }


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                return false
            }
        })
        return super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.pickMedia) {

            Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    .apply {
                        type = "Audio/*"
                    }
                    .also {

                        startActivityForResult(it, REQ_PICK_AUDIO)
                    }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onAlbumSelected(position: Int, item: AudioFolderContent) {
        adapter.submitList(item.musicFiles)
        showOrHideAlbumList()
        showOrHIdeAlbum.text=item.folderName
    }

    override fun onItemSelected(position: Int, item: AudioContent) {

    }

}

