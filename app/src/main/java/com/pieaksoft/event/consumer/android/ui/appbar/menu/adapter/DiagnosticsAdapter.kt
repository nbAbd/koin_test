package com.pieaksoft.event.consumer.android.ui.appbar.menu.adapter

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.ItemDiagnosticCategoryBinding
import com.pieaksoft.event.consumer.android.databinding.ItemUsaRulesBinding
import com.pieaksoft.event.consumer.android.model.md.MD
import com.pieaksoft.event.consumer.android.model.md.MD.DiagnosticsCategory
import com.pieaksoft.event.consumer.android.model.md.MD.DiagnosticsContent
import com.pieaksoft.event.consumer.android.views.dp

class DiagnosticsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var diagnostics: List<MD> = emptyList()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_diagnostic_category -> {
                DiagnosticsCategoryViewHolder(
                    ItemDiagnosticCategoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            R.layout.item_usa_rules -> {
                DiagnosticsContentViewHolder(
                    ItemUsaRulesBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> throw IllegalStateException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val diagnostic = diagnostics[position]) {
            is DiagnosticsCategory -> (holder as DiagnosticsCategoryViewHolder).onBind(data = diagnostic)
            is DiagnosticsContent -> (holder as DiagnosticsContentViewHolder).onBind(data = diagnostic)
        }
    }

    override fun getItemCount(): Int = diagnostics.size

    override fun getItemViewType(position: Int): Int {
        return when (diagnostics[position]) {
            is DiagnosticsCategory -> R.layout.item_diagnostic_category
            is DiagnosticsContent -> R.layout.item_usa_rules
        }
    }

    private inner class DiagnosticsCategoryViewHolder(val binding: ItemDiagnosticCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: DiagnosticsCategory) {
            binding.categoryTitle.text = data.categoryName
        }
    }

    private inner class DiagnosticsContentViewHolder(val binding: ItemUsaRulesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: DiagnosticsContent) = with(binding) {
            ruleLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            ruleLabel.compoundDrawablePadding = 13.dp
            val diagnosticStatus = when (data.isFailed) {
                true -> DiagnosticStatus.FAILED
                else -> DiagnosticStatus.PASSED
            }
            val drawableStart = ContextCompat.getDrawable(root.context, diagnosticStatus.statusIcon)
            ruleLabel.setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, null, null)
            ruleLabel.text = data.content

            ruleValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            ruleValue.isAllCaps = true
            ruleValue.setTextColor(ContextCompat.getColor(root.context, diagnosticStatus.textColor))
            ruleValue.text = root.context.getString(diagnosticStatus.status)
        }
    }

    private enum class DiagnosticStatus(
        @DrawableRes val statusIcon: Int,
        @StringRes val status: Int,
        @ColorRes val textColor: Int
    ) {
        FAILED(R.drawable.ic_tick_red, R.string.failed, R.color.red),
        PASSED(R.drawable.ic_tick_green, R.string.passed, R.color.green2)
    }
}