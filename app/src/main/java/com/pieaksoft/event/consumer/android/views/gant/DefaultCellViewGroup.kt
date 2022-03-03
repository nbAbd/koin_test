package com.pieaksoft.event.consumer.android.views.gant

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.model.gant.MyGantItem
import miguelbcr.ui.tableFixHeadesWrapper.TableFixHeaderAdapter

class DefaultCellViewGroup : FrameLayout,
    TableFixHeaderAdapter.FirstHeaderBinder<String>,
    TableFixHeaderAdapter.HeaderBinder<String>,
    TableFixHeaderAdapter.FirstBodyBinder<List<String>>,
    TableFixHeaderAdapter.BodyBinder<List<String>>,
    TableFixHeaderAdapter.SectionBinder<List<String>> {

    var text_view: TextView
    private var rowList: List<MyGantItem>? = null



    constructor(context: Context, rowList: List<MyGantItem>) : super(context) {
        LayoutInflater.from(context).inflate(R.layout.text_view, this, true)
        text_view = findViewById<TextView>(R.id.text_view)
        this.rowList = rowList
    }


    override fun bindFirstHeader(headerName: String?) {
        text_view.text = headerName
    }

    override fun bindHeader(headerName: String?, col: Int) {
        if(headerName == "0" || headerName == "24"){
            text_view.text = "M"
        } else {
            text_view.text = headerName
        }
    }

    override fun bindFirstBody(bodyList: List<String>?, row: Int) {
//        text_view.text = rowList!![row].title
    }

    override fun bindBody(items: List<String>?, row: Int, col: Int) {
        text_view.text = items!![col]
    }

    override fun bindSection(p0: List<String>?, row: Int, col: Int) {
        text_view.text = if (col == 0) "Section: " + (row + 1) else ""
    }
}