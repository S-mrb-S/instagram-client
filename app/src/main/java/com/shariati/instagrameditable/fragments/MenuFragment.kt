package com.shariati.instagrameditable.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.shariati.instagrameditable.R
import com.shariati.instagrameditable.databinding.FragmentMenuBinding
import com.shariati.instagrameditable.utils.getProfilePictureUrl

class MenuFragment : Fragment() {

    private lateinit var binding: FragmentMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(layoutInflater)

        Glide.with(requireContext()).load(getProfilePictureUrl(requireContext()))
            .into(binding.bottomNavigationAvatar)

        binding.icBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.slide_down)
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
        }

        binding.consAccount.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.slide_in_right,  // انیمیشن ورود
                R.anim.slide_out_left,  // انیمیشن خروج
                R.anim.slide_in_right,  // انیمیشن ورود برای بازگشت به صفحه قبل
                R.anim.slide_out_left   // انیمیشن خروج برای بازگشت به صفحه قبل
            )
            transaction.replace(R.id.fragment_container, SettingFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.goToArchive.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.slide_in_right,  // انیمیشن ورود
                R.anim.slide_out_left,  // انیمیشن خروج
                R.anim.slide_in_right,  // انیمیشن ورود برای بازگشت به صفحه قبل
                R.anim.slide_out_left   // انیمیشن خروج برای بازگشت به صفحه قبل
            )
            transaction.replace(R.id.fragment_container, ArchiveFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // why ??
//        Handler(Looper.getMainLooper()).postDelayed({
//            binding.progressbarLoading.visibility = View.GONE
//            binding.layoutMainMenu.visibility = View.VISIBLE
//        },1000)
    }
}