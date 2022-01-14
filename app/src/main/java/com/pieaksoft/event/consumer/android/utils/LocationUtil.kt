package com.pieaksoft.event.consumer.android.utils

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.araujo.jordan.excuseme.ExcuseMe

object LocationUtil {

    private fun Context.isPermissionGranted(permission: String): Boolean =
        ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    private val Context.isFineLocationPermissionGranted
        get() = isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)

    private val Context.isBackgroundLocationPermissionGranted
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> isPermissionGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            else -> isFineLocationPermissionGranted
        }

    val Context.isFineAndBackgroundLocationPermissionGranted
        get() = isFineLocationPermissionGranted && isBackgroundLocationPermissionGranted

    private fun Fragment.requestFineLocationPermission(action: () -> Unit) {
        if (requireContext().isFineLocationPermissionGranted) {
            action()
            return
        }

        ExcuseMe.couldYouGive(this).permissionFor(Manifest.permission.ACCESS_FINE_LOCATION) {
            when (it.granted.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                true -> {
                    action()
                    return@permissionFor
                }
                else -> requestFineLocationPermission(action)
            }
        }
    }

    @TargetApi(29)
    private fun Fragment.requestFineAndBackgroundLocation(action: () -> Unit) {
        if (requireContext().isFineAndBackgroundLocationPermissionGranted) {
            action()
            return
        }
        val permissions = arrayListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )

        ExcuseMe.couldYouGive(this).permissionFor(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) {
            when (it.granted.containsAll(permissions)) {
                true -> {
                    action()
                    return@permissionFor
                }
                else -> {
                    if (!requireContext().isFineLocationPermissionGranted) {
                        requestFineLocationPermission(action)
                    }
                    if (!requireContext().isBackgroundLocationPermissionGranted) {
                        requestFineAndBackgroundLocation(action)
                    }
                }
            }
        }
    }

    fun checkLocationPermissions(fragment: Fragment, action: () -> Unit) = with(fragment) {
        if (requireContext().isFineAndBackgroundLocationPermissionGranted) {
            action()
            return@with
        }

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                requestFineAndBackgroundLocation(action)
            }
            else -> {
                requestFineLocationPermission(action)
            }
        }
    }
}