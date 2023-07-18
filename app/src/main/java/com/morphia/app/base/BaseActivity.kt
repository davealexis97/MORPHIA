package com.morphia.app.base

import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    private var dialog: LoadingDialog? = null


    fun showProgress() {
        if (dialog == null) {
            initDialog()
        } else {
            if (dialog?.isShowing == true) {
                dialog?.dismiss()
            } else {
                dialog?.show()
            }
        }
    }

    private fun initDialog() {
        dialog = LoadingDialog(this)
        dialog?.show()
    }

    fun hideProgress() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }


}