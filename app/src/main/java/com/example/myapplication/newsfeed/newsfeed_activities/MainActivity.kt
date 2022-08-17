package com.example.myapplication.newsfeed.newsfeed_activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.activities.user_profile.ProfileActivity
import com.example.myapplication.chat.ChatActivity
import com.example.myapplication.models.Dummy
import com.example.myapplication.models.Feed
import com.example.myapplication.models.User
import com.example.myapplication.newsfeed.adapter.RecyclerViewAdapter
import com.example.myapplication.network.firebase_connection.FirestoreConnect
import kotlinx.coroutines.*

var USER: User = User()

class MainActivity : AppCompatActivity() {
    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mBTPost = findViewById<Button>(R.id.BTPost)
        val mETPost = findViewById<EditText>(R.id.ETPost)
        val mBTChat = findViewById<CardView>(R.id.BTChat)
        val mBTNewsFeed = findViewById<CardView>(R.id.BTNewsFeed)
        val mBTProfile = findViewById<CardView>(R.id.BTProfile)
        val recyclerview = findViewById<RecyclerView>(R.id.recycler_view)

        val adapter = RecyclerViewAdapter()
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter
        val newsFeed = FirestoreConnect()

       
        GlobalScope.launch(Dispatchers.IO) {
            newsFeed.getAllNewsFeeds()
            withContext(Dispatchers.Main) {
                adapter.updateList(newsFeed.getListInAdapter())
            }

        }


        mBTNewsFeed.setOnClickListener {

            //getting data in IO thread
            GlobalScope.launch(Dispatchers.IO) {
                newsFeed.getAllNewsFeeds()
                withContext(Dispatchers.Main) {
                    adapter.updateList(newsFeed.getListInAdapter())
                }
            }


        }

        mBTChat.setOnClickListener {
            val intent = Intent(this@MainActivity, ChatActivity::class.java)
            startActivity(intent)
        }


        mBTProfile.setOnClickListener {

            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            intent.putExtra("userID", USER.id)
            startActivity(intent)
        }

        mBTPost.setOnClickListener {
            val postContent = mETPost.text.toString()

            if (postContent.isNotEmpty()) {
                //Setting et empty after post
                mETPost.setText("")
                GlobalScope.launch(Dispatchers.IO) {
                    val feed = FirestoreConnect().getUserName()?.let { it1 ->
                        Feed(
                            FirestoreConnect().getUserID(),
                            it1,
                            postContent
                        )
                    }
                    if (feed != null) {
                        newsFeed.insertFeed(this@MainActivity, feed, Dummy())
                    }
                    newsFeed.getAllNewsFeeds()
                    //Update to recycler view
                    withContext(Dispatchers.Main) {
                        adapter.updateList(newsFeed.getListInAdapter())
                    }


                }


            }


        }
    }
}





