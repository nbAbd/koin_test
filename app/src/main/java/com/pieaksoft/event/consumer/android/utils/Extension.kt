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
import chat.ChatOuterClass
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.peaksoft.e_commerce.R
import com.peaksoft.e_commerce.model.*
import com.squareup.picasso.Picasso
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
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

fun ImageView.setImage(url: String?) {
    Picasso.get()
        .load(url?.let { Uri.parse(it) })
        .placeholder(R.drawable.placeholder)
        .into(this)
}

fun SimpleDraweeView.setImageWithPlaceHolder(url: String?, placeHolder: String) {
    hierarchy.setPlaceholderImage(context?.let { TextDrawableUtil.getTextDrawable(it, placeHolder) })
    setImageURI(url)
}

fun ImageView.setImage(listContent: List<Image>?) {
    when {
        listContent.isNullOrEmpty() -> {
            Picasso.get()
                .load(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(this)
        }
        else -> setImage(listContent.first().thumbnailUrl)
    }
}

fun Button.isVisibleButton(data: Any?) {
    if (data != null) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

fun bitmapDescriptorFromVector(
    context: Context,
    @DrawableRes vectorDrawableResourceId: Int
): BitmapDescriptor? {
    val background = ContextCompat.getDrawable(context, vectorDrawableResourceId)
    background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
    val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)

    val bitmap = Bitmap.createBitmap(
        background.intrinsicWidth,
        background.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    background.draw(canvas)
    vectorDrawable?.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)

}

fun ViewGroup.createView(@LayoutRes resId: Int) =
    LayoutInflater.from(context).inflate(resId, this, false)!!

fun userItem2(it: ChatOuterClass.Correspondent): User {
    return User(
        it.id,
        it.contentUrl,
        ChatMessageModel(
            it.lastMessage.id,
            it.lastMessage.fromUid,
            it.lastMessage.toUid,
            it.lastMessage.mediaId,
            it.lastMessage.textMessage,
            it.lastMessage.chatListId,
            it.lastMessage.isRead,
            it.lastMessage.createdTime,
            it.profile.id,
            it.profile.userId,
            it.profile.userName,
            it.profile.profileImage
        ),
        it.lastActivityDate,
        it.profile.id,
        it.profile.userId,
        it.profile.userName,
        it.profile.profileImage,
        it.unreadMessageCount
    )
}

fun chatMessages(item: ChatOuterClass.ChatDetailResponse): MutableList<ChatMessageModel> {
    val list = mutableListOf<ChatMessageModel>()
    item.messageList.forEach {
        list.add(
            ChatMessageModel(
                it.id,
                it.fromUid,
                it.toUid,
                it.mediaId,
                it.textMessage,
                it.chatListId,
                it.isRead,
                it.createdTime,
                item.profile.id,
                item.profile.userId,
                item.profile.userName,
                item.profile.profileImage
            )
        )
    }
    return list
}

fun chatMessage(item: ChatOuterClass.UserResponse): ChatMessageModel {
    return ChatMessageModel(
        item.lastMessage.id,
        item.lastMessage.fromUid,
        item.lastMessage.toUid,
        item.lastMessage.mediaId,
        item.lastMessage.textMessage,
        item.lastMessage.chatListId,
        item.lastMessage.isRead,
        item.lastMessage.createdTime,
        item.correspondent.profile.id,
        item.correspondent.profile.userId,
        item.correspondent.profile.userName,
        item.correspondent.profile.profileImage
    )
}

fun chatMessageIsRead(item: ChatMessageModel): ChatMessageModel {
    return ChatMessageModel(
        item.id,
        item.fromUid,
        item.toUid,
        item.mediaId,
        item.textMessage,
        item.chatListId,
        true,
        item.createdTime,
        item.profileId,
        item.userId,
        item.userName,
        item.profileImage
    )
}


fun userItem(item: ChatOuterClass.UserResponse): User {
    return User(
        item.correspondent.id,
        item.correspondent.contentUrl,
        ChatMessageModel(
            item.correspondent.lastMessage.id,
            item.correspondent.lastMessage.fromUid,
            item.correspondent.lastMessage.toUid,
            item.correspondent.lastMessage.mediaId,
            item.correspondent.lastMessage.textMessage,
            item.correspondent.lastMessage.chatListId,
            item.correspondent.lastMessage.isRead,
            item.correspondent.lastMessage.createdTime,
            item.correspondent.profile.id,
            item.correspondent.profile.userId,
            item.correspondent.profile.userName,
            item.correspondent.profile.profileImage
        ),
        item.correspondent.lastActivityDate,
        item.correspondent.profile.id,
        item.correspondent.profile.userId,
        item.correspondent.profile.userName,
        item.correspondent.profile.profileImage,
        item.correspondent.unreadMessageCount
    )
}

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

fun TextView.priceText(price: Int?) {
    text = when (price) {
        0 -> "一 c"
        else -> String.format("%d c", price)
    }
}

fun ImageView.isFavorite(favorite: Boolean) {
    when (favorite) {
        true -> setImageResource(R.drawable.ic_favorites)
        false -> setImageResource(R.drawable.ic_baseline_favorite)
    }
}

fun ImageView.isFavoriteMenuIcon(favorite: Boolean, context: Context) {
    when (favorite) {
        true -> setTint(ContextCompat.getColor(context, R.color.red_color))
        false -> setTint(ContextCompat.getColor(context, R.color.white))
    }
}

fun ImageView.isBlurFavorite(favorite: Boolean) {
    backgroundTintList = when (favorite) {
        true -> {
            ContextCompat.getColorStateList(
                getApplicationContext(),
                R.color.favorite_selected_color
            )
        }
        false -> {
            ContextCompat.getColorStateList(getApplicationContext(), R.color.black_transparent)
        }
    }
}

fun ImageView.isWhiteFavorite(favorite: Boolean) {
    backgroundTintList = when (favorite) {
        true -> {
            setImageResource(R.drawable.ic_baseline_favorite2)
            ContextCompat.getColorStateList(
                getApplicationContext(),
                R.color.favorite_selected_color
            )
        }
        false -> {
            setImageResource(R.drawable.ic_baseline_favorite_2_grey)
            ContextCompat.getColorStateList(getApplicationContext(), R.color.white)
        }
    }
}

fun ImageView.isVideo(listContent: List<Image>?) {
    isVisible = when {
        listContent.isNullOrEmpty() -> false
        listContent.first().isVideo -> true
        else -> false
    }
}

fun ImageView.isVideo(item: Boolean) {
    isVisible = item
}

fun ImageView.circleImage(url: String?) {
    Glide.with(context)
        .load(url ?: "")
        .transform(CenterCrop(), RoundedCorners(50))
        .placeholder(R.drawable.placeholder)
        .into(this)
}

fun ImageView.withCorner(url: String?, radius: Int) {
    Glide.with(context)
        .load(url ?: "")
        .transform(CenterCrop(), RoundedCorners(radius))
        .placeholder(R.drawable.placeholder)
        .into(this)
}

fun ImageView.setTint(@ColorInt color: Int?) {
    if (color == null) {
        ImageViewCompat.setImageTintList(this, null)
        return
    }
    ImageViewCompat.setImageTintMode(this, PorterDuff.Mode.SRC_ATOP)
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(color))
}
fun TextView.categoryName(shopCategory: Int?) {
    when {
        listCategory.isEmpty() -> text = "----"
        listCategory.isNotEmpty() -> text =
            listCategory.find { it.id == shopCategory }?.categoryName
    }
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

fun String.phoneNumberFormat(region: String = "KG", util: PhoneNumberUtil): String {
    return util.format(util.parse(this, region), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
}

fun String.isPhoneNumberValid(region: String = "KG", util: PhoneNumberUtil): Boolean {
    val phoneNumberFormatter = util.parse(this, region)
    return util.isValidNumber(phoneNumberFormatter)
}

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

fun OrderResponseModel.getOrderStatus(): OrderStatus {
    return when (status) {
        0 -> {
            OrderStatus.Pending
        }
        1 -> {
            OrderStatus.Unassigned
        }
        2 -> {
            OrderStatus.Assigned
        }
        3 -> {
            OrderStatus.Started
        }
        4 -> {
            OrderStatus.Completed
        }
        5 -> {
            OrderStatus.New
        }
        6 -> {
            OrderStatus.Canceled
        }
        else -> {
            OrderStatus.Pending
        }
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

fun TextView.setDrawableStart(drawable: Int){
    this.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)

}

fun TextView.isVerified(isVerified: Boolean = true, context: Context){
    this.setDrawableStart(if(isVerified) R.drawable.ic_verified else 0)
    this.compoundDrawablePadding = context.dp2px(5)
}