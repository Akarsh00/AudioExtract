package com.ail.audioextract.views.fragments

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.ail.audioextract.R
import com.ail.audioextract.SAVED_EDITED_MEDIA
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
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
import com.otaliastudios.transcoder.strategy.DefaultVideoStrategy
import com.otaliastudios.transcoder.strategy.TrackStrategy
import com.otaliastudios.transcoder.strategy.size.FractionResizer
import idv.luchafang.videotrimmer.VideoTrimmerView
import idv.luchafang.videotrimmerexample.getVideoFileDuration
import idv.luchafang.videotrimmerexample.setTime
import idv.luchafang.videotrimmerexample.setVideoTime
import idv.luchafang.videotrimmerexample.uriExtractorFromPath
import kotlinx.android.synthetic.main.base_options_layout.audioFormatSpinner
import kotlinx.android.synthetic.main.fragment_trim.*
import kotlinx.android.synthetic.main.trim_layout.*
import kotlinx.android.synthetic.main.trim_video_dialog.*
import java.io.File

class VideoTrimFragment : Fragment(R.layout.fragment_video_trim), VideoTrimmerView.OnSelectedRangeChangedListener {

    private val REQ_PERMISSION = 200
    private var mTrimStartingPosition: Long = 0
    private var mTrimEndPosition: Long = 0
    lateinit var builder: AlertDialog.Builder
    lateinit var player: SimpleExoPlayer
    lateinit var dataSourceFactory: DataSource.Factory
    private var mTranscodeVideoStrategy: TrackStrategy? = null


    var outputFilePath = ""

    var ffmpeg: FFmpeg? = null

    /*Video Path to trim*/
    private var videoPath: String = ""
    private var heightOfVideo: String = ""
    private var widthOfVideo: String = ""


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadFFMpegBinary()
        player = SimpleExoPlayer.Builder(requireContext()).build().also {
            it.repeatMode = SimpleExoPlayer.REPEAT_MODE_ALL
            playerView.player = it
        }


        dataSourceFactory = DefaultDataSourceFactory(requireContext(), "VideoTrimmer")
        videoPath = arguments?.let { VideoTrimFragmentArgs.fromBundle(it).videoToTrim }.toString()
        if (!videoPath.isBlank()) {
            displayTrimmerView(videoPath)
            val metaRetriever = MediaMetadataRetriever()
            metaRetriever.setDataSource(videoPath)
            heightOfVideo = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            widthOfVideo = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)

//            audioFormatSpinner.onItemSelectedListener = this
        }




        builder = AlertDialog.Builder(requireContext())

        iv_trimLayoutDone.setOnClickListener {
//            startVideoTrim()

            showDialog(requireActivity(), "hi there")
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
        //  Sink has output


        /* val complexCommand = arrayOf("-ss", "" + mTrimStartingPosition / 1000, "-y", "-i", File(videoPath).absolutePath, "-t", "" + (mTrimEndPosition - mTrimStartingPosition) / 1000, "-s", "320x240", "-r", "15", "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", getNewVideoPath())
         val moviesDir = Environment.getExternalStoragePublicDirectory(
                 Environment.DIRECTORY_MOVIES
         )

         val filePrefix = "compress_video"
         val fileExtn = ".mp4"

         var dest = File(moviesDir, filePrefix + fileExtn)
         var fileNo = 0
         while (dest.exists()) {
             fileNo++
             dest = File(moviesDir, filePrefix + fileNo + fileExtn)
         }
         val c = arrayOf("-ss", "" + mTrimStartingPosition / 1000, "-y", "-i", videoPath, "-t", "" + (mTrimEndPosition - mTrimStartingPosition) / 1000, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", getNewVideoPath())
         executeQuery(c)*/
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


    private fun startTranscode(sink: DataSink, trim: TrimDataSource, fraction: Float = 1f) {
        mTranscodeVideoStrategy = DefaultVideoStrategy.Builder()
                .addResizer(FractionResizer(fraction))
                .build()
        Transcoder.into(sink)
                .addDataSource(trim)
                .setVideoTrackStrategy(mTranscodeVideoStrategy)
                .setListener(object : TranscoderListener {
                    override fun onTranscodeCompleted(successCode: Int) {
                        displayTrimmerView(outputFilePath)
                        MediaScannerConnection.scanFile(context, arrayOf(outputFilePath), arrayOf("video/mp4"), object : MediaScannerConnection.MediaScannerConnectionClient {
                            override fun onMediaScannerConnected() {}
                            override fun onScanCompleted(s: String?, uri: Uri?) {}
                        })
                        Toast.makeText(requireContext(), "transcode completed", Toast.LENGTH_LONG).show()
                    }

                    override fun onTranscodeProgress(progress: Double) {
                    }

                    override fun onTranscodeCanceled() {
                        Toast.makeText(requireContext(), "transcode onTranscodeCanceled", Toast.LENGTH_LONG).show()
                    }

                    override fun onTranscodeFailed(exception: Throwable) {
//                        Toast.makeText(requireContext(), "transcode onTranscodeFailed", Toast.LENGTH_LONG).show()
                    }
                }).transcode()
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
        val videoPath: String = File(videoPath).name
        val outputFilePath: String =
                getDestinationPath() + "${System.currentTimeMillis()}${videoPath}"
        this.outputFilePath = outputFilePath
        return this.outputFilePath

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


    fun showDialog(activity: Activity, msg: String?) {
        var resolution = 1f //default resolution of video is 1
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.trim_video_dialog)

        val allOutputAudioFileForamt =
                arrayOf("$heightOfVideo * $widthOfVideo" to 1f, "${heightOfVideo.toFloat().div(2)} * ${widthOfVideo.toFloat().div(2)}" to 1 / 2f)
        val audioFormatArrayAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                allOutputAudioFileForamt
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        dialog.audioFormatSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                resolution = allOutputAudioFileForamt[p2].second
            }

        })
        dialog.audioFormatSpinner.adapter = audioFormatArrayAdapter
        dialog.save.setOnClickListener {
//            startVideoTrim()
            val sink: DataSink = DefaultDataSink(File(getNewVideoPath()).absolutePath)
            val source = UriDataSource(requireContext(), Uri.fromFile(File(videoPath)))
            val videoTotalLengthInTime = File(videoPath).getVideoFileDuration(requireContext())

            //    timeFromEnd-->End time  --> [TOTAL TIME -  TRIM END POSITION]
            val timeFromEnd = (videoTotalLengthInTime - mTrimEndPosition).toInt()
            val trim = TrimDataSource(source, mTrimStartingPosition * 1000, timeFromEnd * 1000.toLong())

            startTranscode(sink, trim,resolution.toFloat())
        }
        dialog.cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}