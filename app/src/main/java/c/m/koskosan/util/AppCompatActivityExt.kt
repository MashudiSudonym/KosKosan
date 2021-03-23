package c.m.koskosan.util

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

/**
 * Utility file for help about check and request permission
 */

fun AppCompatActivity.checkSelfPermissionCompat(permission: String) =
    ActivityCompat.checkSelfPermission(this, permission)

fun AppCompatActivity.shouldShowRequestPermissionRationaleCompat(permission: String) =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun AppCompatActivity.requestPermissionsCompat(permissionArray: Array<String>, requestCode: Int) =
    ActivityCompat.requestPermissions(this, permissionArray, requestCode)