package com.shariati.instagrameditable.utils

import android.view.View
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class DepthPageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {

        MarginPageTransformer(10)
        val r = 1 - abs(position)
        page.scaleY = 0.75f + r * 0.22f

    }
}