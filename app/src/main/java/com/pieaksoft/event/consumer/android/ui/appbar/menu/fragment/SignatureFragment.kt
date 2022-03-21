package com.pieaksoft.event.consumer.android.ui.appbar.menu.fragment

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.navigation.NavController
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentSignatureBinding
import com.pieaksoft.event.consumer.android.databinding.ProgressBarBinding
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

    private lateinit var proDialog: ProgressDialog

    private lateinit var navController: NavController

    private val menuViewModel: MenuViewModel by sharedViewModel()

    override fun setupView() {
        setupPD()
        setupNavController()
        menuViewModel.apply {
            downloadSignature(
                sharedPrefs.get(SHARED_PREFERENCES_CURRENT_USER_ID, "")
            )

            proDialog.show()

            isUploaded.observe(this@SignatureFragment) {
                if (it) {
                    navController.navigateUp()
                    proDialog.hide()
                }
            }

            signature.observe(this@SignatureFragment) {
                if (it != null) {
                    binding.signaturePad.signatureBitmap = it
                    disableViews()
                }
                proDialog.hide()
            }
        }

        binding.apply {
            btnSave.setOnClickListener {
                if (!signaturePad.isEmpty) {
                    menuViewModel.uploadSignature(signaturePad.signatureBitmap)
                    proDialog.show()
                }
            }
            btnClear.setOnClickListener {
                binding.signaturePad.isEnabled = true
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

    private fun setupPD() {
        proDialog = ProgressDialog.show(context, null, null, false, false)
        proDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        proDialog.setContentView(ProgressBarBinding.inflate(layoutInflater).root)
    }

    private fun disableViews() {
        binding.signaturePad.isEnabled = false
        binding.btnClear.text = resources.getString(R.string.change_signature)
    }
}