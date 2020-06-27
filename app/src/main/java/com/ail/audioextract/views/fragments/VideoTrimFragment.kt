package com.ail.audioextract.views.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.ail.audioextract.R
import com.ail.audioextract.SAVED_EDITED_MEDIA
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.otaliastudios.transcoder.Transcoder
import com.otaliastudios.transcoder.TranscoderListener
import com.otaliastudios.transcoder.sink.DataSink
import com.otaliastudios.transcoder.sink.DefaultDataSink
import com.otaliastudios.transcoder.source.TrimDataSource
import com.otaliastudios.transcoder.source.UriDataSource
import idv.luchafang.videotrimmer.VideoTrimmerView
import idv.luchafang.videotrimmerexample.getVideoFileDuration
import idv.luchafang.videotrimmerexample.setTime
import idv.luchafang.videotrimmerexample.setVideoTime
import idv.luchafang.videotrimmerexample.uriExtractorFromPath
import kotlinx.android.synthetic.main.base_options_layout.*
import kotlinx.android.synthetic.main.fragment_trim.*
import kotlinx.android.synthetic.main.trim_layout.*
import java.io.File

class VideoTrimFragment : Fragment(R.layout.fragment_video_trim), VideoTrimmerView.OnSelectedRangeChangedListener {

    private val REQ_PERMISSION = 200
    private var mTrimStartingPosition: Long = 0
    private var mTrimEndPosition: Long = 0
    lateinit var builder: AlertDialog.Builder
    lateinit var player: SimpleExoPlayer
    lateinit var dataSourceFactory: DataSource.Factory

    var outputFilePath = ""

    /*Video Path to trim*/
    private var videoPath: String = ""


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        player = SimpleExoPlayer.Builder(requireContext()).build().also {
            it.repeatMode = SimpleExoPlayer.REPEAT_MODE_ALL
            playerView.player = it
        }

        dataSourceFactory = DefaultDataSourceFactory(requireContext(), "VideoTrimmer")
        videoPath = arguments?.let { VideoTrimFragmentArgs.fromBundle(it).videoToTrim }.toString()
        if (!videoPath.isBlank()) {
            displayTrimmerView(videoPath)
        }
        builder = AlertDialog.Builder(requireContext())

        iv_trimLayoutDone.setOnClickListener {
            startVideoTrim()
        }
        iv_trimLayoutCancel.setOnClickListener {
            displayTrimmerView(videoPath)
        }
        player.addListener(object : Player.EventListener {
            override fun onPlayerError(error: ExoPlaybackException) {
                super.onPlayerError(error)
                Toast.makeText(requireContext(), " error$error", Toast.LENGTH_LONG).show()
            }
        })


    }

    private fun startVideoTrim() {
        /* Sink has output  */
        val sink: DataSink = DefaultDataSink(File(getNewVideoPath()).absolutePath)
        val source = UriDataSource(requireContext(), Uri.fromFile(File(videoPath)))
        val videoTotalLengthInTime = File(videoPath).getVideoFileDuration(requireContext())

        /*  timeFromEnd-->End time  --> [TOTAL TIME -  TRIM END POSITION] */
        val timeFromEnd = (videoTotalLengthInTime - mTrimEndPosition).toInt()
        val trim = TrimDataSource(source, mTrimStartingPosition * 1000, timeFromEnd * 1000.toLong())
        startTranscode(sink, trim)
    }

    private fun startTranscode(sink: DataSink, trim: TrimDataSource) {
        Transcoder.into(sink)
                .addDataSource(trim)
                .setListener(object : TranscoderListener {
                    override fun onTranscodeCompleted(successCode: Int) {
                        displayTrimmerView(outputFilePath)
                        Toast.makeText(requireContext(), "transcode completed", Toast.LENGTH_LONG).show()
                    }

                    override fun onTranscodeProgress(progress: Double) {
                    }

                    override fun onTranscodeCanceled() {
                        Toast.makeText(requireContext(), "transcode onTranscodeCanceled", Toast.LENGTH_LONG).show()
                    }

                    override fun onTranscodeFailed(exception: Throwable) {
                        Toast.makeText(requireContext(), "transcode onTranscodeFailed", Toast.LENGTH_LONG).show()
                    }
                }).transcode()
    }


    private fun showOnlyTrimLayout() {
        trimLayout.visibility = View.VISIBLE
        baseLayout.visibility = View.GONE
    }

    private fun showBaseLayout() {
        trimLayout.visibility = View.GONE
        baseLayout.visibility = View.VISIBLE
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


    private fun getNewVideoPath(): String {
        val outputFilePath: String =
                getDestinationPath() + "tv_fileName.text.${".mp4"}"
        File(outputFilePath).also { it.parentFile.mkdirs() }

        this.outputFilePath = outputFilePath
        return this.outputFilePath

    }
}