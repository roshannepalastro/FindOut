package com.example.myapplication.utils

import android.content.Context
import android.widget.Toast
import com.example.myapplication.authentication.RegisterFragment

class Utils {
    companion object{
        fun toastIt(string: String,context: Context) {
            try{
                Toast.makeText(
                    context,
                    string,
                    Toast.LENGTH_SHORT
                ).show()
            }
            catch (e:Exception){

            }
        }
    }

}