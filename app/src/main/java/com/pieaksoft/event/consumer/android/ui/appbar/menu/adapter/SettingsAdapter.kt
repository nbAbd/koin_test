package com.pieaksoft.event.consumer.android.ui.appbar.menu.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pieaksoft.event.consumer.android.databinding.ItemSettingBinding

class SettingsAdapter(val onClick: (item: SettingItem) -> Unit) :
    RecyclerView.Adapter<SettingsAdapter.ItemSettingViewHolder>() {
    var list = emptyList<SettingItem>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSettingViewHolder {
        return ItemSettingViewHolder(
            ItemSettingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemSettingViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ItemSettingViewHolder(val binding: ItemSettingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun onBind(setting: SettingItem) {
            binding.apply {
                settingLabel.text = setting.value
            }
            binding.root.setOnClickListener {
                onClick(setting)
            }
        }
    }

    enum class SettingItem {
        SIGNATURE {
            override val value: String
                get() = "Signature"
        };

        abstract val value: String
    }
}