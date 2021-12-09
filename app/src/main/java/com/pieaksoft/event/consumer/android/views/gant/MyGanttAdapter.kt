package com.pieaksoft.event.consumer.android.views.gant

import android.content.Context
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.model.MyGantItem
import com.pieaksoft.event.consumer.android.utils.Common
import miguelbcr.ui.tableFixHeadesWrapper.TableFixHeaderAdapter

class MyGanttAdapter(
    private val context: Context, private val myGantItemList: List<MyGantItem>
) :
    TableFixHeaderAdapter<String, DefaultCellViewGroup,
            String, TimeCellView,
            List<String>,
            DefaultCellViewGroup,
            BarCellViewGroup,
            DefaultCellViewGroup>(context) {

    override fun inflateFirstHeader(): DefaultCellViewGroup {
        return DefaultCellViewGroup(context, myGantItemList)
    }

    override fun inflateHeader(): TimeCellView {
        return TimeCellView(context, myGantItemList)
    }

    override fun inflateFirstBody(): DefaultCellViewGroup {
        val defaultCellViewGroup = DefaultCellViewGroup(context, myGantItemList)

        return defaultCellViewGroup
    }

    override fun inflateBody(): BarCellViewGroup {
       return BarCellViewGroup(context, myGantItemList)
    }

    override fun inflateSection(): DefaultCellViewGroup {
       return DefaultCellViewGroup(context, myGantItemList)
    }

    override fun getHeaderWidths(): MutableList<Int> {
        val headerWidths: MutableList<Int> = ArrayList()

        headerWidths.add(context.resources.getDimension(R.dimen._50dp).toInt())

        for (i in 0 until Common.COLUMN_COUNT) headerWidths.add(context.resources.getDimension(R.dimen._25dp).toInt())
        return headerWidths

    }

    override fun getHeaderHeight(): Int {
        return context.resources.getDimension(R.dimen._40dp).toInt()
    }

    override fun getSectionHeight(): Int {
        return context.resources.getDimension(R.dimen._40dp).toInt()
    }

    override fun getBodyHeight(): Int {
        return context.resources.getDimension(R.dimen._40dp).toInt()
    }

    override fun isSection(p0: MutableList<List<String>>?, p1: Int): Boolean {
       return false
    }
}