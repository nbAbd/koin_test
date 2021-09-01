package com.pieaksoft.event.consumer.android

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.DialogCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.araujo.jordan.excuseme.ExcuseMe
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pieaksoft.event.consumer.android.databinding.ActivityLoginBinding
import com.pieaksoft.event.consumer.android.databinding.ActivityMainBinding
import com.pieaksoft.event.consumer.android.ui.base.BaseActivity
import com.pieaksoft.event.consumer.android.utils.*

class MainActivity : BaseActivity(R.layout.activity_main) {
    private lateinit var navController: NavController
    private var permissionDialog: Dialog? = null
   // private val binding by viewBinding(ActivityMainBinding::bind)
    override fun setView() {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setupWithNavController(navController)


        val bottomMenuView = bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        val view = bottomMenuView.getChildAt(2)
        val itemView = view as BottomNavigationItemView

        val viewCustom =
            LayoutInflater.from(this).inflate(R.layout.custom_on_off_button, bottomMenuView, false)
        itemView.addView(viewCustom)

        val off = viewCustom.findViewById<AppCompatButton>(R.id.off_btn)
        val on = viewCustom.findViewById<AppCompatButton>(R.id.on_btn)
        off.setOnClickListener {
            Toast.makeText(this, "This is test", Toast.LENGTH_SHORT).show()
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {

                }
                R.id.coDriverFragment -> {

                }

                else -> {

                }

            }
        }


       findViewById<AppCompatImageView>(R.id.menu).setOnClickListener{
                if (findViewById<ConstraintLayout>(R.id.menu_opened).visibility == View.GONE) {
                    findViewById<ConstraintLayout>(R.id.menu_opened).show()
                    findViewById<AppCompatImageView>(R.id.menu).setImageResource(R.drawable.ic_close2)
                } else {
                    findViewById<ConstraintLayout>(R.id.menu_opened).hide()
                    findViewById<AppCompatImageView>(R.id.menu).setImageResource(R.drawable.ic_menu)
                }
            }
       if(!sp.getBoolean(SHARED_PREFERENCES_NOTIFICATION_SETTINGS, true) ||
           !ExcuseMe.doWeHavePermissionFor(this, Manifest.permission.ACCESS_FINE_LOCATION)){
           showPermissionsDialog()
       }
    }

    override fun bindVM() {

    }


    private fun showPermissionsDialog() {
        permissionDialog = Dialog(this, R.style.ThemeDialog)
        permissionDialog?.setContentView(R.layout.dialog_permissions)
        val locationAllowTextView = permissionDialog?.findViewById<AppCompatTextView>(R.id.location_allow)
        val notificationAllowTextView = permissionDialog?.findViewById<AppCompatTextView>(R.id.notification_allow)
        if (sp.getBoolean(SHARED_PREFERENCES_NOTIFICATION_SETTINGS, true)){
            notificationAllowTextView?.text = getString(R.string.allowed)
            notificationAllowTextView?.setTextColor(ContextCompat.getColor(this, R.color.white))
            notificationAllowTextView?.background?.colorFilter = BlendModeColorFilterCompat
                .createBlendModeColorFilterCompat(ContextCompat.getColor(this, R.color.blue),
                    BlendModeCompat.SRC_IN)
        } else {
            notificationAllowTextView?.text = getString(R.string.allow)
            notificationAllowTextView?.setTextColor(ContextCompat.getColor(this, R.color.grey))
            notificationAllowTextView?.background?.colorFilter = BlendModeColorFilterCompat
                .createBlendModeColorFilterCompat(ContextCompat.getColor(this, R.color.white),
                    BlendModeCompat.SRC_IN)
        }

        if(ExcuseMe.doWeHavePermissionFor(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            locationAllowTextView?.text = getString(R.string.allowed)
            locationAllowTextView?.setTextColor(ContextCompat.getColor(this, R.color.white))
            locationAllowTextView?.background?.colorFilter = BlendModeColorFilterCompat
                .createBlendModeColorFilterCompat(ContextCompat.getColor(this, R.color.blue),
                    BlendModeCompat.SRC_IN)
        } else {
            locationAllowTextView?.text = getString(R.string.allow)
            locationAllowTextView?.setTextColor(ContextCompat.getColor(this, R.color.grey))
            locationAllowTextView?.background?.colorFilter = BlendModeColorFilterCompat
                .createBlendModeColorFilterCompat(ContextCompat.getColor(this, R.color.white),
                    BlendModeCompat.SRC_IN)
        }


        locationAllowTextView?.setOnClickListener {
            ExcuseMe.couldYouGive(this).permissionFor(Manifest.permission.ACCESS_FINE_LOCATION){
                locationAllowTextView.text = getString(R.string.allowed)
                locationAllowTextView.setTextColor(ContextCompat.getColor(this, R.color.white))
                locationAllowTextView.background?.colorFilter = BlendModeColorFilterCompat
                    .createBlendModeColorFilterCompat(ContextCompat.getColor(this, R.color.blue),
                        BlendModeCompat.SRC_IN)
            }
        }
        permissionDialog?.show()
    }

    companion object {
        fun newInstance(context: Context): Intent {
            return newIntent<MainActivity>(context).apply {

            }
        }
    }
}