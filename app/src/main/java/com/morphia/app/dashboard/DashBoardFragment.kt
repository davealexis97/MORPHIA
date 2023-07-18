package com.morphia.app.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.morphia.app.base.BaseFragment
import com.morphia.app.R
import com.morphia.app.databinding.FragmentDashBoardBinding


class DashBoardFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentDashBoardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDashBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

    }

    private fun initViews() {
        binding.btnCameraFaceSwap.setOnClickListener(this)
        binding.btnTextToSpeech.setOnClickListener(this)
        binding.btnVoiceClone.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnCameraFaceSwap -> {
                findNavController().navigate(R.id.action_nav_dashboard_to_nav_camera_main)
            }

            binding.btnTextToSpeech -> {
                findNavController().navigate(R.id.action_nav_dashboard_to_nav_text_to_speech)
            }

            binding.btnVoiceClone -> {
                findNavController().navigate(R.id.action_nav_dashboard_to_nav_record_audio)
            }
        }
    }
}