package com.pieaksoft.event.consumer.android.ui.codriver

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.pieaksoft.event.consumer.android.databinding.FragmentCoDriverBinding
import com.pieaksoft.event.consumer.android.network.ErrorHandler
import com.pieaksoft.event.consumer.android.ui.base.BaseActivity
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment
import com.pieaksoft.event.consumer.android.ui.activities.login.LoginViewModel
import com.pieaksoft.event.consumer.android.ui.profile.ProfileViewModel
import com.pieaksoft.event.consumer.android.utils.*
import com.pieaksoft.event.consumer.android.views.Dialogs
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel


class CoDriverFragment : BaseFragment<FragmentCoDriverBinding>() {
    private val profileViewModel: ProfileViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()

    private var loginValue: String? = null
    private var passwordValue: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    override fun setupView() {
        getDriversInfo()

        with(binding) {
            driver2.setEmpty(true)
            driver2.setOnClickListener {
                driversContainer.hide()
                loginContainer.show()
            }

            loginName.addTextChangedListener {
                if (it?.length == 0) {
                    loginValue = null
                    loginBtn.isEnabled = isEnableButton()
                } else {
                    loginValue = it.toString()
                    loginBtn.isEnabled = isEnableButton()
                }
            }

            password.addTextChangedListener {
                if (it?.length == 0) {
                    passwordValue = null
                    loginBtn.isEnabled = isEnableButton()
                } else {
                    passwordValue = it.toString()
                    loginBtn.isEnabled = isEnableButton()
                }
            }


            loginBtn.setOnClickListener {
                loginViewModel.login(loginValue ?: "", passwordValue ?: "", true)
            }

            cancelBtn.setOnClickListener {
                driversContainer.show()
                loginContainer.hide()
            }
        }
    }

    private fun bindViewModel() {
        profileViewModel.driver1.observe(this, {
            launch {
                binding.driver1.setDriverInfo(it)
            }
        })
        profileViewModel.driver2.observe(this, {
            launch {
                binding.driver2.setDriverInfo(it, false)
                binding.driver2.setEmpty(false)
                binding.driver2.setOnClickListener {
                    Dialogs.showSwapDriversDialog(
                        requireActivity(),
                        object : Dialogs.SwapDriversListener {
                            override fun onSwapDriversClick() {
                                profileViewModel.swapDrivers()
                            }
                        })
                }
            }
        })

        profileViewModel.needUpdateObservable.observe(this, {
            getDriversInfo()
            LocalBroadcastManager
                .getInstance(requireContext())
                .sendBroadcast(Intent().setAction(BROADCAST_SWAP_DRIVERS))
        })

        profileViewModel.error.observe(this, { message ->
            message?.let {
                toast(ErrorHandler.getErrorMessage(it, requireContext()))
            }
        })

        loginViewModel.progress.observe(this, {
            (activity as BaseActivity).setProgressVisible(it)
        })

        loginViewModel.error.observe(this, { message ->
            message?.let {
                toast(ErrorHandler.getErrorMessage(it, requireContext()))
            }
        })

        loginViewModel.isSuccessLogin.observe(this, { success ->
            if (success) {
                launch {
                    binding.loginContainer.hide()
                    binding.driversContainer.show()
                    getDriversInfo()
                }
            }
        })
    }

    private fun getDriversInfo() {
        profileViewModel.getProfile()
        if (isCooDriverExist()) {
            profileViewModel.getProfile(true)
        }
    }

    private fun isEnableButton(): Boolean {
        return loginValue != null && passwordValue != null
    }

    private fun isCooDriverExist(): Boolean {
        return sharedPrefs.getString(SHARED_PREFERENCES_ADDITIONAL_USER_ID, "") != ""
    }
}