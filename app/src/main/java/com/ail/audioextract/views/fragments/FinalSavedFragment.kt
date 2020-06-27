package com.ail.audioextract.views.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ail.audioextract.R
import com.ail.audioextract.playAudioIntent
import com.ail.audioextract.setRingtone
import com.ail.audioextract.shareAudioIntent
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import idv.luchafang.videotrimmerexample.uriExtractorFromPath
import kotlinx.android.synthetic.main.final_activity_toolbar.*
import kotlinx.android.synthetic.main.fragment_final_saved.*
import kotlinx.android.synthetic.main.fragment_final_saved.playerView
import java.io.File

class FinalSavedFragment : Fragment(R.layout.fragment_final_saved) {


    lateinit var player: SimpleExoPlayer

    lateinit var dataSourceFactory: DataSource.Factory

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
            audioPath?.let { playAudioIntent(requireContext(), it) }
        }

        shareWith.setOnClickListener {
            audioPath?.let { shareAudioIntent(requireContext(), it) }
        }

        setCallerTune.setOnClickListener {
            audioPath?.let { setRingtone(requireContext(), it) }
        }

        player = SimpleExoPlayer.Builder(requireContext()).build().also {
            it.repeatMode = SimpleExoPlayer.REPEAT_MODE_ALL
            playerView.player = it
        }

        dataSourceFactory = DefaultDataSourceFactory(requireContext(), "VideoTrimmer")

        if (audioPath != null)
            playAudio(audioPath)


    }

    private fun updateLayouts(path: String) {
        val file = File(path)
        if (file.exists()) {
            tv_title.text = file.name

            val fileSizeInBytes = file.length()
            val fileSizeInKB = fileSizeInBytes / 1024
            val fileSizeInMB = fileSizeInKB / 1024
            if (fileSizeInMB > 1) {
                tv_Size.text = "$fileSizeInMB mb"

            } else {
                tv_Size.text = "$fileSizeInKB kb"

            }

        }
    }

    private fun playAudio(path: String) {
        if (path.isBlank()) return
        val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(
                        path.uriExtractorFromPath()
                )
        player.playWhenReady = false
        player.prepare(source)
        playerView.setShutterBackgroundColor(ContextCompat.getColor(requireContext(), R.color.semi_white))
        playerView.player = player
        playerView.requestFocus()
    }

    private fun pausePlayer() {
        player.playWhenReady = false
        player.playbackState
    }

    private fun startPlayer() {
        player.playWhenReady = false
        player.playbackState
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onResume() {
        super.onResume()
        startPlayer()
    }
}