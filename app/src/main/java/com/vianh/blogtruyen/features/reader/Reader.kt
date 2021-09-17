package com.vianh.blogtruyen.features.reader

import androidx.fragment.app.Fragment

abstract class Reader: Fragment() {
    abstract fun onContentChange()
}