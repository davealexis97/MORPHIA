package com.morphia.app.textToSpeech

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.morphia.app.R
import com.morphia.app.databinding.FragmentTextToSpeechBinding
import com.morphia.app.extension.ViewExtension.showToast
import com.morphia.app.modal.VoiceModal

import net.gotev.speech.Speech
import net.gotev.speech.TextToSpeechCallback


class TextToSpeechFragment : Fragment(), View.OnClickListener, VoiceAdapter.OnItemClick {

    lateinit var binding: FragmentTextToSpeechBinding

    lateinit var adapter: VoiceAdapter
    var pitch: Float = 0.8f


    companion object {
        const val TAG = "TextToSpeechFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTextToSpeechBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClicks()
        initVoiceAdapter()
        initTextSpeech()


    }

    private fun initVoiceAdapter() {
        adapter = VoiceAdapter(this)
        binding.rvVoices.adapter = adapter
        binding.rvVoices.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Speech.getInstance().shutdown()
    }

    private fun initTextSpeech() {
        Speech.init(
            requireContext(), requireActivity().packageName
        ) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val voiceList = arrayListOf<VoiceModal>()
                val list = Speech.getInstance().supportedTextToSpeechVoices
                list.forEach { voice ->
                    val modal = VoiceModal(voice = voice)
                    voiceList.add(modal)
                }

                val currentVoice = Speech.getInstance().textToSpeechVoice
                if (currentVoice != null) {
                    Log.e(TAG, "initTextSpeech: current  ${currentVoice.name}")
                }

                voiceList.forEach { voiceModal ->
                    run {
                        Log.e(TAG, "initTextSpeech: list  ${voiceModal.voice}")
                        if (voiceModal.voice == currentVoice) {
                            voiceModal.isSelected = true
                            Log.e(TAG, "initTextSpeech: map ${voiceModal.voice.name}")
                        }
                    }
                }
                adapter.setVoices(voiceList)
                binding.btnConvertToSwitch.isEnabled = true
            } else {
                binding.btnConvertToSwitch.isEnabled = false
            }

        }

    }

    private fun setClicks() {
        binding.btnConvertToSwitch.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnConvertToSwitch -> {
                textToSpeak()

            }

        }
    }


    private fun textToSpeak() {
        val text = binding.etTextForSpeech.text.toString().trim()
        if (text.isNotEmpty() || text.isNotBlank()) {


            Speech.getInstance().say(text, object : TextToSpeechCallback {
                override fun onStart() {
                    Log.i("speech", "speech started")
                }

                override fun onCompleted() {
                    Log.i("speech", "speech completed")
                }

                override fun onError() {
                    Log.i("speech", "speech error")
                }
            })


        } else {
            showToast(getString(R.string.enter_text))
        }
    }

    override fun onVoiceClick(voice: Voice) {
        Speech.getInstance().setVoice(voice)
    }
}