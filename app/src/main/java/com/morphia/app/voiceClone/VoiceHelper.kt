package com.morphia.app.voiceClone

import android.content.Context
import android.os.Environment
import android.util.Log
import com.reactlibrary.basseffect.DBAudioManager
import com.reactlibrary.basseffect.DBMediaPlayer
import com.reactlibrary.basseffect.IDBMediaListener
import com.reactlibrary.constants.IVoiceChangerConstants
import com.reactlibrary.dataMng.JsonParsingUtils
import com.reactlibrary.`object`.EffectObject
import com.reactlibrary.utils.StringUtils
import java.io.File

class VoiceHelper(val context: Context) {

    private var effectObjects: ArrayList<EffectObject>? = null
    private var mPathAudio: String? = null
    private var mDBMedia: DBMediaPlayer? = null
    private var isInit = false
    private var playingIndex: Int? = null
    private var outputDir: File? = null
    private val mNameExportVoice: String? = null

    init {
        val dbAudioManager = DBAudioManager.getInstance()
        dbAudioManager.onInitAudioDevice(context)
        effectObjects = arrayListOf()
    }


    fun createOutputDir() {
        this.outputDir = this.getDir()
    }


    private fun getDir(): File {
        val dirpath = Environment.getExternalStorageDirectory().path
        val dir = File(dirpath, IVoiceChangerConstants.NAME_FOLDER_RECORD)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun setPath(path: String?) {
        this.mPathAudio = path
    }

    private fun onCreateDBMedia() {
        if (!StringUtils.isEmptyString(mPathAudio)) {
            mDBMedia = DBMediaPlayer(mPathAudio)
            mDBMedia?.prepareAudio()
            mDBMedia?.setOnDBMediaListener(object : IDBMediaListener {
                override fun onMediaError() {}
                override fun onMediaCompletion() {
                    effectObjects!![playingIndex!!].isPlaying = false
                    setPlayingIndex(null)

                }
            })
        }
    }

    fun createDBMedia() {
        onCreateDBMedia()
    }

    fun setPlayingIndex(idx: Int?) {
        if (idx != null) {
            playingIndex = idx
        }
    }

    fun insertEffect(effect: String?) {
        effectObjects!!.add(JsonParsingUtils.jsonToEffectObject(effect))
    }

    fun getEffects(): ArrayList<EffectObject>? {
        return effectObjects
    }


    fun playEffect(effectIndex: Int) {
        Log.d("TAG", "audioPath: " + mPathAudio)
        if (!StringUtils.isEmptyString(mPathAudio)) {
            val mFile = File(mPathAudio)
            if (!(mFile.exists() && mFile.isFile)) {
                //todo File not found exception
                Log.d("TAG", "File not found exception")
            }
        }
        try {
            setPlayingIndex(effectIndex)
            onPlayEffect(effectObjects!![effectIndex])

        } catch (ex: java.lang.Exception) {
            Log.d("TAG", " exception",ex)
        }
    }

    private fun onPlayEffect(mEffectObject: EffectObject) {
        val isPlaying = mEffectObject.isPlaying
        if (isPlaying) {
            mEffectObject.isPlaying = false
            if (mDBMedia != null) {
                mDBMedia!!.pauseAudio()
            }

        } else {

            mEffectObject.isPlaying = true
            if (mDBMedia != null) {
                mDBMedia!!.setPathMix(mEffectObject.pathMix)
                mDBMedia!!.setNeedMix(mEffectObject.isMix)
                mDBMedia!!.prepareAudio()
                mDBMedia!!.setReverse(mEffectObject.isReverse)
                mDBMedia!!.setAudioPitch(mEffectObject.pitch)
                mDBMedia!!.setCompressor(mEffectObject.compressor)
                mDBMedia!!.setAudioRate(mEffectObject.rate)
                mDBMedia!!.setAudioEQ1(mEffectObject.eq1)
                mDBMedia!!.setAudioEQ2(mEffectObject.eq2)
                mDBMedia!!.setAudioEQ3(mEffectObject.eq3)
                mDBMedia!!.setPhaser(mEffectObject.phaser)
                mDBMedia!!.setAutoWah(mEffectObject.autoWah)
                mDBMedia!!.setAudioReverb(mEffectObject.reverb)
                mDBMedia!!.setEcho4Effect(mEffectObject.echo4)
                mDBMedia!!.setAudioEcho(mEffectObject.echo)
                mDBMedia!!.setBiQuadFilter(mEffectObject.filter)
                mDBMedia!!.setFlangeEffect(mEffectObject.flange)
                mDBMedia!!.setChorus(mEffectObject.chorus)
                mDBMedia!!.setAmplify(mEffectObject.amplify)
                mDBMedia!!.setDistort(mEffectObject.distort)
                mDBMedia!!.setRotate(mEffectObject.rotate)
                mDBMedia!!.startAudio()
            }
        }
    }


}