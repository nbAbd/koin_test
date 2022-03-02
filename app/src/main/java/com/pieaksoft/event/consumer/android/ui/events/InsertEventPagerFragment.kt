package com.pieaksoft.event.consumer.android.ui.events

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentInsertEventPagerBinding
import com.pieaksoft.event.consumer.android.ui.events.adapter.InsertEventPagerAdapter

class InsertEventPagerDialog(private val dismissCallBack: () -> Unit) : DialogFragment(),
    InsertEventListener {
    private val pagerAdapter by lazy { InsertEventPagerAdapter(this, this) }
    private var _binding: FragmentInsertEventPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.ThemeDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.decorView?.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInsertEventPagerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupView() = with(binding) {
        container.setOnTouchListener { v, event ->
            if (event?.action == MotionEvent.ACTION_DOWN) {
                dialog?.dismiss()
            }
            true
        }
        viewPager.adapter = pagerAdapter
    }

    override fun onDestroyView() {
        dismissCallBack.invoke()
        super.onDestroyView()
        _binding = null
    }

    override fun onBack() {
        if (binding.viewPager.currentItem == 0) {
            dialog?.dismiss()
            return
        }
        binding.viewPager.currentItem -= 1
    }

    override fun onNext() {
        if (binding.viewPager.currentItem == 1) {
            return
        }
        binding.viewPager.currentItem += 1
    }

    override fun onComplete() {
        dialog?.dismiss()
    }
}

interface InsertEventListener {
    fun onBack()
    fun onNext()
    fun onComplete()
}