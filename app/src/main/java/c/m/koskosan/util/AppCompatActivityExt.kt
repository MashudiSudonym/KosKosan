package c.m.koskosan.util

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity

/**
 * Utility file for help about check and request permission
 */

fun AppCompatActivity.checkSelfPermissionCompat(permission: String) =
    ActivityCompat.checkSelfPermission(this, permission)

fun AppCompatActivity.shouldShowRequestPermissionRationaleCompat(permission: String) =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun AppCompatActivity.requestPermissionsCompat(permissionArray: Array<String>, requestCode: Int) =
    ActivityCompat.requestPermissions(this, permissionArray, requestCode)

// request permission for get location
fun AppCompatActivity.requestPermission() {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        Constants.PERMISSION_REQUEST_LOCATION
    )
}

// request permission for get location (fragment)
fun FragmentActivity.requestPermission() {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        Constants.PERMISSION_REQUEST_LOCATION
    )
}