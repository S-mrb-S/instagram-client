package com.shariati.instagrameditable.fragments.insight

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class CustomPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val scaleFactor = 0.8f + (1 - Math.abs(position)) * 0.2f

        // تغییر مقیاس
        page.scaleX = scaleFactor
        page.scaleY = scaleFactor

        // اطمینان از اینکه صفحه همیشه قابل مشاهده است
       // page.visibility = View.VISIBLE
    }
}