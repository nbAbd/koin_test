package com.pieaksoft.event.consumer.android.model

import android.graphics.Point

class MyGantItem {
    var isError = false
    var title: String = ""
    var point: Point = Point()
    var pointList:MutableList<GantItemPoint> = mutableListOf()
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

    constructor(isError: Boolean, title: String, pointList:MutableList<GantItemPoint>) {
        this.isError = isError
        this.title = title
        this.pointList = pointList
    }


//    fun getSortedPointList(): MutableList<Point> {
//        //            .sortWith(compareBy { it.x })
////        pointList.sortBy({it.x})
////        sortedList
//        return pointList.toList().sortedWith(compareBy { it.x }).toMutableList()
//    }

    override fun toString(): String {
        return "is Error = "+ isError +" title = "+ title+" pointList = "+pointList
    }
}