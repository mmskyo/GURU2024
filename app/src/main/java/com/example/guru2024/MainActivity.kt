package com.example.guru2024

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.guru2024.databinding.ActivityMainBinding

private const val TAG_HOME = "home_fragment"
private const val TAG_REC = "rec_fragment"
private const val TAG_INFO = "info_fragment"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(TAG_HOME, HomeFragment())

        userId = intent.getStringExtra("ID") ?: ""


        binding.nvMain.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.recFragment -> setFragment(TAG_REC, RecFragment())
                R.id.infoFragment -> setFragment(TAG_INFO, InfoFragment())
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager : FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null) {
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val home = manager.findFragmentByTag(TAG_HOME)
        val rec = manager.findFragmentByTag(TAG_REC)
        val info = manager.findFragmentByTag(TAG_INFO)

        if (home != null) {
            fragTransaction.hide(home)
        }
        if (rec != null) {
            fragTransaction.hide(rec)
        }
        if (info != null) {
            fragTransaction.hide(info)
        }

        if (tag == TAG_HOME) {
            if (home != null) {
                fragTransaction.show(home)
            }
        } else if (tag == TAG_REC) {
            if (rec != null) {
                fragTransaction.show(rec)
            }
        } else if (tag == TAG_INFO) {
            if (info != null) {
                fragTransaction.show(info)
            }
        }
        fragTransaction.commitNowAllowingStateLoss()
    }
}