package com.pieaksoft.event.consumer.android.ui.appbar.menu.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.NavController
import com.pieaksoft.event.consumer.android.databinding.FragmentSignatureBinding
import com.pieaksoft.event.consumer.android.ui.activities.main.MainActivity
import com.pieaksoft.event.consumer.android.ui.appbar.menu.MenuViewModel
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment
import com.pieaksoft.event.consumer.android.utils.SHARED_PREFERENCES_CURRENT_USER_ID
import com.pieaksoft.event.consumer.android.utils.get
import com.pieaksoft.event.consumer.android.utils.visible
import org.koin.android.viewmodel.ext.android.sharedViewModel

class SignatureFragment : BaseFragment<FragmentSignatureBinding>() {
    init {
        requiresBottomNavigation = false
    }

    private lateinit var navController: NavController

    private val menuViewModel: MenuViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavController()
    }

    override fun setupView() {

        menuViewModel.downloadSignature(
            sharedPrefs.get(SHARED_PREFERENCES_CURRENT_USER_ID, "")
        )

        menuViewModel.isUploaded.observe(this) {
            if (it) navController.navigateUp()
        }

        menuViewModel.signature.observe(this) {
            if (it != null) {
                binding.signaturePad.signatureBitmap = it
                binding.signaturePad.isEnabled = false
            } else {
                binding.btnClear.visible(true)
                binding.btnSave.visible(true)
            }
        }

        binding.apply {
            btnSave.setOnClickListener {
                if (!signaturePad.isEmpty) {
                    menuViewModel.uploadSignature(signaturePad.signatureBitmap)
                }
            }
            btnClear.setOnClickListener {
                signaturePad.clear()
            }
            btnExit.setOnClickListener {
                navController.navigateUp()
            }
        }
    }

    private fun setupNavController() = with(binding) {
        val navCont = (activity as MainActivity).navController
        navController = navCont
    }
}