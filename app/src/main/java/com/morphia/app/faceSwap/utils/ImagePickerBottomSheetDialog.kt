package com.morphia.app.faceSwap.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.morphia.app.R
import com.morphia.app.databinding.ImageDialogBinding
import com.morphia.app.extension.ViewExtension
import com.morphia.app.extension.ViewExtension.requestPermission
import com.morphia.app.extension.ViewExtension.showToast
import com.morphia.app.utils.Constant
import java.util.ArrayList

class ImagePickerBottomSheetDialog : BottomSheetDialogFragment(), View.OnClickListener {

    lateinit var binding: ImageDialogBinding

    private var onViewClickedListener: OnViewClickedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)

    }

    fun setOnViewClickedListener(onViewClickedListener: OnViewClickedListener?) {
        this.onViewClickedListener = onViewClickedListener
    }

    interface OnViewClickedListener {
        fun onImageSelected(image: String)
        fun onCameraSelected()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ImageDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClick()
    }

    private fun setClick() {
        binding.linearCamera.setOnClickListener(this)
        binding.linearGallery.setOnClickListener(this)
        binding.imgClose.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.linear_gallery -> {
                val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayListOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO
                    )

                } else {
                    arrayListOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }

                requestPermission(permissions, object : ViewExtension.PermissionCallback {
                    override fun onAllGranted() {
                        galleryIntent()

                    }

                    override fun onDeny(deniedList: List<String>) {
                        showToast(getString(R.string.please_allow_given_permission))
                    }

                })

            }

            R.id.linear_camera -> {
                if (onViewClickedListener != null)
                    onViewClickedListener!!.onCameraSelected()
            }

            R.id.img_close -> {
                dismiss()
            }
        }

    }

    private fun galleryIntent() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, Constant.GALLERY_INTENT_REQUEST_CODE)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        when (requestCode) {


            Constant.GALLERY_INTENT_REQUEST_CODE ->
                if (resultCode == Activity.RESULT_OK) {

                    try {
                        val selectedImage = data!!.data
                        Log.e("TAG", "onActivityResult: $selectedImage")
                        val filePathColumn =
                            arrayOf(MediaStore.Images.Media.DATA)
                        val cursor = requireActivity().contentResolver
                            .query(
                                selectedImage!!, filePathColumn, null, null,
                                null
                            )
                        cursor!!.moveToFirst()
                        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                        val path = cursor.getString(columnIndex)

                        if (onViewClickedListener != null)
                            onViewClickedListener!!.onImageSelected(path)


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

        }

    }
}
