package com.ail.audioextract.views.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.ail.audioextract.AudioListRecyclerViewAdapter
import com.ail.audioextract.R
import com.ail.audioextract.Track_Bean
import com.ail.audioextract.getAllSdCardTrack_Beans
import idv.luchafang.videotrimmerexample.playAudioIntent
import kotlinx.android.synthetic.main.fragment_saved_audio_list_fragments.*
import java.io.File


class SavedAudioListFragments : Fragment(), AudioListRecyclerViewAdapter.Interaction {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = AudioListRecyclerViewAdapter(this)
        rv_audioListRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        rv_audioListRecyclerView.adapter = adapter
        getAllSdCardTrack_Beans(requireContext())?.let { adapter.submitList(it) }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_saved_audio_list_fragments, container, false)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationIcon(R.drawable.ic_back_button)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationOnClickListener {
                Navigation.findNavController(requireView()).navigateUp()
            }
        }
        return view
    }

    override fun onItemSelected(position: Int, item: Track_Bean) {

        playAudioIntent(requireContext(),item.mPath)
    }




}