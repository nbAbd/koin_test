package com.pieaksoft.event.consumer.android.model

import android.graphics.Point

class MyGantItem {
    var isError = false
    var title: String = ""
    var point: Point = Point()
    var isEmpty = false


    constructor(title: String, isEmpty: Boolean) {
        this.title = title
        this.isEmpty = isEmpty
    }


    constructor(isError: Boolean, title: String, point: Point) {
        this.isError = isError
        this.title = title
        this.point = point
    }

}