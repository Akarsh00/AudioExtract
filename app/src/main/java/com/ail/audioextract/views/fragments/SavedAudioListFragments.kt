package com.ail.audioextract.views.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.ail.audioextract.*
import kotlinx.android.synthetic.main.fragment_saved_audio_list_fragments.*


class SavedAudioListFragments : Fragment(), AudioListRecyclerViewAdapter.Interaction {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = AudioListRecyclerViewAdapter(this)
        rv_audioListRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        rv_audioListRecyclerView.adapter = adapter
        getAllSdCardTrackBeans(requireContext())?.let { adapter.submitList(it.reversed()) }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
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

    override fun onItemSelected(position: Int, item: AudioTrackBean) {

        playAudioIntent(requireContext(), item.mPath)
    }


}