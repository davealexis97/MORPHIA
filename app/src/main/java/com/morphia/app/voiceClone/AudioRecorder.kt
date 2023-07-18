package com.morphia.app.voiceClone

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import java.io.*


class AudioRecorder(val context: Context) {

    private val RECORDER_BPP = 16
    private val AUDIO_RECORDER_FILE_EXT_WAV = ".wav"
    private val AUDIO_RECORDER_FOLDER = "AudioRecorder"
    private val AUDIO_RECORDER_TEMP_FILE = "record_temp.raw"
    private val RECORDER_SAMPLERATE = 44100
    private val RECORDER_CHANNELS: Int = AudioFormat.CHANNEL_IN_STEREO
    private val RECORDER_AUDIO_ENCODING: Int = AudioFormat.ENCODING_PCM_16BIT


    private var recorder: AudioRecord? = null
    private var bufferSize = 0
    private var recordingThread: Thread? = null
    var isRecording = false
    var fileName: String? = null

    init {
        bufferSize = AudioRecord.getMinBufferSize(
            8000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        fileName = getFilename()

    }

    private fun getFilename(): String {
        val filepath: String = Environment.getExternalStorageDirectory().path
        val file = File(filepath, AUDIO_RECORDER_FOLDER)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_WAV
    }

    private fun getTempFilename(): String {
        val filepath = context.cacheDir
        val file = File(filepath, AUDIO_RECORDER_FOLDER)
        if (!file.exists()) {
            file.mkdirs()
        }
        val tempFile = File(file, AUDIO_RECORDER_TEMP_FILE)
        if (tempFile.exists()) tempFile.delete()
        return file.absolutePath + "/" + AUDIO_RECORDER_TEMP_FILE
    }


    @SuppressLint("MissingPermission")
    fun startRecording() {
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize
        )
        val i = recorder!!.state
        if (i == 1) recorder!!.startRecording()
        isRecording = true
        recordingThread = Thread({ writeAudioDataToFile() }, "AudioRecorder Thread")
        recordingThread!!.start()
    }


    private fun writeAudioDataToFile() {
        val data = ByteArray(bufferSize)
        val filename = getTempFilename()
        var os: FileOutputStream? = null
        try {
            os = FileOutputStream(filename)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        var read = 0
        if (null != os) {
            while (isRecording) {
                read = recorder!!.read(data, 0, bufferSize)
                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        os.write(data)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            try {
                os.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun stopRecording() {
        if (null != recorder) {
            isRecording = false
            val i = recorder!!.state
            if (i == 1) recorder!!.stop()
            recorder!!.release()
            recorder = null
            recordingThread = null
        }
        copyWaveFile(getTempFilename(), fileName!!)
        deleteTempFile()
    }

    private fun deleteTempFile() {
        val file = File(getTempFilename())
        file.delete()
    }

    private fun copyWaveFile(inFilename: String, outFilename: String) {
        var `in`: FileInputStream? = null
        var out: FileOutputStream? = null
        var totalAudioLen: Long = 0
        val totalDataLen: Long
        val longSampleRate = RECORDER_SAMPLERATE.toLong()
        val channels = 2
        val byteRate = (RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8).toLong()
        val data = ByteArray(bufferSize)
        try {
            `in` = FileInputStream(inFilename)
            out = FileOutputStream(outFilename)
            totalAudioLen = `in`.channel.size()
            totalDataLen = totalAudioLen + 36

            writeWaveFileHeader(
                out, totalAudioLen, totalDataLen,
                longSampleRate, channels, byteRate
            )
            while (`in`.read(data) != -1) {
                out.write(data)
            }
            `in`.close()
            out.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun writeWaveFileHeader(
        out: FileOutputStream, totalAudioLen: Long,
        totalDataLen: Long, longSampleRate: Long, channels: Int,
        byteRate: Long
    ) {
        val header = ByteArray(44)
        header[0] = 'R'.code.toByte() // RIFF/WAVE header
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xffL).toByte()
        header[5] = (totalDataLen shr 8 and 0xffL).toByte()
        header[6] = (totalDataLen shr 16 and 0xffL).toByte()
        header[7] = (totalDataLen shr 24 and 0xffL).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte() // 'fmt ' chunk
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16 // 4 bytes: size of 'fmt ' chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // format = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (longSampleRate and 0xffL).toByte()
        header[25] = (longSampleRate shr 8 and 0xffL).toByte()
        header[26] = (longSampleRate shr 16 and 0xffL).toByte()
        header[27] = (longSampleRate shr 24 and 0xffL).toByte()
        header[28] = (byteRate and 0xffL).toByte()
        header[29] = (byteRate shr 8 and 0xffL).toByte()
        header[30] = (byteRate shr 16 and 0xffL).toByte()
        header[31] = (byteRate shr 24 and 0xffL).toByte()
        header[32] = (2 * 16 / 8).toByte() // block align
        header[33] = 0
        header[34] = RECORDER_BPP.toByte() // bits per sample
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xffL).toByte()
        header[41] = (totalAudioLen shr 8 and 0xffL).toByte()
        header[42] = (totalAudioLen shr 16 and 0xffL).toByte()
        header[43] = (totalAudioLen shr 24 and 0xffL).toByte()
        out.write(header, 0, 44)
    }


}

