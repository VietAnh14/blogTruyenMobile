package com.vianh.blogtruyen.features.main

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit

class NavigationHelper(private val host: AppCompatActivity, @IdRes private val containerId: Int) {

    private val fragmentManager
        get() = host.supportFragmentManager

    fun changeFragment(fragment: Fragment, addToBackStack: Boolean = false, name: String? = null): Boolean {
        val fragmentManager = host.supportFragmentManager
        fragmentManager.commit {
            setReorderingAllowed(true)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            if (addToBackStack) {
                addToBackStack(name)
            }
            replace(containerId, fragment)
        }
        return true
    }


    fun openAsRoot(fragment: Fragment) {
        clearBackstack()
        changeFragment(fragment)
    }

    private fun  clearBackstack() {
        for (i in 0..fragmentManager.backStackEntryCount) {
            fragmentManager.popBackStackImmediate()
        }
    }

    fun navigateUp() {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            host.finish()
        }
    }
}