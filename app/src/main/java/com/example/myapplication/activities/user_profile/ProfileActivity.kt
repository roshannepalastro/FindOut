package com.example.myapplication.activities.user_profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        transectToFragments()
    }

    private fun transectToFragments() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.FLFrameLayout, ProfileFragment())
        transaction.commit()
    }
}