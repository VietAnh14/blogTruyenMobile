package com.vianh.blogtruyen.features.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<B: ViewBinding>: AppCompatActivity() {
    lateinit var binding: B
    val windowInsetController
        get() = ViewCompat.getWindowInsetsController(window.decorView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make layout take full screen
        // we'll manually handle insets in fragments
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = createBinding()
        setContentView(binding.root)
    }

    abstract fun createBinding(): B

    fun canNavigateUp(): Boolean = supportFragmentManager.backStackEntryCount > 0

    fun hideSystemUI() {
        windowInsetController?.apply {
            // Configure the behavior of the hidden system bars
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            // Hide both the status bar and the navigation bar
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    fun showSystemUI() {
        windowInsetController?.apply {
            // Configure the behavior of the hidden system bars
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            // Hide both the status bar and the navigation bar
            show(WindowInsetsCompat.Type.systemBars())
        }
    }
}