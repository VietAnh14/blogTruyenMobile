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

class HomeActivity : BaseActivityVB<HomeActivityBinding>() {

    override fun createBinding(): HomeActivityBinding = HomeActivityBinding.inflate(layoutInflater)

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
        }
    }

    fun setupToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 0)
        }
    }

    fun changeFragment(fragment: Fragment): Boolean {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.host_fragment, fragment)
        }
        return true
    }
}