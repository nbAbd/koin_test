package com.pieaksoft.event.consumer.android

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.DialogCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pieaksoft.event.consumer.android.ui.base.BaseActivity
import com.pieaksoft.event.consumer.android.utils.newIntent

class MainActivity : BaseActivity(R.layout.activity_main) {

    private lateinit var navController: NavController
    private var permissionDialog: Dialog? = null

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
    }

    override fun bindVM() {
        showPermissionsDialog()

    }


    fun showPermissionsDialog() {
        permissionDialog = Dialog(this, R.style.ThemeDialog)
        permissionDialog?.setContentView(R.layout.dialog_permissions)
        permissionDialog?.show()
    }

    companion object {
        fun newInstance(context: Context): Intent {
            return newIntent<MainActivity>(context).apply {

            }
        }
    }
}