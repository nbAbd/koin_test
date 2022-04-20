package com.pieaksoft.event.consumer.android.utils.graph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler
import com.pieaksoft.event.consumer.android.R

class YAxisRenderer(
    viewPortHandler: ViewPortHandler?,
    yAxis: YAxis?,
    trans: Transformer?,
    private val context: Context
) : YAxisRenderer(viewPortHandler, yAxis, trans) {
    override fun drawYLabels(
        c: Canvas?,
        fixedPosition: Float,
        positions: FloatArray?,
        offset: Float
    ) {
        repeat(mYAxis.mEntryCount.minus(1)) {
            val labelText = mYAxis.getFormattedLabel(it)
            mAxisLabelPaint.color = getLabelColor(index = it)
            c?.drawText(
                labelText,
                fixedPosition,
                ((positions?.get(it * 2 + 1) ?: 0).toFloat() + offset),
                mAxisLabelPaint
            )
        }
    }

    /**
     * Returns specific color for given index
     * @param index the label index on chart
     * @return [Int] the color for index
     * */
    private fun getLabelColor(index: Int): Int {
        return when (index) {
            1 -> ContextCompat.getColor(context, R.color.toast_yellow)
            2 -> ContextCompat.getColor(context, R.color.toast_green)
            3 -> ContextCompat.getColor(context, R.color.toast_blue)
            4 -> ContextCompat.getColor(context, R.color.toast_red)
            else -> Color.TRANSPARENT
        }
    }
}