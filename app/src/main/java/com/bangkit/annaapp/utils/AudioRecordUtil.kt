package com.bangkit.annaapp.utils

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import java.io.File

private var mediaRecorder: MediaRecorder? = null
private var audioFile: File? = null

fun startRecording(context: Context): File {
    audioFile = createTempAudioFile(context)
    mediaRecorder = MediaRecorder().apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.OGG)
        setAudioEncoder(MediaRecorder.AudioEncoder.OPUS)
//        setAudioSamplingRate(22050)
        setOutputFile(audioFile?.absolutePath)
        prepare()
        start()
    }
    return audioFile!!
}

fun stopRecording() {
    mediaRecorder?.stop()
    mediaRecorder?.release()
    mediaRecorder = null
}

private fun createTempAudioFile(context: Context): File {
    val storageDir = context.externalCacheDir
    return File.createTempFile("AUDIO_${System.currentTimeMillis()}", ".opus", storageDir)
}