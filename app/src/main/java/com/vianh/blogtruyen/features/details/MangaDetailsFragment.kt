package com.vianh.blogtruyen.features.details

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.vianh.blogtruyen.R
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.databinding.MangaDetailsFragmentBinding
import com.vianh.blogtruyen.features.base.BaseFragment
import com.vianh.blogtruyen.features.main.MainActivity
import com.vianh.blogtruyen.utils.PendingIntentHelper
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MangaDetailsFragment : BaseFragment<MangaDetailsFragmentBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): MangaDetailsFragmentBinding = MangaDetailsFragmentBinding.inflate(inflater, container, false)

    private val viewModel: MangaDetailsViewModel by viewModel {
        parametersOf(getManga(), getOfflineState())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(requireBinding.toolbar)
        setup()
        observe()
    }

    private fun observe() {
        viewModel.manga.observe(viewLifecycleOwner, ::onContentChange)
    }

    private fun onContentChange(manga: Manga) {
        with(requireBinding) {
            toolbar.title = manga.title
        }
    }

    private fun setup() {
        with(requireBinding) {
            hostActivity?.setupToolbar(toolbar)
            pager.adapter = ContentPagerAdapter(this@MangaDetailsFragment)
            TabLayoutMediator(tabLayout, pager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.manga_info)
                    1 -> getString(R.string.comment)
                    else -> "Unknown"
                }
            }.attach()
        }
    }

    private fun getManga(): Manga {
        val manga: Manga? = arguments?.getParcelable(MANGA_BUNDLE_KEY)
        return if (manga == null) {
            showToast("Failed to get manga details :(")
            (activity as AppCompatActivity).supportFragmentManager.popBackStack()
            Manga.getEmpty()
        } else {
            manga
        }
    }

    private fun getOfflineState(): Boolean {
        return arguments?.getBoolean(OFFLINE_MODE_KEY) ?: false
    }

    companion object {
        const val MANGA_BUNDLE_KEY = "MANGA_KEY"
        const val OFFLINE_MODE_KEY = "OFFLINE"

        fun newInstance(manga: Manga, isOffline: Boolean = false): MangaDetailsFragment {
            val bundle = Bundle(2).apply {
                putParcelable(MANGA_BUNDLE_KEY, manga)
                putBoolean(OFFLINE_MODE_KEY, isOffline)
            }
            return MangaDetailsFragment().apply {
                arguments = bundle
            }
        }

        fun getPendingIntent(context: Context, manga: Manga, isOffline: Boolean = false): PendingIntent {
            val intent = Intent(context, MainActivity::class.java).apply {
                addFlags(PendingIntentHelper.UPDATE_INTENT_FLAGS)
                action = MainActivity.ACTION_DOWNLOAD_COMPLETE
                putExtra(MANGA_BUNDLE_KEY, manga)
                putExtra(OFFLINE_MODE_KEY, isOffline)
            }

            return PendingIntent.getActivity(context, 0, intent, PendingIntentHelper.getPendingFlagCompat())
        }
    }
}