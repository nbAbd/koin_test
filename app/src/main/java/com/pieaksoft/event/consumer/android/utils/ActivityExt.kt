package com.pieaksoft.event.consumer.android.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.util.DisplayMetrics
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.*

fun Activity.displayMetrics(): DisplayMetrics {
    val displayMetrics = DisplayMetrics();
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics
}

@Suppress("ObsoleteSdkInt")
inline fun <reified T : Any> Activity.launchActivity(
        requestCode: Int = -1,
        options: Bundle? = null,
        noinline init: Intent.() -> Unit = {}) {

    val intent = newIntent<T>(this)
    intent.init()

    Handler(Looper.getMainLooper()).post {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivityForResult(intent, requestCode, options)
        } else {
            startActivityForResult(intent, requestCode)
        }
    }
    //TODO: delete and use intents with newInstance in each activity
}

inline fun <reified T : Any> newIntent(context: Context): Intent = Intent(context, T::class.java)






//fun Context.shareApp() {
//    val sendIntent: Intent = Intent().apply {
//        action = Intent.ACTION_SEND
//        putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_title))
//        type = "text/plain"
//    }
//    startActivity(Intent.createChooser(sendIntent, "Send to"))
//}



fun Context.shareMessage(message: String) {
    startActivity(Intent.createChooser(getShareIntent(message), "Send to"))
}

fun getShareIntent(textMessage: String) : Intent{
    return Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, textMessage)
        type = "text/plain"
    }
}

fun Intent.startActivity(context: Context) {
    context.startActivity(this)
}

//fun Activity.requestLocationPermissions() {
//    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION),
//            Const.PERMISSIONS_REQUEST)
//}

//fun Activity.hasReadContactsPermission() =
//        EasyPermissions.hasPermissions(
//                this,
//                Manifest.permission.READ_CONTACTS)
//
//
//fun Activity.requestReadContactsPermission(code: Int) {
//    EasyPermissions.requestPermissions(
//            this,
//            "",
//            code,
//            Manifest.permission.READ_CONTACTS)
//}

fun Activity.getContacts(code: Int) {
    startActivityForResult(Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI), code)
}