package com.pieaksoft.event.consumer.android.base

import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity(@LayoutRes private val idRes: Int) : AppCompatActivity(),
    ConnectionStateMonitor.OnNetworkAvailableCallbacks, CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    val sp by lazy {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }


    internal val pd by lazy {
        ProgressDialog(this).apply {
            setMessage(context.getString(R.string.loading))
            setCancelable(false)
        }
    }

    var snackbar: CustomSnackbar? = null
    var connectionStateMonitor: ConnectionStateMonitor? = null
    var viewGroup: ViewGroup? = null

    abstract fun setView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(idRes)
        setView()
        val nMgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nMgr.cancelAll()
    }

    override fun onPositive() {
    }

    override fun onNegative() {
    }

    override fun onResume() {
        super.onResume()

//        sp.get("profileId", "").let {
//            chatVM.setStatus(this, it, "online")
//        }
    }

    override fun onPause() {
        super.onPause()
//        sp.get("profileId", "").let {
//            chatVM.setStatus(this, it, "offline")
//        }
    }

    fun setProgressVisible(visible: Boolean) {
        launch {
            if (!isUIAvailable()) {
                return@launch
            }
            try {
                if (visible && !pd.isShowing) {
                    pd.show()
                } else if (!visible && pd.isShowing) {
                    pd.dismiss()
                }
            } catch (e: Exception) {
            }
        }
    }

//    fun updateAppDialog(text: String) {
//        launch {
//            if (isUIAvailable()) {
//                val alert = AlertDialog.Builder(this@BaseActivity).create()
//                alert.setTitle(getString(R.string.need_update_title))
//                alert.setMessage(text)
//                alert.setCancelable(false)
//                alert.setButton(
//                    AlertDialog.BUTTON_POSITIVE,
//                    getString(R.string.need_update_confirm)
//                ) { _, _ ->
//                    try {
//                        startActivity(
//                            Intent(
//                                Intent.ACTION_VIEW,
//                                Uri.parse("market://details?id=com.peaksoft.e_commerce")
//                            )
//                        )
//                        finish()
//                    } catch (e: ActivityNotFoundException) {
//                        startActivity(
//                            Intent(
//                                Intent.ACTION_VIEW,
//                                Uri.parse("https://play.google.com/store/apps/details?id=com.peaksoft.e_commerce")
//                            )
//                        )
//                        finish()
//                    }
//                }
//                alert.show()
//            }
//        }
//    }
//
//    fun showDialogWithOkNoButton(title: String, message: String,
//                                 @StringRes positiveStringRes: Int = R.string.yes,
//                                 @StringRes negativeStringRes: Int = R.string.no,
//                                 listenerOk: View.OnClickListener?, listenerNo: View.OnClickListener?) {
//        launch {
//            if (!isUIAvailable()) {
//                return@launch
//            }
//
//            val alert = AlertDialog.Builder(this@BaseActivity).create()
//            alert.setTitle(title)
//            alert.setMessage(message)
//            alert.setCancelable(false)
//            alert.setButton(AlertDialog.BUTTON_POSITIVE, this@BaseActivity.getText(positiveStringRes)) { _, _ ->
//                listenerOk?.onClick(null)
//                alert.dismiss()
//            }
//            alert.setButton(AlertDialog.BUTTON_NEGATIVE, this@BaseActivity.getText(negativeStringRes)) { _, _ ->
//                listenerNo?.onClick(null)
//                alert.dismiss()
//            }
//            alert.show()
//        }
//    }
}