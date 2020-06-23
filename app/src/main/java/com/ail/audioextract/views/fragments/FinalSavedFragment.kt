package com.ail.audioextract.views.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.ail.audioextract.R
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import idv.luchafang.videotrimmerexample.playAudioIntent
import idv.luchafang.videotrimmerexample.setRingtone
import idv.luchafang.videotrimmerexample.shareAudioIntent
import idv.luchafang.videotrimmerexample.uriExtractorFromPath
import kotlinx.android.synthetic.main.final_activity_toolbar.*
import kotlinx.android.synthetic.main.fragment_final_saved.*
import kotlinx.android.synthetic.main.fragment_final_saved.playerView
import kotlinx.android.synthetic.main.fragment_trim.*
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
            audioPath?.let { it1 -> playAudioIntent(requireContext(), it1) }
        }
        shareWith.setOnClickListener {
            audioPath?.let { it1 -> shareAudioIntent(requireContext(), it1) }
        }
        setCallerTune.setOnClickListener {
            audioPath?.let { it1 -> setRingtone(requireContext(), it1) }
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
                tv_Size.text = fileSizeInMB.toString() + " mb"

            } else {
                tv_Size.text = fileSizeInKB.toString() + " kb"

            }

        }
    }

    private fun playAudio(path: String) {
        if (path.isBlank()) return
        var buildUriFromSource = path.uriExtractorFromPath()
        val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(
                        buildUriFromSource
                )
//        player.playWhenReady = true
        player.playWhenReady = false
        player.prepare(source)

        playerView.setShutterBackgroundColor(ContextCompat.getColor(requireContext(),R.color.semi_white))
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