package com.example.myapplication.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import com.example.myapplication.activities.user_profile.ProfileFragment

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        transectToFragments()
    }
    private fun transectToFragments() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.FLAuth, RegisterFragment())
        transaction.commit()
    }
}