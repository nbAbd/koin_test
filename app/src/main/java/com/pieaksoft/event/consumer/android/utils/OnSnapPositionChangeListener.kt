package com.pieaksoft.event.consumer.android.utils

interface OnSnapPositionChangeListener {

    fun onSnapPositionChange(position: Int)
    fun onSnapPositionDragging()
    fun onSnapPositionNotChange(position: Int)
}