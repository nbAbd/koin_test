package com.pieaksoft.event.consumer.android.ui.appbar.menu.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.ItemEventListBinding
import com.pieaksoft.event.consumer.android.databinding.ItemEventListHeaderBinding
import com.pieaksoft.event.consumer.android.model.EditEvent
import com.pieaksoft.event.consumer.android.model.Event
import com.pieaksoft.event.consumer.android.model.EventInsertCode
import com.pieaksoft.event.consumer.android.model.formatDuration

class EventListAdapter(private val editCallback: (Event) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var list = emptyList<EditEvent>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_event_list_header -> {
                EditEventHeaderViewHolder(
                    ItemEventListHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            R.layout.item_event_list -> {
                EditEventContentViewHolder(
                    (ItemEventListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ))
                )
            }
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val editEvent = list[position]) {
            is EditEvent.Header -> (holder as EditEventHeaderViewHolder).bind(titles = editEvent.titles)
            is EditEvent.Content -> (holder as EditEventContentViewHolder).bind(event = editEvent.event)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is EditEvent.Header -> R.layout.item_event_list_header
            is EditEvent.Content -> R.layout.item_event_list
        }
    }

    inner class EditEventHeaderViewHolder(val binding: ItemEventListHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(titles: List<String>) {
            binding.root.apply {
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    color =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue_indigo))
                    cornerRadii = floatArrayOf(12f, 12f, 12f, 12f, 0f, 0f, 0f, 0f)
                }

                dividerDrawable = ShapeDrawable(RectShape()).apply {
                    intrinsicWidth = 2
                    paint.color = ContextCompat.getColor(context, R.color.separator)
                }
            }
            binding.root.children
                .filter { it is AppCompatTextView }
                .forEachIndexed { index, textView ->
                    (textView as AppCompatTextView).text = titles[index]
                }
        }
    }

    inner class EditEventContentViewHolder(val binding: ItemEventListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) = with(binding) {
            root.apply {
                dividerDrawable = ShapeDrawable(RectShape()).apply {
                    intrinsicWidth = 2
                    paint.color = ContextCompat.getColor(context, R.color.separator)
                }
            }

            EventTypeAppearance.getByCode(event.eventCode ?: "").appearance.apply {
                eventType.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(root.context, first))
                eventType.text = root.context.getString(second)
            }
            eventType.setTextColor(
                ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.white))
            )

            eventTime.text = event.time

            eventDuration.text = event.formatDuration()

            "none".also {
                eventTr.text = it
                eventSh.text = it
            }

            eventEditBtn.setOnClickListener { editCallback(event) }
        }
    }

    enum class EventTypeAppearance {
        OFF {
            override val appearance =
                Pair(R.color.red, R.string.off)
        },
        SLEEP {
            override val appearance =
                Pair(R.color.flying_fish_blue, R.string.sb)
        },
        DRIVING {
            override val appearance =
                Pair(R.color.ufo_green, R.string.drive)
        },
        ON {
            override val appearance =
                Pair(R.color.orange, R.string.on)
        };

        abstract val appearance: Pair<Int, Int>

        companion object {
            private val DEFAULT = OFF
            fun getByCode(code: String) =
                values().find { it.name == EventInsertCode.getByCode(code).name.uppercase() }
                    ?: DEFAULT
        }
    }
}