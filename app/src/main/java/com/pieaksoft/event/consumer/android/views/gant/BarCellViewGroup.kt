package com.pieaksoft.event.consumer.android.views.gant

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.model.gant.MyGantItem
import com.pieaksoft.event.consumer.android.utils.getGantColor
import com.pieaksoft.event.consumer.android.utils.toColorInt
import miguelbcr.ui.tableFixHeadesWrapper.TableFixHeaderAdapter

class BarCellViewGroup : FrameLayout,
    TableFixHeaderAdapter.BodyBinder<List<String>> {

    var line_body: View? = null
    var myContext: Context? = null
    var myGantItemList: List<MyGantItem>? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, myGantItemList: List<MyGantItem>) : super(context) {
        this.myContext = context
        this.myGantItemList = myGantItemList
        LayoutInflater.from(context).inflate(R.layout.bar_view_item, this, true)
        line_body = findViewById(R.id.line_body)

    }

    override fun bindBody(bodyList: List<String>?, row: Int, col: Int) {
        if (bodyList!![col] != "empty") {
            line_body?.setBackgroundResource(R.drawable.gant_line)
            line_body?.setBackgroundColor(bodyList[col].getGantColor().toColorInt())
        } else {
            line_body?.setBackgroundResource(R.drawable.dotted)
        }
    }
}