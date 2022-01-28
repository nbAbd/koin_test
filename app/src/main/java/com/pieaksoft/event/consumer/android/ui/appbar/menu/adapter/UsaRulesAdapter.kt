package com.pieaksoft.event.consumer.android.ui.appbar.menu.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.databinding.ItemUsaRulesBinding

class UsaRulesAdapter : RecyclerView.Adapter<UsaRulesAdapter.ItemUsaViewHolder>() {
    var rules: Map<String, String> = emptyMap()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemUsaViewHolder {
        return ItemUsaViewHolder(
            ItemUsaRulesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemUsaViewHolder, position: Int) {
        rules.keys.toTypedArray()[position].also {
            holder.onBind(data = Pair(it, rules[it] ?: ""))
        }
    }

    override fun getItemCount(): Int = rules.size

    inner class ItemUsaViewHolder(val binding: ItemUsaRulesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(data: Pair<String, String>) {
            binding.apply {
                ruleLabel.text = data.first
                ruleValue.text = data.second
            }
        }
    }
}