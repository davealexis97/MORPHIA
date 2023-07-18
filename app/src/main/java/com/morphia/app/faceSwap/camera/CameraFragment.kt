package com.morphia.app.faceSwap.camera

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.morphia.app.base.BaseFragment
import com.morphia.app.extension.ViewExtension.showToast
import com.morphia.app.faceSwap.utils.FaceDetectorEngine
import com.morphia.app.faceSwap.utils.ImagePickerBottomSheetDialog
import com.morphia.app.faceSwap.utils.Landmarks
import com.morphia.app.faceSwap.utils.Swap
import com.morphia.app.utils.Constant
import com.morphia.app.utils.ImageUtils
import com.morphia.app.R
import com.morphia.app.databinding.FragmentCameraBinding
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.PictureFormat


class CameraFragment : BaseFragment(), View.OnClickListener {


    private var faceIndex: Int = 0
    lateinit var binding: FragmentCameraBinding

    private lateinit var bitmap1: Bitmap
    private lateinit var bitmap2: Bitmap
    private lateinit var bitmap1Swapped: Bitmap
    private lateinit var bitmap2Swapped: Bitmap

    private var face1Done = false
    private var face2Done = false
    private var okToSwap = false
    private var hasSwapped = false

    private val desiredWidth = 800
    private val desiredHeight = 800

    private lateinit var faces1: List<Face>
    private lateinit var faces2: List<Face>

    private val faceDetectorEngine = FaceDetectorEngine()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCamera()
        setClick()
    }

    private fun setClick() {
        binding.ivCapture.setOnClickListener(this)
    }

    private fun initCamera() {
        binding.camera.setLifecycleOwner(viewLifecycleOwner)
        binding.camera.pictureFormat = PictureFormat.JPEG
        binding.camera.addCameraListener(object : CameraListener() {

            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                result.toBitmap { bitmap ->
                    if (bitmap != null) {
//                        val saveImageUri = ImageUtils.saveBitmapToCache(requireActivity(), bitmap)
                        processSelfieImage(bitmap, faceIndex)
                    }
                }
            }
        })
    }

    private fun processSelfieImage(bitmap: Bitmap, faceIndex: Int) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        faceDetectorEngine.detectInImage(inputImage).addOnSuccessListener { faces ->

            val notEmpty = faces.isNotEmpty()
            if (notEmpty) {
                if (faceIndex == 0) {
                    face1Done = true
                    faces1 = faces
                    bitmap1 = bitmap
                    openOptions()
                } else if (faceIndex == 1) {
                    face2Done = true
                    faces2 = faces
                    bitmap2 = bitmap
                }

                okToSwap = face1Done && face2Done
                if (okToSwap) {
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Proceed")
                    alertDialog.setMessage("Are you sure u want to face swap")
                    alertDialog.setPositiveButton(
                        "Swap"
                    ) { dialog, _ ->
                        swapFace()


                    }

                    alertDialog.setNegativeButton(
                        "Cancel"
                    ) { dialog, _ -> dialog?.dismiss() }

                    alertDialog.show()
                }

            } else {
                showToast(getString(R.string.face_not_found))
            }

        }
    }

    private fun swapFace() {
        if (okToSwap) {
            val landmarksForFaces1 = Landmarks.arrangeLandmarksForFaces(faces1)
            val landmarksForFaces2 = Landmarks.arrangeLandmarksForFaces(faces2)
            bitmap2Swapped =
                Swap.faceSwapAll(bitmap1, bitmap2, landmarksForFaces1, landmarksForFaces2)

            val path = ImageUtils.saveBitmapToCache(requireActivity(), bitmap2Swapped)

            val arg = Bundle()
            arg.putParcelable(Constant.KEY_LIST1, path)
            findNavController().navigate(R.id.action_nav_camera_main_to_nav_faceswap, arg)

        }

    }

    private fun openOptions() {
        val dialog = ImagePickerBottomSheetDialog()

        dialog.setOnViewClickedListener(object :
            ImagePickerBottomSheetDialog.OnViewClickedListener {

            override fun onImageSelected(image: String) {
                dialog.dismiss()
                Glide.with(requireActivity())
                    .asBitmap()
                    .load(image)
                    .into(object : CustomTarget<Bitmap>(desiredWidth, desiredHeight) {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            processSelfieImage(resource, 1)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })


            }

            override fun onCameraSelected() {
                faceIndex = 1
                dialog.dismiss()
            }

        })

        dialog.show(childFragmentManager, getString(R.string.image_picker))
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.ivCapture -> {
                binding.camera.takePicture()
            }
        }
    }
}