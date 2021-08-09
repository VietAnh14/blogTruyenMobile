package com.vianh.blogtruyen.features.home

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.databinding.HomeActivityBinding
import com.vianh.blogtruyen.features.base.BaseActivity
import com.vianh.blogtruyen.features.favorites.FavoritesFragment
import com.vianh.blogtruyen.features.favorites.UpdateFavoriteWorker
import com.vianh.blogtruyen.features.feed.NewFeedFragment
import com.vianh.blogtruyen.features.history.HistoryFragment
import com.vianh.blogtruyen.features.local.LocalMangaFragment
import com.vianh.blogtruyen.views.ViewHeightAnimator

class HomeActivity : BaseActivity<HomeActivityBinding>(), FragmentManager.OnBackStackChangedListener {

    override fun createBinding(): HomeActivityBinding = HomeActivityBinding.inflate(layoutInflater)

    private lateinit var bottomNavAnimator: ViewHeightAnimator

    private val rootFragments = setOf(
        HistoryFragment::class.java,
        FavoritesFragment::class.java,
        LocalMangaFragment::class.java,
        NewFeedFragment::class.java
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        setUpDefaultFragment()
    }

    private fun setupViews() {
        with(binding) {
            bottomNav.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.home_menu -> changeFragment(NewFeedFragment.newInstance())
                    R.id.history -> changeFragment(HistoryFragment())
                    R.id.bookmarks -> changeFragment(FavoritesFragment())
                    R.id.downloads -> changeFragment(LocalMangaFragment())
                    else -> false
                }
            }

            bottomNav.setOnItemReselectedListener {  }
            bottomNavAnimator = ViewHeightAnimator(bottomNav)
        }

        supportFragmentManager.addOnBackStackChangedListener(this)
        syncBottomNavState()
    }

    private fun setUpDefaultFragment() {
        val defaultFragment =
            supportFragmentManager.findFragmentById(R.id.host_fragment) ?: NewFeedFragment()
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
            supportFragmentManager.popBackStack()
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

    override fun onBackStackChanged() {
        syncBottomNavState()
    }

    private fun syncBottomNavState() {
        val fragment = supportFragmentManager.findFragmentById(R.id.host_fragment) ?: return
        if (rootFragments.contains(fragment::class.java)) {
            showBottomNav()
        } else {
            hideBottomNav()
        }
    }

    fun selectBottomNavItem(@IdRes menuId: Int) {
        binding.bottomNav.selectedItemId = menuId
    }
}