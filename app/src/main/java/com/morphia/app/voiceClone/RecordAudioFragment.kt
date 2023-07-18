package com.morphia.app.voiceClone

import android.Manifest
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.morphia.app.extension.ViewExtension
import com.morphia.app.extension.ViewExtension.requestPermission
import com.morphia.app.extension.ViewExtension.showToast
import com.morphia.app.utils.Constant
import com.morphia.app.R
import com.morphia.app.databinding.FragmentRecordAudioBinding
import java.io.File


class RecordAudioFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentRecordAudioBinding
    var isRecording: Boolean = false
    private var mRecorder: MediaRecorder? = null
    var audioFile: File? = null
    var audioRecord: AudioRecorder? = null
    var isRecorded: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecordAudioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audioRecord = AudioRecorder(requireContext())
        isRecorded = false
        setClicks()

    }

    private fun setClicks() {
        binding.btnRecordAudio.setOnClickListener(this)
        binding.btnEffects.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnRecordAudio -> {
                requestPermission(arrayListOf(Manifest.permission.RECORD_AUDIO),
                    object : ViewExtension.PermissionCallback {
                        override fun onAllGranted() {
                            if (isRecording) {
                                stopRecording()
                            } else {
                                recordAudio()
                            }

                        }

                        override fun onDeny(deniedList: List<String>) {
                            showToast(getString(R.string.allow_permission_settings))
                        }

                    })
            }

            binding.btnEffects -> {
                if (isRecorded) {
                    val bundle = Bundle().apply {
                        putString(Constant.AUDIO_FILE_PATH, audioRecord?.fileName)
                    }
                    findNavController().navigate(
                        R.id.action_nav_record_audio_to_nav_effects,
                        bundle
                    )
                } else {
                    showToast(R.string.please_record_audio)
                }

            }
        }
    }

    private fun stopRecording() {
        if (mRecorder != null) {
            mRecorder!!.stop()
            mRecorder!!.release()
            mRecorder = null
            isRecording = false
            isRecorded = true
            binding.btnRecordAudio.text = getString(R.string.record_audio)
        }
    }

    private fun recordAudio() {
        mRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(requireContext())
        } else {
            MediaRecorder()
        }

        // below method is used to set the audio
        // source which we are using a mic.
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)

        // below method is used to set
        // the output format of the audio.
        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)

        // below method is used to set the
        // audio encoder for our recorded audio.
        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

        // below method is used to set the
        // output file location for our recorded audio
        audioFile = AudioUtils.getAudioFileFromCache(requireActivity())

        audioFile?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mRecorder!!.setOutputFile(it)
            } else {
                mRecorder!!.setOutputFile(it.absolutePath)
            }
        }



        try {
            mRecorder!!.prepare()
            mRecorder!!.start()
            isRecording = true
            binding.btnRecordAudio.text = getString(R.string.stop_recording)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            isRecording = false
        }


    }
}