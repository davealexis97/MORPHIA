package com.morphia.app.base

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

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
        dialog = LoadingDialog(requireContext())
        dialog?.show()
    }

    fun hideProgress() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }

}