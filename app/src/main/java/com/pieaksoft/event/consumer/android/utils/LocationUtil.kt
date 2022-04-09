package com.pieaksoft.event.consumer.android.utils

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.araujo.jordan.excuseme.ExcuseMe
import com.google.android.gms.location.LocationServices

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

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        Log.e("locationMnager",locationManager.toString())
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun getCurrentLocationOnce(
        activity: Activity,
        context: Context,
        onLocationAvailable: (location: Location) -> Unit
    ) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("isLocation enabled",isLocationEnabled(context).toString())
            if (isLocationEnabled(context)) {
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    Log.e("location",it.toString())
                    onLocationAvailable(it)
                }
            }
        }
    }
}