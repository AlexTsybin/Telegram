package com.example.telegram.utils

import android.media.MediaPlayer
import com.example.telegram.database.REF_DATABASE_ROOT
import com.example.telegram.database.REF_STORAGE_ROOT
import com.example.telegram.database.getFileFromStorage
import java.io.File
import java.lang.Exception

class AppVoicePlayer {

    private lateinit var mMediaPlayer: MediaPlayer
    private lateinit var mFile: File

    fun init(){
        mMediaPlayer = MediaPlayer()
    }

    fun play(messageKey: String, fileUrl: String, function: () -> Unit) {
        mFile = File(APP_ACTIVITY.filesDir, messageKey)
        if (mFile.exists() && mFile.length() > 0 && mFile.isFile) {
            startPlay() {
                function()
            }
        } else {
            // Get file from storage
            mFile.createNewFile()
            getFileFromStorage(mFile, fileUrl) {
                startPlay {
                    function()
                }
            }
        }
    }

    private fun startPlay(function: () -> Unit) {
        try {
            mMediaPlayer.setDataSource(mFile.absolutePath)
            mMediaPlayer.prepare()
            mMediaPlayer.start()
            mMediaPlayer.setOnCompletionListener {
                stop {
                    function()
                }
            }
        } catch (ex: Exception) {
            showToast(ex.message.toString())
        }
    }

    fun stop(function: () -> Unit) {
        try {
            mMediaPlayer.stop()
            mMediaPlayer.reset()
        } catch (ex: Exception) {
            showToast(ex.message.toString())
        } finally {
            function()
        }
    }

    fun release() {
        mMediaPlayer.release()
    }
}