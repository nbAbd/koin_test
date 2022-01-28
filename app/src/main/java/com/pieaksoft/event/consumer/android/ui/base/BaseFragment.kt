package com.pieaksoft.event.consumer.android.ui.base

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.pieaksoft.event.consumer.android.ui.activities.main.IMainAction
import com.pieaksoft.event.consumer.android.utils.SHARED_PREFERENCES_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.lang.reflect.ParameterizedType
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment<VB : ViewBinding> : Fragment(),
    CoroutineScope {
    private var _binding: VB? = null
    val binding get() = _binding!!

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    protected val sharedPrefs: SharedPreferences by lazy {
        requireActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    var requiresBottomNavigation: Boolean = true
    var requiresActionBar: Boolean = false

    abstract fun setupView()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val type = javaClass.genericSuperclass
        val clazz = (type as ParameterizedType).actualTypeArguments[0] as Class<VB>
        val method = clazz.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        _binding = method.invoke(null, layoutInflater, container, false) as VB
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()

        when (requiresBottomNavigation) {
            true -> (requireActivity() as IMainAction).showBottomNavigation()
            else -> (requireActivity() as IMainAction).hideBottomNavigation()
        }

        when (requiresActionBar) {
            true -> (requireActivity() as IMainAction).showActionBar()
            else -> (requireActivity() as IMainAction).hideActionBar()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}