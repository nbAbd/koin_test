package com.pieaksoft.event.consumer.android.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.facebook.drawee.view.SimpleDraweeView
import com.pieaksoft.event.consumer.android.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


fun FragmentActivity.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

inline fun <reified T> SharedPreferences.get(key: String, defaultValue: T): T {
    when (T::class) {
        Boolean::class -> return this.getBoolean(key, defaultValue as Boolean) as T
        Float::class -> return this.getFloat(key, defaultValue as Float) as T
        Int::class -> return this.getInt(key, defaultValue as Int) as T
        Long::class -> return this.getLong(key, defaultValue as Long) as T
        String::class -> return this.getString(key, defaultValue as String) as T
        else -> {
            if (defaultValue is Set<*>) {
                return this.getStringSet(key, defaultValue as Set<String>) as T
            }
        }
    }

    return defaultValue
}

inline fun <reified T> SharedPreferences.put(key: String, value: T) {
    val editor = this.edit()

    when (T::class) {
        Boolean::class -> editor.putBoolean(key, value as Boolean)
        Float::class -> editor.putFloat(key, value as Float)
        Int::class -> editor.putInt(key, value as Int)
        Long::class -> editor.putLong(key, value as Long)
        String::class -> editor.putString(key, value as String)
        else -> {
            if (value is Set<*>) {
                editor.putStringSet(key, value as Set<String>)
            }
        }
    }

    editor.apply()
}

fun Activity.hideKeyboard() {
    val imm: InputMethodManager =
        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var view: View? = currentFocus
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.hideKeyboard() {
    val imm: InputMethodManager =
        requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view: View? = requireActivity().currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(requireActivity())
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

//fun ImageView.setImage(url: String?) {
//    Picasso.get()
//        .load(url?.let { Uri.parse(it) })
//        .placeholder(R.drawable.placeholder)
//        .into(this)
//}

fun SimpleDraweeView.setImageWithPlaceHolder(url: String?, placeHolder: String) {
    // hierarchy.setPlaceholderImage(context?.let { TextDrawableUtil.getTextDrawable(it, placeHolder) })
    setImageURI(url)
}

//fun ImageView.setImage(listContent: List<Image>?) {
//    when {
//        listContent.isNullOrEmpty() -> {
//            Picasso.get()
//                .load(R.drawable.placeholder)
//                .placeholder(R.drawable.placeholder)
//                .into(this)
//        }
//        else -> setImage(listContent.first().thumbnailUrl)
//    }
//}

//fun bitmapDescriptorFromVector(
//    context: Context,
//    @DrawableRes vectorDrawableResourceId: Int
//): BitmapDescriptor? {
//    val background = ContextCompat.getDrawable(context, vectorDrawableResourceId)
//    background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
//    val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
//
//    val bitmap = Bitmap.createBitmap(
//        background.intrinsicWidth,
//        background.intrinsicHeight,
//        Bitmap.Config.ARGB_8888
//    )
//    val canvas = Canvas(bitmap)
//    background.draw(canvas)
//    vectorDrawable?.draw(canvas)
//    return BitmapDescriptorFactory.fromBitmap(bitmap)
//
//}


@SuppressLint("SimpleDateFormat")
val formatter = SimpleDateFormat("HH:mm")

@SuppressLint("SimpleDateFormat")
val formatterDateAndTime = SimpleDateFormat("EEE, MMM d/yyyy")


//fun ImageView.circleImage(url: String?) {
//    Glide.with(context)
//        .load(url ?: "")
//        .transform(CenterCrop(), RoundedCorners(50))
//        .placeholder(R.drawable.placeholder)
//        .into(this)
//}

//fun ImageView.withCorner(url: String?, radius: Int) {
//    Glide.with(context)
//        .load(url ?: "")
//        .transform(CenterCrop(), RoundedCorners(radius))
//        .placeholder(R.drawable.placeholder)
//        .into(this)
//}

/**
 * View extensions
 */
fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.visible(visible: Boolean, withGone: Boolean = true) {
    visibility = when {
        visible -> View.VISIBLE
        withGone -> View.GONE
        else -> View.INVISIBLE
    }
}

fun Activity.isUIAvailable(): Boolean {
    return !isFinishing && !isDestroyed
}


fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}

fun Context.isNetworkAvailable(): Boolean {
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val capabilities = manager?.activeNetwork ?: return false
        val active = manager.getNetworkCapabilities(capabilities) ?: return false
        return when {
            active.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            active.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            active.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        return when (manager?.activeNetworkInfo?.type) {
            ConnectivityManager.TYPE_WIFI,
            ConnectivityManager.TYPE_MOBILE,
            ConnectivityManager.TYPE_ETHERNET -> manager.activeNetworkInfo?.isConnected == true
            else -> false
        }
    }
}

fun Context.enableNotifications() {
    Intent().apply {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                action = "android.settings.APP_NOTIFICATION_SETTINGS"
                putExtra("app_package", packageName)
                putExtra("app_uid", applicationInfo?.uid)
            }
            else -> {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                addCategory(Intent.CATEGORY_DEFAULT)
                data = Uri.parse("package:$packageName")
            }
        }
    }.also { startActivity(it) }
}

fun ImageView.switchSelectStopIcon(select: Boolean) {
    if (select) {
        setImageResource(R.drawable.ic_radio_on)
    } else {
        setImageResource(R.drawable.ic_radio_off)
    }
}

fun View.showWithAnimation(duration: Int = 300) {
    if (isShown) return

    isVisible = true
    animate()
        .translationY(0f)
        .setDuration(duration.toLong())
        .setListener(null)
        .start()
}

fun View.hideWithAnimation(to: Int, duration: Int = 300) {
    if (isGone) return
    val translateTo = when (to) {
        Gravity.TOP -> -(height.toFloat() + 20)
        else -> height.toFloat() + 40
    }

    isGone = true

    animate()
        .translationY(translateTo)
        .setDuration(duration.toLong())
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                isGone = true
            }
        })
        .start()
}