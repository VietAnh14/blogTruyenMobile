package com.vianh.blogtruyen.utils

import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


fun <V: ViewBinding> Fragment.viewBinding(creator: (View) -> V): FragmentBindingDelegate<V> {
    return FragmentBindingDelegate(this, creator)
}


class FragmentBindingDelegate<V: ViewBinding>(host: Fragment, val creator: (View) -> V): ReadOnlyProperty<Fragment, V>{

    var binding: V? = null

    init {
        host.viewLifecycleOwner.lifecycle.addObserver(object: LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                binding = null
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): V {
        return binding ?: createBinding(thisRef)
    }

    private fun createBinding(fragment: Fragment): V {
        return creator.invoke(fragment.requireView()).also { binding = it }
    }
}