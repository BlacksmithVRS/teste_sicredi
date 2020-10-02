package com.ortiz91.testesicredi.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ortiz91.testesicredi.fragment.HomeListFragment
import com.ortiz91.testesicredi.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.content, HomeListFragment.newInstance()).commit()
    }
}
