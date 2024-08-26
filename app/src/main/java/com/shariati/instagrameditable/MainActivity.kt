package com.shariati.instagrameditable

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shariati.instagrameditable.databinding.ActivityMainBinding
import com.shariati.instagrameditable.fragments.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ProfileFragment())
            .commit()
    }
}