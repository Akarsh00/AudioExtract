package com.ail.audioextract.views.fragments

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ail.audioextract.R
import com.ail.audioextract.SAVED_EDITED_MEDIA
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import idv.luchafang.videotrimmerexample.uriExtractorFromPath
import kotlinx.android.synthetic.main.fragment_audio_trim.*
import kotlinx.android.synthetic.main.fragment_final_saved.playerView
import java.io.File


class AudioTrimFragment : Fragment(R.layout.fragment_audio_trim) {
    lateinit var player: SimpleExoPlayer
    lateinit var dataSourceFactory: DataSource.Factory
    var audioPath = ""

    private var ffmpeg: FFmpeg? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadFFMpegBinary()
        audioPath = arguments?.let { AudioTrimFragmentArgs.fromBundle(it).audioPathToTrim }.toString()

        player = SimpleExoPlayer.Builder(requireContext()).build().also {
            it.repeatMode = SimpleExoPlayer.REPEAT_MODE_ALL
            playerView.player = it
        }

        dataSourceFactory = DefaultDataSourceFactory(requireContext(), "VideoTrimmer")

        if (audioPath != null)
            playAudio(audioPath)


        startTrimAudio.setOnClickListener {
            trimAudio()
        }

    }

    private fun trimAudio() {
        val startDuration = starTrim.text
        val endPosition = endTrim.text
        val fromWhereToTrim = Integer.parseInt(player.duration.toString()) - Integer.parseInt(endPosition.toString())
        val c = arrayOf("-ss", "$startDuration", "-t", "${fromWhereToTrim}", "-i", "$audioPath", "${getNewAudioPath()}")
        executeQuery(c)

    }

    private fun executeQuery(query: Array<String>) {
        ffmpeg = FFmpeg.getInstance(requireContext());
        ffmpeg?.execute(query, object : FFmpegExecuteResponseHandler {
            override fun onFinish() {

                Toast.makeText(requireContext(), "onFinish", Toast.LENGTH_LONG).show()
            }

            override fun onSuccess(message: String?) {
                Toast.makeText(requireContext(), "onSuccess" + message, Toast.LENGTH_LONG).show()
            }

            override fun onFailure(message: String?) {
                Toast.makeText(requireContext(), "onFailure" + message, Toast.LENGTH_LONG).show()
            }

            override fun onProgress(message: String?) {
                Toast.makeText(requireContext(), "onProgress" + message, Toast.LENGTH_LONG).show()
            }

            override fun onStart() {
                Toast.makeText(requireContext(), "onStart", Toast.LENGTH_LONG).show()
            }

        });
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

    private fun loadFFMpegBinary() {
        try {
            if (ffmpeg == null) {
                Log.d("loaded", "ffmpeg : era nulo")
                ffmpeg = FFmpeg.getInstance(requireContext())
            }
            ffmpeg?.loadBinary(object : LoadBinaryResponseHandler() {
                override fun onFailure() {
                    Log.d("loaded", "ffmpeg : loading failure onFailure")

                }

                override fun onSuccess() {
                    Log.d("loaded", "ffmpeg : correct Loaded")
                }
            })
        } catch (e: FFmpegNotSupportedException) {
            Log.d("loaded", "ffmpeg : FFmpegNotSupportedException" + e.message)

        } catch (e: java.lang.Exception) {
            Log.d("loaded", "ffmpeg : FFmpegNotSupportedException" + e.message)

            Log.d("loaded", "EXception no controlada : $e")
        }
    }

    private fun getNewAudioPath(): String {
        val audiopath: String = File(audioPath).name
        val outputFilePath: String =
                getDestinationPath() + "${System.currentTimeMillis()}${audiopath}"

        return outputFilePath

    }

    private fun getDestinationPath(): String {
        val mFinalPath: String
        val folder = Environment.getExternalStorageDirectory()
        mFinalPath = folder.path + File.separator + SAVED_EDITED_MEDIA
        if (!File(mFinalPath).exists()) {
            File(mFinalPath).mkdir()
        }
        return mFinalPath + File.separator
    }

}