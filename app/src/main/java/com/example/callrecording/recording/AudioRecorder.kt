package com.example.callrecording.recording

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.*
import android.media.audiofx.NoiseSuppressor
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.example.callrecording.utils.AppUtils
import kotlinx.coroutines.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream


class AudioRecorder(
    private val context: Context,
) {

    private var recorder: MediaRecorder? = null
    private val audioBufferSize = 1024
    private val amplificationFactor = 2.0 // Adjust this value to increase the amplitude
    private val audioBuffer = ShortArray(audioBufferSize)
    private fun processAudioData(audioData: ShortArray) {
        for (i in audioData.indices) {
            val amplifiedSample = (audioData[i] * amplificationFactor).toInt().toShort()
            audioData[i] = amplifiedSample
        }
    }
    // Process the captured audio buffer
    private fun processAudioBuffer(buffer: ShortArray) {
        processAudioData(buffer)
    }
   /* private val audioListener = AudioRecord.OnRecordPositionUpdateListener { recorder ->
        val buffer = audioBuffer
        recorder.read(buffer, 0, audioBufferSize)
        processAudioBuffer(buffer)
    }*/



    private fun getAudioSources(): List<Int> {
        val list: MutableList<Int> = ArrayList()
        list.add(MediaRecorder.AudioSource.DEFAULT)
        list.add(MediaRecorder.AudioSource.MIC)
        list.add(MediaRecorder.AudioSource.UNPROCESSED)
        list.add(MediaRecorder.AudioSource.CAMCORDER)
        list.add(MediaRecorder.AudioSource.VOICE_CALL)
        list.add(MediaRecorder.AudioSource.VOICE_DOWNLINK)
        list.add(MediaRecorder.AudioSource.VOICE_UPLINK)
        list.add(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
        list.add(MediaRecorder.AudioSource.VOICE_RECOGNITION)
        return list
    }


    @Suppress("DEPRECATION")
    private fun initializeMediaRecorder() {
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(context)
        else MediaRecorder()
    }
   private fun increaseCallVolume(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
     //    audioManager.setParameters("noise_suppression=on")

        // Increase the volume by one level
        if (currentVolume < maxVolume) {
            audioManager.setStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                currentVolume + 1,
                AudioManager.FLAG_SHOW_UI
            )
        }
    }

    fun start(destination: File) {
        val audioSource = getAudioSources()[8]//[PrefrenceUtils.getAudioSource(context).toInt()]

        increaseCallVolume(context)
        initializeMediaRecorder()

        //startAudioSpeaker()
        recorder?.apply {
            if (Build.VERSION.SDK_INT >= 10) {
                setAudioSamplingRate(44100)
                setAudioEncodingBitRate(96000)
            }else{
                setAudioSamplingRate(8000)
                setAudioEncodingBitRate(12200)
            }
            setAudioSource(audioSource)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(FileOutputStream(destination).fd)
            maxAmplitude
            prepare()
            start()

        }

        AppUtils.writeLogs("Audio call recording now....")
    }



    fun pause() {
        AppUtils.writeLogs("Recording pause ....")
        recorder?.pause()

    }

    fun resume() {
        AppUtils.writeLogs("Recording resume ....")
        recorder?.resume()
    }

    fun stop() {
        //  wakeLock.release()
        try {
            if(recorder != null) {
                recorder?.stop()
                recorder?.reset()
            }
        }
        catch (e :Exception)
        {
            AppUtils.writeLogs("Unable to stop Recroding$e")
        }

        recorder = null
    }





}