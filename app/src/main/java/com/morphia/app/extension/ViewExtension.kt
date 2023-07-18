package com.morphia.app.extension

import android.app.Activity
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.morphia.app.app.MorphiaApp
import com.morphia.app.R
import com.permissionx.guolindev.PermissionX
import java.util.ArrayList

object ViewExtension {

    fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this.requireContext(), message, duration).show()
    }

    fun Fragment.showToast(@StringRes message: Int, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this.requireContext(), message, duration).show()
    }

    fun Activity.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }

    fun Activity.showToast(@StringRes message: Int, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }

    fun Fragment.requestPermission(
        list: ArrayList<String>, permissionsCallback: PermissionCallback? = null
    ) {
        PermissionX.init(this).permissions(list).setDialogTintColor(
            R.color.bg_dark_blue.asColor(), R.color.bg_dark_blue.asColor()
        ).onExplainRequestReason { scope, deniedList, beforeRequest ->
            val message = getString(R.string.permission_needs)
            scope.showRequestReasonDialog(
                deniedList, message, getString(R.string.allow), getString(R.string.deny)
            )
        }.onForwardToSettings { scope, deniedList ->
            val message = getString(R.string.allow_permission_settings)
            scope.showForwardToSettingsDialog(
                deniedList, message, getString(R.string.allow), getString(R.string.deny)
            )
        }.request { allGranted, _, deniedList ->
            if (allGranted) {
                permissionsCallback?.onAllGranted()
            } else {
                permissionsCallback?.onDeny(deniedList)

            }
        }
    }

    interface PermissionCallback {
        fun onAllGranted()

        fun onDeny(deniedList: List<String>)
    }

    fun Int.asColor() = ContextCompat.getColor(MorphiaApp.instance, this)


}