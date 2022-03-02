package com.pieaksoft.event.consumer.android.ui.dialog

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.araujo.jordan.excuseme.ExcuseMe
import com.google.android.material.button.MaterialButton
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.DialogPermissionsBinding
import com.pieaksoft.event.consumer.android.utils.LocationUtil
import com.pieaksoft.event.consumer.android.utils.LocationUtil.isFineAndBackgroundLocationPermissionGranted
import com.pieaksoft.event.consumer.android.utils.enableNotifications
import com.pieaksoft.event.consumer.android.utils.hideSystemUI
import com.pieaksoft.event.consumer.android.utils.toast

class PermissionDialog : DialogFragment() {
    companion object {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
    }

    private var _binding: DialogPermissionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var registerForEnableBluetoothResult: ActivityResultLauncher<Intent>

    private var isLocationGranted = false
        get() = requireContext().isFineAndBackgroundLocationPermissionGranted
        set(value) {
            field = value
            when (value) {
                true -> {
                    configureButton(binding.btnLocationAllow, PermissionButtonStyle.ALLOWED) {
                        toast("Permission already granted")
                    }
                    if (isBluetoothGranted && isNotificationGranted) dialog?.dismiss()
                }
                else -> {
                    configureButton(binding.btnLocationAllow, PermissionButtonStyle.DISALLOWED) {
                        LocationUtil.checkLocationPermissions(this) {
                            isLocationGranted = true
                        }
                    }
                }
            }
        }

    private var isBluetoothGranted = false
        get() = ExcuseMe.doWeHavePermissionFor(requireContext(), Manifest.permission.BLUETOOTH)
        set(value) {
            field = value
            when (value) {
                true -> {
                    configureButton(binding.btnBluetoothAllow, PermissionButtonStyle.ALLOWED) {
                        toast("Permission already granted")
                    }
                    if (isLocationGranted && isNotificationGranted) dialog?.dismiss()
                }
                else -> {
                    configureButton(binding.btnBluetoothAllow, PermissionButtonStyle.DISALLOWED) {
                        // open bluetooth
                        registerForEnableBluetoothResult.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                    }
                }
            }
        }

    private var isNotificationGranted = false
        get() = NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
        set(value) {
            field = value
            when (value) {
                true -> {
                    configureButton(binding.btnNotificationAllow, PermissionButtonStyle.ALLOWED) {
                        toast("Permission already granted")
                    }
                    if (isBluetoothGranted && isLocationGranted) dialog?.dismiss()
                }
                else -> {
                    configureButton(
                        binding.btnNotificationAllow,
                        PermissionButtonStyle.DISALLOWED
                    ) {
                        // open notification settings
                        requireContext().enableNotifications()
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideSystemUI()
        _binding = DialogPermissionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.ThemeDialog)
        registerForEnableBluetoothResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
                isBluetoothGranted = res.resultCode == Activity.RESULT_OK
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = with(binding) {
        isLocationGranted =
            requireContext().isFineAndBackgroundLocationPermissionGranted

        isBluetoothGranted =
            ExcuseMe.doWeHavePermissionFor(requireContext(), Manifest.permission.BLUETOOTH)

        isNotificationGranted =
            NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
    }

    private fun configureButton(
        button: MaterialButton,
        toStyle: PermissionButtonStyle,
        onClickAction: (() -> Unit)? = null
    ) {
        button.setOnClickListener { onClickAction?.invoke() }
        when (toStyle) {
            PermissionButtonStyle.ALLOWED -> {
                button.apply {
                    text = getString(R.string.allowed)
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    setBackgroundColor(ContextCompat.getColor(context, R.color.flying_fish_blue))
                }
            }
            PermissionButtonStyle.DISALLOWED -> {
                button.apply {
                    text = getString(R.string.allow)
                    setTextColor(ContextCompat.getColor(context, R.color.mountain_fig))
                    setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    enum class PermissionButtonStyle {
        ALLOWED,
        DISALLOWED
    }
}