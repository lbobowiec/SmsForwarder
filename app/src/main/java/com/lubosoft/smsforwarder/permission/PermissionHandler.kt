package com.lubosoft.smsforwarder.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.lubosoft.smsforwarder.R

enum class CheckPermissionResult {
    PermissionAsk,
    PermissionPreviouslyDenied,
    PermissionDisabled,
    PermissionGranted
}

typealias PermissionCheckCompletion = (CheckPermissionResult) -> Unit

object PermissionHandler {

    private fun shouldAskPermission() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    fun shouldAskPermission(context: Context, permission: String): Boolean {
        return shouldAskPermission() && ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
    }

    private fun shouldShowRequestPermissionRationale(context: Context, permission: String, fragment: Fragment?): Boolean {
        return fragment?.shouldShowRequestPermissionRationale(permission)
                ?: ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)
    }

    private fun checkPermission(context: Context, permission: String, completion: PermissionCheckCompletion, fragment: Fragment?) {
        // If permission is not granted
        if (shouldAskPermission(context, permission)) {
            // If permission denied previously
            if (shouldShowRequestPermissionRationale(context, permission, fragment)) {
                completion(CheckPermissionResult.PermissionPreviouslyDenied)
            } else {
                // Permission denied or first time requested
                if (PermissionUtil.isFirstTimeAskingPermission(context, permission)) {
                    PermissionUtil.firstTimeAskingPermission(context, permission, false)
                    completion(CheckPermissionResult.PermissionAsk)
                } else {
                    // Handle the feature without permission or ask user to manually allow permission
                    completion(CheckPermissionResult.PermissionDisabled)
                }
            }
        } else {
            completion(CheckPermissionResult.PermissionGranted)
        }
    }

    fun checkPermission(context: Context, permission: String, completion: PermissionCheckCompletion) {
        checkPermission(context, permission, completion, fragment = null)
    }

    fun checkPermission(fragment: Fragment, permission: String, completion: PermissionCheckCompletion) {
        checkPermission(fragment.requireContext(), permission, completion, fragment)
    }

    fun requestPermissions(context: Context, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(context as Activity, permissions, requestCode)
    }

    fun requestPermissions(fragment: Fragment, permissions: Array<String>, requestCode: Int) {
        fragment.requestPermissions(permissions, requestCode)
    }

    private fun showAlert(context: Context, permissions: Array<String>, requestCode: Int, title: String, message: String, fragment: Fragment?) {
        val builder = AlertDialog.Builder(context)
        builder.apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(context.getString(R.string.ok_button)) { _, _ ->
                if (fragment != null)
                    requestPermissions(fragment, permissions, requestCode)
                else
                    requestPermissions(context, permissions, requestCode)
            }
            setNeutralButton(context.getString(R.string.cancel_button), null)
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun showAlertInFragment(fragment: Fragment, permissions: Array<String>, requestCode: Int, title: String, message: String) {
        showAlert(fragment.requireContext(), permissions, requestCode, title, message, fragment)
    }

    fun showAlert(context: Context, permissions: Array<String>, requestCode: Int, title: String, message: String) {
        showAlert(context, permissions, requestCode, title, message, fragment = null)
    }

}