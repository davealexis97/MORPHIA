package com.morphia.app.user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.morphia.app.MainActivity
import com.morphia.app.R
import com.morphia.app.base.BaseActivity
import com.morphia.app.base.Status
import com.morphia.app.databinding.ActivityRegisterBinding
import com.morphia.app.extension.ViewExtension.asColor
import com.morphia.app.extension.ViewExtension.showToast
import com.morphia.app.faceSwap.utils.ImagePickerBottomSheetDialog
import com.morphia.app.modal.UserDataModel
import kotlinx.coroutines.tasks.await
import java.io.File

class RegisterActivity : BaseActivity(), View.OnClickListener {

    private var imageUrl: String? = null
    lateinit var binding: ActivityRegisterBinding
    val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        setClicks()
        subscribeObserver()
    }

    private fun subscribeObserver() {

        viewModel.authSignUpHandle.observe(this) {
            when (it.status) {
                Status.LOADING -> {
                    showProgress()
                }

                Status.SUCCESS -> {
                    hideProgress()
                    if (it.data != null) {

                    }
                }


                Status.ERROR -> {
                    hideProgress()
                    if (it.exception != null) {
                        viewModel.setError(it.exception.message)
                    }

                }
            }
        }




        viewModel.getErrorObserver().observe(this) {
            if (!it.isNullOrEmpty() || it.isNotBlank()) {
                showToast(it)
            }
        }

    }


    private fun setClicks() {
        binding.btnCreateAccount.setOnClickListener(this)
        binding.ivAddProfile.setOnClickListener(this)
    }

    private fun initViews() {
        setSpanString()
    }

    private fun setSpanString() {
        val string = getString(R.string.dont_have_account)
        if (string.contains("?")) {
            val split = string.split("?")

            val ss = SpannableString(string)
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                    ds.color = R.color.text_color.asColor()
                }
            }
            ss.setSpan(
                clickableSpan, split[0].length + 2, string.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.createAccount.text = ss
            binding.createAccount.movementMethod = LinkMovementMethod.getInstance()
        }


    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnCreateAccount -> {
                val user = UserDataModel(
                    "0",
                    email = binding.teEmail.text.toString().trim(),
                    organization = binding.teOrganization.text.toString(),
                    name = binding.teFullName.text.toString(),
                    user_image = imageUrl
                )
                viewModel.createUserWithCredential(binding.tePassword.text.toString().trim(), user)
            }

            binding.ivAddProfile -> {
                val dialog = ImagePickerBottomSheetDialog()
                dialog.setOnViewClickedListener(object :
                    ImagePickerBottomSheetDialog.OnViewClickedListener {
                    override fun onImageSelected(image: String) {
                        imageUrl = image
                        Glide.with(this@RegisterActivity).load(image).into(binding.ivProfile)
                    }

                    override fun onCameraSelected() {

                    }

                })

                dialog.show(supportFragmentManager,"image picker")
            }
        }


    }
}