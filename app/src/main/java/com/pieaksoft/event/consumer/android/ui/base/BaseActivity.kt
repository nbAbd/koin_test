package com.pieaksoft.event.consumer.android.ui.base

import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.db.AppDataBase
import com.pieaksoft.event.consumer.android.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.inject
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity(@LayoutRes private val idRes: Int) : AppCompatActivity(),
    ConnectionStateMonitor.OnNetworkAvailableCallbacks, CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    val sp by lazy {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    val db: AppDataBase by inject()

    internal val pd by lazy {
        ProgressDialog(this).apply {
            setMessage(context.getString(R.string.loading))
            setCancelable(false)
        }
    }

    var connectionStateMonitor: ConnectionStateMonitor? = null

    abstract fun setView()
    abstract fun bindVM()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(idRes)
        setView()
        bindVM()
        val nMgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nMgr.cancelAll()
    }

    override fun onPositive() {
    }

    override fun onNegative() {
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

    fun showDialogWithOkNoButton(
        title: String, message: String,
        @StringRes positiveStringRes: Int = R.string.yes,
        @StringRes negativeStringRes: Int = R.string.no,
        listenerOk: View.OnClickListener?, listenerNo: View.OnClickListener?
    ) {
        launch {
            if (!isUIAvailable()) {
                return@launch
            }

            val alert = AlertDialog.Builder(this@BaseActivity).create()
            alert.setTitle(title)
            alert.setMessage(message)
            alert.setCancelable(false)
            alert.setButton(
                AlertDialog.BUTTON_POSITIVE,
                this@BaseActivity.getText(positiveStringRes)
            ) { _, _ ->
                listenerOk?.onClick(null)
                alert.dismiss()
            }
            alert.setButton(
                AlertDialog.BUTTON_NEGATIVE,
                this@BaseActivity.getText(negativeStringRes)
            ) { _, _ ->
                listenerNo?.onClick(null)
                alert.dismiss()
            }
            alert.show()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }
}