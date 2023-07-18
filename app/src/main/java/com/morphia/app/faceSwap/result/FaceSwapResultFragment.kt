package com.morphia.app.faceSwap.result

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.morphia.app.databinding.FragmentFaceSwapResultBinding
import com.morphia.app.utils.Constant


class FaceSwapResultFragment : Fragment() {

    lateinit var binding: FragmentFaceSwapResultBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFaceSwapResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val path = requireArguments().getParcelable(Constant.KEY_LIST1) as Uri?
            if (path != null) {
                Glide.with(requireActivity()).load(path).centerCrop().diskCacheStrategy(
                    DiskCacheStrategy.NONE).into(binding.image)
            }
        }
    }
}