package com.ail.audioextract.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ail.audioextract.R
import idv.luchafang.videotrimmerexample.playAudioIntent
import idv.luchafang.videotrimmerexample.setRingtone
import idv.luchafang.videotrimmerexample.shareAudioIntent
import kotlinx.android.synthetic.main.final_activity_toolbar.*
import kotlinx.android.synthetic.main.fragment_final_saved.*
import java.io.File

class FinalSavedFragment : Fragment(R.layout.fragment_final_saved) {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val audioPath = arguments?.let { FinalSavedFragmentArgs.fromBundle(it).convertdAudioPath }
        if (audioPath != null) {
            updateLayouts(audioPath)
        }
        home.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.action_finalSavedFragment_to_savedAudioListFragments)
        }
        btn_cancel.setOnClickListener {
            Navigation.findNavController(requireView()).navigateUp()
        }

        openWith.setOnClickListener {
            audioPath?.let { it1 -> playAudioIntent(requireContext(), it1) }
        }
        shareWith.setOnClickListener {
            audioPath?.let { it1 -> shareAudioIntent(requireContext(), it1) }
        }
        setCallerTune.setOnClickListener {
            audioPath?.let { it1 -> setRingtone(requireContext(), it1) }
        }

    }

    private fun updateLayouts(path: String) {
        val file = File(path)
        if (file.exists()) {
            tv_title.text = file.name

            val fileSizeInBytes = file.length()
            val fileSizeInKB = fileSizeInBytes / 1024
            val fileSizeInMB = fileSizeInKB / 1024
            if (fileSizeInMB > 1) {
                tv_Size.text = fileSizeInMB.toString() + " mb"

            } else {
                tv_Size.text = fileSizeInKB.toString() + " kb"

            }

        }
    }

}