package com.pieaksoft.event.consumer.android.views

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.model.MyGantItem
import miguelbcr.ui.tableFixHeadesWrapper.TableFixHeaderAdapter

class BarCellViewGroup: FrameLayout,
   TableFixHeaderAdapter.BodyBinder<List<String>>{

    var card_item: ConstraintLayout? = null
    var myContext: Context? = null
    var myGantItemList: List<MyGantItem>? = null

   constructor(context: Context): super(context)

   constructor(context: Context, myGantItemList: List<MyGantItem>): super(context){
    this.myContext = context
    this.myGantItemList = myGantItemList
    LayoutInflater.from(context).inflate(R.layout.bar_view_item, this, true)
       card_item = findViewById<ConstraintLayout>(R.id.card_item)


   }

    override fun bindBody(bodyList: List<String>?, row: Int, col: Int) {
        when {
            bodyList!![col] == "error" -> {
                card_item?.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            }
            bodyList!![col] == "done" -> {
                card_item?.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
            }
            else -> {
                card_item?.setBackgroundResource(R.drawable.border_time)
            }
        }

    }
}