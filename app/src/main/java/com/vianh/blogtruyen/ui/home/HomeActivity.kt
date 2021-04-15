package com.vianh.blogtruyen.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.HomeActivityBinding
import com.vianh.blogtruyen.ui.base.BaseActivityVB
import com.vianh.blogtruyen.ui.bookmarks.BookmarksFragment
import com.vianh.blogtruyen.ui.history.HistoryFragment
import com.vianh.blogtruyen.views.ViewHeightAnimator

class HomeActivity : BaseActivityVB<HomeActivityBinding>() {

    override fun createBinding(): HomeActivityBinding = HomeActivityBinding.inflate(layoutInflater)

    private lateinit var bottomNavAnimator: ViewHeightAnimator
    private val rootFragments = mutableMapOf<Int, Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        setUpDefaultFragment()
    }

    private fun setupViews() {
        with(binding) {
            bottomNav.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.home_menu -> changeFragment(HomeFragment())
                    R.id.history -> changeFragment(HistoryFragment())
                    R.id.bookmarks -> changeFragment(BookmarksFragment())
                    else -> false
                }
            }
            bottomNav.setOnNavigationItemReselectedListener {

            }
            bottomNavAnimator = ViewHeightAnimator(bottomNav)
        }
    }

    private fun setUpDefaultFragment() {
        val defaultFragment =
            supportFragmentManager.findFragmentById(R.id.host_fragment) ?: HomeFragment()
        changeFragment(defaultFragment)
    }

    fun setupToolbar(toolbar: Toolbar, title: String? = null) {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            val backStackCount = supportFragmentManager.backStackEntryCount
            setDisplayHomeAsUpEnabled(backStackCount > 0)
            setDisplayShowHomeEnabled(backStackCount > 0)
            if (title != null) {
                setTitle(title)
            }
        }
    }

    fun navigateUp() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager
                .popBackStack()
        } else {
            finish()
        }
    }

    fun hideBottomNav() {
        bottomNavAnimator.hide()
    }

    fun showBottomNav() {
        bottomNavAnimator.show()
    }

    fun hideSystemUI() {
        window.decorView.systemUiVisibility = flagHideSystemUI
    }

    fun showSystemUI() {
        window.decorView.systemUiVisibility = flagShowSystemUI
    }

    fun changeFragment(
        fragment: Fragment,
        addToBackStack: Boolean = false,
        name: String? = null
    ): Boolean {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            if (addToBackStack) {
                addToBackStack(name)
            }
            replace(R.id.host_fragment, fragment)
        }
        return true
    }

    companion object {

        const val flagHideSystemUI = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        const val flagShowSystemUI = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}