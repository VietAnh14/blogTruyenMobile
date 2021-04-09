package com.vianh.blogtruyen.ui.home

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        changeFragment(HomeFragment())
    }

    fun setupViews() {
        with(binding) {
            bottomNav.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.home_menu -> changeFragment(HomeFragment())
                    R.id.history -> changeFragment(HistoryFragment())
                    R.id.bookmarks -> changeFragment(BookmarksFragment())
                    else -> false
                }
            }
            bottomNavAnimator = ViewHeightAnimator(bottomNav)
        }
    }

    fun setupToolbar(toolbar: Toolbar, title: String? = null) {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 1)
            if (title != null) {
                setTitle(title)
            }
        }
    }

    fun hideBottomNav() {
        bottomNavAnimator.hide()
    }

    fun showBottomNav() {
        bottomNavAnimator.show()
    }

    fun changeFragment(fragment: Fragment, addToBackStack: Boolean = false, name: String? = null): Boolean {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            if (addToBackStack) {
                addToBackStack(name)
            }
            replace(R.id.host_fragment, fragment)
        }
        return true
    }
}