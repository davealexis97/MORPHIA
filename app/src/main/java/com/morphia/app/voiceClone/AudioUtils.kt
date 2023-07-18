package com.morphia.app.voiceClone

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object AudioUtils {

    fun getAudioFileFromCache(activity: Activity): File {
        return File(
            activity.cacheDir,
            "Audio_" + System.currentTimeMillis() + ".mp3"
        )
    }

    fun getStringFromInputStream(inputStream: InputStream): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val bArr = ByteArray(1024)
        while (true) {
            try {
                val read = inputStream.read(bArr)
                if (read == -1) {
                    break
                }
                byteArrayOutputStream.write(bArr, 0, read)
            } catch (e: IOException) {
                e.printStackTrace()
                return try {
                    byteArrayOutputStream.close()
                    inputStream.close()
                    ""
                } catch (e2: IOException) {
                    e2.printStackTrace()
                    ""
                }
            } catch (th: Throwable) {
                try {
                    byteArrayOutputStream.close()
                    inputStream.close()
                } catch (e3: IOException) {
                    e3.printStackTrace()
                }
                throw th
            }
        }
        val byteArrayOutputStream2 = byteArrayOutputStream.toString("UTF-8")
        try {
            byteArrayOutputStream.close()
            inputStream.close()
        } catch (e4: IOException) {
            e4.printStackTrace()
        }
        return byteArrayOutputStream2
    }

}