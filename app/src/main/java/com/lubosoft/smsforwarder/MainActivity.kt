package com.lubosoft.smsforwarder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.view.GravityCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.lubosoft.smsforwarder.databinding.ActivityMainBinding
import com.lubosoft.smsforwarder.extensions.longToast
import com.lubosoft.smsforwarder.permission.CheckPermissionResult
import com.lubosoft.smsforwarder.permission.PermissionHandler
import com.lubosoft.smsforwarder.utilities.KeyboardUtils

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val DAY_NIGHT_MODE_PREFERENCE_KEY = "light_dark_mode_settings"
        const val PERMISSION_REQUEST_SMS = 101
    }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme()
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        drawerLayout = binding.drawerLayout

        navController = Navigation.findNavController(this, R.id.sms_forwarder_nav_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        // Set up ActionBar
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up navigation menu
        binding.navigationView.setupWithNavController(navController)

        checkPermissions()
    }

    override fun onSupportNavigateUp(): Boolean {
        KeyboardUtils.hideKeyboard(this)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setTheme() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val dayNightMode = sharedPreferences.getBoolean(DAY_NIGHT_MODE_PREFERENCE_KEY, false)
        AppCompatDelegate.setDefaultNightMode(if (dayNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun checkPermissions() {
        PermissionHandler.checkPermission(this, Manifest.permission.RECEIVE_SMS) {
            when (it) {
                CheckPermissionResult.PermissionGranted -> {
                    // FIXME
                    // toast(getString(R.string.sms_permission_granted))
                }
                CheckPermissionResult.PermissionDisabled -> {
                    longToast(getString(R.string.sms_permission_disabled))
                }
                CheckPermissionResult.PermissionAsk -> {
                    PermissionHandler.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS),
                        PERMISSION_REQUEST_SMS)

                }
                CheckPermissionResult.PermissionPreviouslyDenied -> {
                    PermissionHandler.showAlert(
                        this@MainActivity,
                        arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS),
                        PERMISSION_REQUEST_SMS,
                        getString(R.string.permission_required_dialog_title),
                        getString(R.string.sms_permission_explanation_dialog_message))
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_SMS -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission to receive SMS has been denied by user")
                } else {
                    Log.d(TAG, "Permission to receive SMS has been granted by user")
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
}
