package com.vianh.blogtruyen.features.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.databinding.HomeActivityBinding
import com.vianh.blogtruyen.features.base.BaseActivity
import com.vianh.blogtruyen.features.details.MangaDetailsFragment
import com.vianh.blogtruyen.features.favorites.FavoritesFragment
import com.vianh.blogtruyen.features.favorites.UpdateFavoriteWorker
import com.vianh.blogtruyen.features.feed.NewFeedFragment
import com.vianh.blogtruyen.features.history.HistoryFragment
import com.vianh.blogtruyen.features.local.LocalMangaFragment
import com.vianh.blogtruyen.utils.showToast
import com.vianh.blogtruyen.views.ViewHeightAnimator
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : BaseActivity<HomeActivityBinding>(), FragmentManager.OnBackStackChangedListener {

    override fun createBinding(): HomeActivityBinding = HomeActivityBinding.inflate(layoutInflater)

    private val viewModel: MainViewModel by viewModel()
    private lateinit var bottomNavAnimator: ViewHeightAnimator

    private val navigationHelper = NavigationHelper(this, R.id.host_fragment)

    private val rootFragments = setOf(
        HistoryFragment::class.java,
        FavoritesFragment::class.java,
        LocalMangaFragment::class.java,
        NewFeedFragment::class.java
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindViewModel()
        setupViews()

        if (!handleNewIntent(intent)) {
            setUpDefaultFragment()
        }
    }

    private fun bindViewModel() {
        viewModel.notificationCount.observe(this, this::onNotificationCountChange)
        viewModel.saveImageCompleteMessage.observe(this, this::showToast)
        viewModel.error.observe(this, { showToast(it.message ?: "Unknown error") })
    }

    private fun onNotificationCountChange(num: Int) {
        if (num == 0) {
            binding.bottomNav.removeBadge(R.id.bookmarks)
        } else {
            binding.bottomNav.getOrCreateBadge(R.id.bookmarks).apply {
                isVisible = true
                number = num
                backgroundColor = ContextCompat.getColor(this@MainActivity, R.color.colorAccent)
            }
        }
    }

    private fun setupViews() {
        with(binding) {
            bottomNav.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.home_menu -> {
                        navigationHelper.openAsRoot(NewFeedFragment.newInstance())
                        true
                    }
                    R.id.history -> {
                        navigationHelper.openAsRoot(HistoryFragment())
                        true
                    }
                    R.id.bookmarks -> {
                        navigationHelper.openAsRoot(FavoritesFragment())
                        true
                    }
                    R.id.downloads -> {
                        navigationHelper.openAsRoot(LocalMangaFragment())
                        true
                    }
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

    private fun findCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.host_fragment)
    }

    override fun onBackPressed() {
        val currentFragment = findCurrentFragment()
        if (currentFragment == null) {
            finish()
            return
        }

        when {
            currentFragment is NewFeedFragment -> super.onBackPressed()

            rootFragments.contains(currentFragment::class.java) -> {
                binding.bottomNav.selectedItemId = R.id.home_menu
            }

            else -> super.onBackPressed()

        }
    }

    fun navigateUp() {
        navigationHelper.navigateUp()
    }

    private fun hideBottomNav() {
        bottomNavAnimator.hide()
    }

    private fun showBottomNav() {
        bottomNavAnimator.show()
    }

    fun changeFragment(
        fragment: Fragment,
        addToBackStack: Boolean = false,
        name: String? = null
    ): Boolean = navigationHelper.changeFragment(fragment, addToBackStack, name)

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleNewIntent(intent)
    }

    private fun handleNewIntent(intent: Intent?): Boolean {
        if (intent == null)
            return false

        return when(intent.action) {
            ACTION_FAVORITE_UPDATE -> {
                selectBottomNavItem(R.id.bookmarks)
                true
            }
            ACTION_DOWNLOAD_COMPLETE -> {
                val manga = intent.getParcelableExtra<Manga>(MangaDetailsFragment.MANGA_BUNDLE_KEY) ?: return false
                val isOffline = intent.getBooleanExtra(MangaDetailsFragment.OFFLINE_MODE_KEY, false)
                changeFragment(MangaDetailsFragment.newInstance(manga, isOffline), true)
                true
            }
            else -> false
        }
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

    companion object {
        const val ACTION_FAVORITE_UPDATE = "com.vianh.favorite.update"
        const val ACTION_DOWNLOAD_COMPLETE = "com.vianh.download.complete"
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}