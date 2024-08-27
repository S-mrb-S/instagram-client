package com.shariati.instagrameditable.fragments.insight

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class CustomPageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val scaleFactor = 0.8f + (1 - Math.abs(position)) * 0.2f

        // تغییر مقیاس
        page.scaleX = scaleFactor
        page.scaleY = scaleFactor

        // تنظیم موقعیت X برای نمایش درست آیتم‌ها
        page.translationX = -position * page.width / 5   // تقسیم عرض به 5 برای نمایش 5 آیتم
    }
}