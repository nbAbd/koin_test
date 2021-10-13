package com.pieaksoft.event.consumer.android.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.*
import android.net.Uri
import android.os.Handler
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

fun String.getActualText(): String {
    return when (this) {
        "popularity" -> "По популярности"
        "date_add" -> "По дате добавления"
        "new" -> "По новизне"
        "price_asc" -> "Цена: по возрастанию"
        "price_desc" -> "Цена: по убыванию"
        else -> "По дате добавления"
    }
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

fun Button.isVisibleButton(data: Any?) {
    if (data != null) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

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

fun ViewGroup.createView(@LayoutRes resId: Int) =
    LayoutInflater.from(context).inflate(resId, this, false)!!


@SuppressLint("SimpleDateFormat")
val formatter = SimpleDateFormat("HH:mm")

@SuppressLint("SimpleDateFormat")
val formatterDateAndTime = SimpleDateFormat("EEE, MMM d/yyyy")

fun toTime(long: Long): String = formatter.format(Date(long * 1000))

fun toDateFormat(long: Long): Date = Date(long)

fun toDate(long: Long): String =
    DateFormat.getDateInstance(DateFormat.LONG).format(Date(long * 1000))

@SuppressLint("SimpleDateFormat")
fun dateToLong(date: String): Long {
    val formatter = SimpleDateFormat("dd-MM-yyyy")
    val dates = formatter.parse(date) as Date
    return dates.time / 1000
}


fun toFormatDate(long: Long): String = formatterDateAndTime.format(Date(long * 1000))


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

fun ImageView.setTint(@ColorInt color: Int?) {
    if (color == null) {
        ImageViewCompat.setImageTintList(this, null)
        return
    }
    ImageViewCompat.setImageTintMode(this, PorterDuff.Mode.SRC_ATOP)
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}

/**
 * View extensions
 */
fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.invisible(invisible: Boolean, withGone: Boolean = false) {
    visibility = when {
        invisible -> View.INVISIBLE
        withGone -> View.GONE
        else -> View.VISIBLE
    }
}

fun View.visible(visible: Boolean, withGone: Boolean = true) {
    visibility = when {
        visible -> View.VISIBLE
        withGone -> View.GONE
        else -> View.INVISIBLE
    }
}

fun View.notEnableForOneSec() {
    isEnabled = false
    Handler().postDelayed({ isEnabled = true }, 1000)
}

fun View.notEnableForSec(seconds: Long) {
    isEnabled = false
    Handler().postDelayed({ isEnabled = true }, seconds)
}

fun View.notClickableForTwoSec() {
    isClickable = false
    Handler().postDelayed({ isClickable = true }, 2000)
}

fun View.notClickableForOneSec() {
    isClickable = false
    Handler().postDelayed({ isClickable = true }, 1000)
}

fun TextView.crossline() {
    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}

fun TextView.setMultipleSpanTextLink(
    string: String,
    linkString: String,
    linkString2: String,
    listener: View.OnClickListener,
    listener2: View.OnClickListener
) {
    val spannableString = SpannableString(string)
    val span = object : ClickableSpan() {
        override fun onClick(p0: View) {
            listener.onClick(p0)
        }
    }
    val span2 = object : ClickableSpan() {
        override fun onClick(p0: View) {
            listener2.onClick(p0)
        }
    }
    if (string.contains(linkString))
        spannableString.setSpan(
            span,
            string.indexOf(linkString),
            string.indexOf(linkString) + linkString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

    if (string.contains(linkString2))
        spannableString.setSpan(
            span2,
            string.indexOf(linkString2),
            string.indexOf(linkString2) + linkString2.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    text = spannableString
    movementMethod = LinkMovementMethod.getInstance()
}

//fun String.phoneNumberFormat(region: String = "KG", util: PhoneNumberUtil): String {
//    return util.format(util.parse(this, region), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
//}
//
//fun String.isPhoneNumberValid(region: String = "KG", util: PhoneNumberUtil): Boolean {
//    val phoneNumberFormatter = util.parse(this, region)
//    return util.isValidNumber(phoneNumberFormatter)
//}

fun TextView.setMultipleBoldText(
    string: String,
    boldString: String,
    boldString2: String,
    color: Int
) {
    val spannableString = SpannableString(string)
    if (string.contains(boldString)) {
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            string.indexOf(boldString),
            string.indexOf(boldString) + boldString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(color),
            string.indexOf(boldString),
            string.indexOf(boldString) + boldString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    if (string.contains(boldString2)) {
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            string.indexOf(boldString2),
            string.indexOf(boldString2) + boldString2.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            ForegroundColorSpan(color),
            string.indexOf(boldString2),
            string.indexOf(boldString2) + boldString2.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    text = spannableString
}

fun Activity.isUIAvailable(): Boolean {
    return !isFinishing && !isDestroyed
}

@ColorInt
fun String.toColorInt(default: String = "#1976D2"): Int {
    return if (this.isBlank()) {
        Color.parseColor(default)
    } else try {
        Color.parseColor(this)
    } catch (e: Exception) {
        Color.parseColor(default)
    }
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

fun TextView.setDrawableStart(drawable: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)

}

fun String.getGantColor(): String {
    return when {
        this == "Off" -> {
            "#D7112A"
        }
        this == "SB" -> {
            "#0045CF"
        }
        this == "D" -> {
            "#27AE60"
        }
        this == "On" -> {
            "#E5AF0A"
        }
        else -> {
            "#9BAEC8"
        }
    }
}

fun RecyclerView.attachSnapHelperWithListener(
    snapHelper: SnapHelper,
    behavior: SnapOnScrollListener.Behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
    onSnapPositionChangeListener: OnSnapPositionChangeListener) {
    snapHelper.attachToRecyclerView(this)
    val snapOnScrollListener = SnapOnScrollListener(snapHelper, behavior, onSnapPositionChangeListener)
    addOnScrollListener(snapOnScrollListener)
}

fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}