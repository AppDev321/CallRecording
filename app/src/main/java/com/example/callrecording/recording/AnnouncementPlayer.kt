package com.example.callrecording.recording

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.*
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.callrecording.MyApplication
import com.example.callrecording.utils.AppUtils
import com.example.callrecording.utils.PrefrenceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException
import kotlin.time.Duration.Companion.seconds


class AnnouncementPlayer(private val context: Context) {

    companion object {
        val announcementPath = File(MyApplication.preRecordingFilePath)
    }

    fun setVolumeAdjustment()
    {
      /*  val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume  = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,maxVolume, 0)
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, -1, 0)

*/
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION)
        } else {
            audioManager.setMode(AudioManager.MODE_IN_CALL)
        }
        when (audioManager.getMode()) {
            AudioManager.MODE_IN_CALL -> println("MODE_IN_CALL")
            AudioManager.MODE_IN_COMMUNICATION -> println("MODE_IN_COMMUNICATION")
            AudioManager.MODE_NORMAL -> println("MODE_NORMAL")
            AudioManager.MODE_RINGTONE -> println("MODE_RINGTONE")
            AudioManager.MODE_INVALID -> println("MODE_INVALID")
            AudioManager.STREAM_VOICE_CALL -> println("STREAM_VOICE_CALL")
        }
        audioManager.setRouting(AudioManager.MODE_NORMAL, AudioManager.ROUTE_EARPIECE, AudioManager.ROUTE_ALL)

// Set the audio mode to MODE_IN_CALL
//        audioManager.mode = AudioManager.MODE_IN_CALL

        // Set the audio stream type to STREAM_VOICE_CALL
        // audioManager.isSpeakerphoneOn = false
        // audioManager.isMicrophoneMute = false
        // audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0)
        // Request audio focus
        // Request audio focus

/*
        val inputStream = context.resources.openRawResource(R.raw.reco)
        val byteArrayOutputStream = ByteArrayOutputStream()

        var read: Int
        val buffer = ByteArray(1024)

        while (inputStream.read(buffer).also { read = it } != -1) {
            byteArrayOutputStream.write(buffer, 0, read)
        }

        byteArrayOutputStream.flush()

        val bytes = byteArrayOutputStream.toByteArray()

        val audioTrack = AudioTrack(AudioManager.STREAM_VOICE_CALL, 44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bytes.size, AudioTrack.MODE_STATIC)

        audioTrack.write(bytes, 0, bytes.size)
        audioTrack.play()*/

      val result =
          if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                    .setOnAudioFocusChangeListener(afChangeListener).build()
            )
        } else {
            audioManager.requestAudioFocus(
                afChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }


        AppUtils.writeLogs("Result Audio focus === $result")




    }

    fun playAnnouncement(isIncomingCall: Boolean) {


        val outgoingDelay = Integer.valueOf(
            PrefrenceUtils.getOutgoingAnnouncementSeconds(context)
        )
        val incomingDelay = Integer.valueOf(
            PrefrenceUtils.getIncomingAnnouncementSeconds(context)
        )

        val recordingAnnouncementStatus = PrefrenceUtils.getAnnouncementStatus(context)
        if (recordingAnnouncementStatus) {
            CoroutineScope(Dispatchers.Main).launch {
                if (!isIncomingCall) {
                    AppUtils.writeLogs("Outgoing call delay = $outgoingDelay seconds")
                    delay(outgoingDelay.seconds)
                } else {
                    AppUtils.writeLogs("Incoming call delay = $incomingDelay seconds")
                    delay(incomingDelay.seconds)
                }


           //   setVolumeAdjustment()

                 playPreRecording(
                     announcementPath,{

                     },{error->
                         Handler(Looper.getMainLooper()).post {
                             AppUtils.showToastMessage(context, "Exception: $error")
                         }
                     },false
                 )
            }

        }

    }

    @SuppressLint("MissingPermission")
    private fun playPreRecording(
        file: File,
        preRecordingFinished: () -> Unit,
        errorPreRecording: (String) -> Unit,
        playFromAssets: Boolean = false
    ) {
        CoroutineScope(Dispatchers.IO).launch {

            val filePath = file.absolutePath
            val mediaPlayer = MediaPlayer()
            try {

                //Playing from assets
                val assetsFile = "record2.mp3"
                val descriptor: AssetFileDescriptor = context.assets.openFd(assetsFile)

                if (playFromAssets) {
                    mediaPlayer.setDataSource(
                        descriptor.fileDescriptor,
                        descriptor.startOffset,
                        descriptor.length
                    )
                } else {
                    mediaPlayer.setDataSource(filePath)
                }

                descriptor.close()

                mediaPlayer.prepare()
                mediaPlayer.setVolume(1.0f, 1.0f)
                mediaPlayer.start()


                mediaPlayer.setOnCompletionListener {
                    AppUtils.writeLogs("Pre-Recording finished")
                    preRecordingFinished()
                }
            } catch (ex: FileNotFoundException) {
                AppUtils.writeLogs("Pre-Recording File not found now play default sound")
                playPreRecording(file, preRecordingFinished, errorPreRecording, true)


            } catch (e: Exception) {
                AppUtils.writeLogs("Pre-Recording Error == ${e.toString()}")
                errorPreRecording(e.toString())
                preRecordingFinished()
            }
        }
    }
    private var afChangeListener: OnAudioFocusChangeListener =
        OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    Toast.makeText(context, "FOCUS LOSS TRANSIENT", Toast.LENGTH_SHORT).show()
                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    Toast.makeText(context, "FOCUS LOST", Toast.LENGTH_SHORT).show()
                }
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT -> {
                    Toast.makeText(context, "AUDIOFOCUS_GAIN_TRANSIENT", Toast.LENGTH_SHORT).show()
                }
                AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {
                    Toast.makeText(context, "AUDIOFOCUS_REQUEST_FAILED", Toast.LENGTH_SHORT).show()
                }
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                    Toast.makeText(context, "AUDIOFOCUS_REQUEST_GRANTED", Toast.LENGTH_SHORT).show()
                }
            }
        }
}