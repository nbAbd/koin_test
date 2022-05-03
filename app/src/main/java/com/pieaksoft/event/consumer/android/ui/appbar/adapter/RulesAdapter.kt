package com.pieaksoft.event.consumer.android.ui.appbar.adapter

import android.content.res.ColorStateList
import android.graphics.Typeface
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
import com.pieaksoft.event.consumer.android.databinding.ItemRuleBinding
import com.pieaksoft.event.consumer.android.model.rules.Rule
import com.pieaksoft.event.consumer.android.model.rules.RulesData

class RulesAdapter : RecyclerView.Adapter<RulesAdapter.RulesViewHolder>() {
    var rules: List<RulesData> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RulesViewHolder {
        return RulesViewHolder(
            ItemRuleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RulesViewHolder, position: Int) {
        when (val rulesData = rules[position]) {
            is RulesData.Header -> holder.bindHeader(rulesData.titles)
            is RulesData.Content -> holder.bind(rulesData.rule)
        }
    }

    override fun getItemCount(): Int = rules.size

    inner class RulesViewHolder(val binding: ItemRuleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(content: Rule) = with(binding) {
            root.apply {
                dividerDrawable = ShapeDrawable(RectShape()).apply {
                    intrinsicWidth = 1
                    paint.color = ContextCompat.getColor(context, R.color.separator)
                }
                setBackgroundColor(context.getColor(R.color.black_bars_light))
            }
            time.text = content.Time
            type.text = content.Type
        }

        fun bindHeader(titles: List<String>) = with(binding) {
            root.apply {
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    color =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.half_blue))
                    cornerRadii = floatArrayOf(12f, 12f, 12f, 12f, 0f, 0f, 0f, 0f)
                }

                dividerDrawable = ShapeDrawable(RectShape()).apply {
                    intrinsicWidth = 1
                    paint.color = ContextCompat.getColor(context, R.color.separator)
                }
            }

            root.children
                .filterIsInstance(AppCompatTextView::class.java)
                .forEachIndexed { index, textView ->
                    textView.text = titles[index]
                    textView.setTypeface(type.typeface, Typeface.BOLD)
                    textView.setTextColor(root.context.getColor(R.color.secondary_gray))
                }
        }
    }
}