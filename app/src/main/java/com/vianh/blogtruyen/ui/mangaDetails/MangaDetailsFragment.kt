package com.vianh.blogtruyen.ui.mangaDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.vianh.blogtruyen.data.model.Manga
import com.vianh.blogtruyen.databinding.MangaDetailsFragmentBinding
import com.vianh.blogtruyen.ui.base.BaseFragment
import com.vianh.blogtruyen.ui.home.HomeActivity
import com.vianh.blogtruyen.utils.loadNetWorkImage
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MangaDetailsFragment : BaseFragment<MangaDetailsFragmentBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): MangaDetailsFragmentBinding = MangaDetailsFragmentBinding.inflate(inflater, container, false)

    val viewModel by viewModel<MangaDetailsViewModel> { parametersOf(getManga()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(requireBinding.toolbar)
        setup()
        observe()
    }

    private fun observe() {
        viewModel.mangaLiveData.observe(viewLifecycleOwner, ::onContentChange)
    }

    private fun onContentChange(manga: Manga) {
        with(requireBinding) {
            largeCover.loadNetWorkImage(manga.imageUrl)
            toolbar.title = manga.title
        }
    }

    private fun setup() {
        with(requireBinding) {
            pager.adapter = ContentPagerAdapter(this@MangaDetailsFragment)
            TabLayoutMediator(tabLayout, pager) { tab, position ->
                tab.text = when (position) {
                    0 -> "Chapters"
                    1 -> "Comments"
                    else -> "Unknown"
                }
            }.attach()
        }
        hostActivity?.hideBottomNav()
    }

    fun getManga(): Manga {
        val manga: Manga? = arguments?.getParcelable(MANGA_BUNDLE_KEY)
        return if (manga == null) {
            showToast("Failed to get manga details :(")
            (activity as AppCompatActivity).supportFragmentManager.popBackStack()
            Manga.getEmpty()
        } else {
            manga
        }
    }

    companion object {
        const val MANGA_BUNDLE_KEY = "MANGA_KEY"

        fun newInstance(manga: Manga): MangaDetailsFragment {
            val bundle = Bundle().apply {
                putParcelable(MANGA_BUNDLE_KEY, manga)
            }
            return MangaDetailsFragment().apply {
                arguments = bundle
            }
        }
    }
}