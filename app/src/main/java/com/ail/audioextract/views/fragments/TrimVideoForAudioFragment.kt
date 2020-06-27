package com.ail.audioextract.views.fragments

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.ail.audioextract.AudioExtractor
import com.ail.audioextract.R
import com.ail.audioextract.SAVED_EDITED_MEDIA
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import idv.luchafang.videotrimmer.VideoTrimmerView
import idv.luchafang.videotrimmerexample.getVideoFileDuration
import idv.luchafang.videotrimmerexample.setTime
import idv.luchafang.videotrimmerexample.setVideoTime
import idv.luchafang.videotrimmerexample.uriExtractorFromPath
import kotlinx.android.synthetic.main.base_options_layout.*
import kotlinx.android.synthetic.main.fragment_trim.*
import kotlinx.android.synthetic.main.trim_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class TrimVideoForAudioFragment : Fragment(R.layout.fragment_trim), VideoTrimmerView.OnSelectedRangeChangedListener,
        AdapterView.OnItemSelectedListener {

    private val REQ_PERMISSION = 200
    private val allOutputAudioFileForamts =
            arrayOf("MP3", "AAC")
    private var mTrimStartingPosition: Long = 0
    private var mTrimEndPosition: Long = 0
    private var mOutputAudioFileFormat = "mp3"
    lateinit var builder: AlertDialog.Builder
    var outputFilePath = ""
    lateinit var  player: SimpleExoPlayer
    lateinit var dataSourceFactory: DataSource.Factory




    /*Video Path to trim*/
    private var videoPath: String = ""
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        player = SimpleExoPlayer.Builder(requireContext()).build().also {
                it.repeatMode = SimpleExoPlayer.REPEAT_MODE_ALL
                playerView.player = it
            }

        dataSourceFactory=  DefaultDataSourceFactory(requireContext(), "VideoTrimmer")



        videoPath = arguments?.let { TrimVideoForAudioFragmentArgs.fromBundle(it).videoToTrim }.toString()
    if (!videoPath.equals("")) {
        displayTrimmerView(videoPath)

    }
        builder = AlertDialog.Builder(requireContext())
        /*tv_fileName is the name of output_audio file */
        tv_fileName.setText(resetFileName())


        val audioFormatArrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                allOutputAudioFileForamts
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        audioFormatSpinner.adapter = audioFormatArrayAdapter
        audioFormatSpinner.onItemSelectedListener = this

        fetchAudioFromSelectedPosition.setOnClickListener {
            extractAudioFiles()
        }

        showTrimLayoutButton.setOnClickListener {
            if (videoPath.isNotEmpty())
                showOnlyTrimLayout()
            else {
                Toast.makeText(context, "Please select a video.", Toast.LENGTH_LONG)
                        .show()
            }
        }


        iv_trimLayoutDone.setOnClickListener {
            /* Base Layout is view below video player where all options are given */
            showBaseLayout()
        }
        iv_trimLayoutCancel.setOnClickListener {
            showBaseLayout()
            displayTrimmerView(videoPath)

        }
        player.addListener(object : Player.EventListener {
            override fun onPlayerError(error: ExoPlaybackException) {
                super.onPlayerError(error)
                Toast.makeText(requireContext(), "error" + error.toString(), Toast.LENGTH_LONG).show()
            }
        })


    }


    private fun extractAudioFiles() {
        if (videoPath.isNotEmpty()) {
            builder.setView(R.layout.progress_dialog)
            val dialog: Dialog = builder.create()
            dialog.setCancelable(false)
            dialog.show()

            CoroutineScope(Dispatchers.IO).launch {
                fetchAudioFile()

                withContext(Dispatchers.Main) {
                    dialog.dismiss()
                    tv_fileName.setText(resetFileName())

                    val action =
                            TrimVideoForAudioFragmentDirections.actionTrimFragmentToFinalSavedFragment(
                                    outputFilePath
                            )
                    findNavController(requireParentFragment()).navigate(action)
                }
            }


        } else {
            Toast.makeText(requireContext(), "Please select a video.", Toast.LENGTH_LONG)
                    .show()
        }
    }

    private fun resetFileName():String {
        var name=File(videoPath).name
        if (name.indexOf(".") > 0)
            name = name.substring(0, name.lastIndexOf("."))
        return name + System.currentTimeMillis().toString()
    }

    private fun showOnlyTrimLayout() {
        trimLayout.visibility = View.VISIBLE
        baseLayout.visibility = View.GONE
    }

    private fun showBaseLayout() {
        trimLayout.visibility = View.GONE
        baseLayout.visibility = View.VISIBLE
    }

    private suspend fun fetchAudioFile() {
        if (videoPath != "") {
            val outputFilePath: String =
                    getDestinationPath() + "${tv_fileName.text}.${".$mOutputAudioFileFormat"}"
            File(outputFilePath).also { it.parentFile.mkdirs() }

            this.outputFilePath = outputFilePath
            AudioExtractor().genVideoUsingMuxer(
                    videoPath,
                    outputFilePath,
                    mTrimStartingPosition,
                    mTrimEndPosition,
                    true,
                    false
            )
            val file = File(outputFilePath)
            val uri = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri)

            requireContext().sendBroadcast(intent)
        }

    }

    private fun getDestinationPath(): String {
        val mFinalPath: String
        val folder = Environment.getExternalStorageDirectory()
        mFinalPath = folder.path + File.separator +/* "SAVED_AUDIO_DIR_NAME"*/SAVED_EDITED_MEDIA
        if (!File(mFinalPath).exists()) {
            File(mFinalPath).mkdir()
        }
        return mFinalPath + File.separator
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
            requireActivity().finish()
        }
    }

    /* -------------------------------------------------------------------------------------------*/
    /* VideoTrimmerView.OnSelectedRangeChangedListener */
    override fun onSelectRangeStart() {
        player.playWhenReady = false
    }

    override fun onSelectRange(startMillis: Long, endMillis: Long) {
        updateDurationInLayout(startMillis, endMillis)
    }

    override fun onSelectRangeEnd(startMillis: Long, endMillis: Long) {
        updateDurationInLayout(startMillis, endMillis)
        playVideo(videoPath, startMillis, endMillis)
    }

    /* -------------------------------------------------------------------------------------------*/
    /* VideoTrimmer */
    private fun displayTrimmerView(path: String) {
        videoTrimmerView
                .setVideo(File(path))
                .setMaxDuration(File(path).getVideoFileDuration(requireContext()))
                .setMinDuration(1)
                .setFrameCountInWindow(10)
                .setOnSelectedRangeChangedListener(this)
                .show()
    }


    private fun playVideo(path: String, startMillis: Long, endMillis: Long) {
        if (path.isBlank()) return
        var buildUriFromSource = path.uriExtractorFromPath()
        val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(
                        buildUriFromSource
                )
                .let {
                    ClippingMediaSource(
                            it,
                            startMillis * 1000L,
                            endMillis * 1000L
                    )
                }

        player.playWhenReady = true
        player.prepare(source)
    }


    private fun updateDurationInLayout(startMillis: Long, endMillis: Long) {
        mTrimStartingPosition = startMillis
        mTrimEndPosition = endMillis
        val duration = (endMillis - startMillis)

        tv_StartSeek.setVideoTime(startMillis.toInt())
        tv_EndSeek.setVideoTime(endMillis.toInt())
        durationView.setTime(duration.toInt())

    }


    override fun onNothingSelected(p0: AdapterView<*>?) = Unit

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        mOutputAudioFileFormat = allOutputAudioFileForamts[p2]
    }


    private fun pausePlayer() {
        player.playWhenReady = false
        player.playbackState
    }

    private fun startPlayer() {
        player.playWhenReady = true
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