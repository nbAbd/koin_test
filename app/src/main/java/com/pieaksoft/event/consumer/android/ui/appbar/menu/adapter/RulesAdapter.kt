package com.pieaksoft.event.consumer.android.ui.appbar.menu.adapter

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
import com.pieaksoft.event.consumer.android.databinding.RulesRecyclerItemBinding
import com.pieaksoft.event.consumer.android.model.rules.Rules
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
    ): RulesAdapter.RulesViewHolder {
        return RulesViewHolder(
            RulesRecyclerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RulesAdapter.RulesViewHolder, position: Int) {
        when (val rulesData = rules[position]) {
            is RulesData.Header -> holder.bindHeader(rulesData.titles)
            is RulesData.Content -> holder.bind(rulesData.rules)
        }
    }

    override fun getItemCount(): Int = rules.size

    inner class RulesViewHolder(val binding: RulesRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(content: Rules) = with(binding) {
            root.apply {
                dividerDrawable = ShapeDrawable(RectShape()).apply {
                    intrinsicWidth = 1
                    paint.color = ContextCompat.getColor(context, R.color.rules_separator)
                }
            }
            root.setBackgroundColor(root.context.getColor(R.color.half_dark_grey))
            rulesTime.text = content.rulesTime
            rulesType.text = content.rulesType
        }

        fun bindHeader(titles: List<String>) = with(binding) {
            root.apply {
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    color =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.grey_type))
                    cornerRadii = floatArrayOf(12f, 12f, 12f, 12f, 0f, 0f, 0f, 0f)
                }

                dividerDrawable = ShapeDrawable(RectShape()).apply {
                    intrinsicWidth = 1
                    paint.color = ContextCompat.getColor(context, R.color.rules_separator)
                }
            }
            rulesType.setTypeface(rulesType.typeface, Typeface.BOLD)
            rulesTime.setTypeface(rulesTime.typeface, Typeface.BOLD)
            root.children
                .filter { it is AppCompatTextView }
                .forEachIndexed { index, textView ->
                    (textView as AppCompatTextView).text = titles[index]
                }
        }
    }
}