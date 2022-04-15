package com.pieaksoft.event.consumer.android.utils

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.araujo.jordan.excuseme.ExcuseMe
import com.google.android.gms.common.ConnectionResult.*
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.pieaksoft.event.consumer.android.R

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

    private const val MIN_TIME_BW_UPDATES = 5000L
    private const val MIN_DISTANCE_CHANGE_FOR_UPDATES = 5F
    private lateinit var locationManager: LocationManager
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var onLocationAvailableListener: LocationListener

    private const val FASTEST_INTERVAL: Long = 2000

    fun startGettingLocation(
        activity: Activity,
        context: Context,
        onLocationUpdates: LocationListener
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (isGPSEnabled || isNetworkEnabled) {
                if (SUCCESS !=
                    GoogleApiAvailability.getInstance()
                        .isGooglePlayServicesAvailable(context)
                ) {

                    val onLocationAvailableListener = LocationListener {
                        stopGettingLocation()
                        onLocationUpdates.onLocationChanged(it)
                    }
                    if (isGPSEnabled) {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            onLocationAvailableListener
                        )
                    } else {
                        locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            onLocationAvailableListener
                        )
                    }
                } else {

                    val fusedLocationClient =
                        LocationServices.getFusedLocationProviderClient(activity)

                    mLocationRequest = LocationRequest()
                    mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    mLocationRequest.interval = MIN_TIME_BW_UPDATES
                    mLocationRequest.fastestInterval = FASTEST_INTERVAL

                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(p0: LocationResult) {
                            if (p0.locations.isNotEmpty()) {
                                onLocationUpdates.onLocationChanged(p0.lastLocation)
                                fusedLocationClient.removeLocationUpdates(this)
                            }
                        }
                    }

                    fusedLocationClient.requestLocationUpdates(
                        mLocationRequest, locationCallback,
                        Looper.getMainLooper()
                    )
                }
            } else {
                context.showMessage(context.getString(R.string.turn_on_location))
            }
        }
    }

    fun stopGettingLocation() {
        if (this::onLocationAvailableListener.isInitialized)
            locationManager.removeUpdates(onLocationAvailableListener)
    }
}