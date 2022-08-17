package com.example.myapplication.network.firebase_connection


import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.myapplication.activities.user_profile.ProfileActivity
import com.example.myapplication.models.*
import com.example.myapplication.newsfeed.newsfeed_activities.MainActivity
import com.example.myapplication.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await

private const val TAG = "FireStore Connect: "

class FirestoreConnect {

    private val mFireStore = FirebaseFirestore.getInstance()
    private val collectionRef = mFireStore.collection(Constants.USERS)
    private val list = ArrayList<NewsFeedClass>()
    private val usersNewsFeedsList = ArrayList<NewsFeedClass>()


    suspend fun getAllNewsFeeds(): ArrayList<NewsFeedClass> {

        list.clear()
        val querySnapshot = collectionRef.get().await()
        val tasks: MutableList<Deferred<DocumentSnapshot>> = mutableListOf()
        for (document in querySnapshot) {
            val currDocRef = document.reference
            val deferredTask = currDocRef
                .collection(Constants.UserProfileInfo)
                .document(Constants.Profile)
                .get()
                .asDeferred()
            tasks.add(deferredTask)
        }

        tasks.awaitAll().forEach {
            val newsFeedClass = NewsFeedClass()
            newsFeedClass.username = it.toObject<User>()?.userName.toString()
//            Log.d(TAG, "getSomethingINotKnow:$username ")


            val nfQuerySnapshot = it.reference.collection(Constants.NewsFeed).get().await()

            for (i in nfQuerySnapshot) {
                newsFeedClass.content = i.toObject<FeedNewsFeed>().newsfeed
                list.add(newsFeedClass)
            }
        }
        list.shuffle()
        return list
    }

    fun getListInAdapter(): ArrayList<NewsFeedClass> {
        return list
    }

    fun insertFeed(activity: MainActivity, feed: Feed, dummy: Dummy) {
        val feedNewsFeed = FeedNewsFeed(feed.newsFeedContent)
        val doc = collectionRef.document(feed.id)
        doc.set(dummy, SetOptions.merge())


        doc.collection(Constants.UserProfileInfo)
            .document(Constants.Profile).collection(Constants.NewsFeed).document()
            .set(feedNewsFeed, SetOptions.merge()).addOnSuccessListener {
                Toast.makeText(
                    activity,
                    "+FeedAdded",
                    Toast.LENGTH_SHORT
                ).show()

            }
            .addOnFailureListener {
                Toast.makeText(
                    activity,
                    "feedNot added",
                    Toast.LENGTH_SHORT
                ).show()
            }


    }

    fun insertProfileInfo(user: User, activity: ProfileActivity) {


        val userCollectionReference = mFireStore.collection(Constants.USERS)
        val docRef = userCollectionReference.document(user.id)
            .collection(Constants.UserProfileInfo)
            .document(Constants.Profile)
        if (user.userName != "") {
            val x = user.userName
            docRef.update("userName", x).addOnSuccessListener {
            }
        }

        if (user.address != "") {
            docRef.update("address", user.address)
        }
        if (user.phoneNo != "") {
            docRef.update("phoneNo", user.phoneNo)
        }
        if (user.college != "") {
            docRef.update("college", user.college)
        }


    }

    suspend fun getUsersNewsfeed(user: User): ArrayList<NewsFeedClass> {
        usersNewsFeedsList.clear()
        val docRef = collectionRef.document(user.id)
        val newsFeedQuerySnapshot =
            docRef
                .collection(Constants.UserProfileInfo).document(Constants.Profile)
                .collection(Constants.NewsFeed).get().await()

        //Traversing through each document in newsfeed
        for (documentOfFeed in newsFeedQuerySnapshot) {

            val thisDocRef = documentOfFeed.reference
            // val thisDocID = documentOfFeed.toObject<User>()

            val feed =
                thisDocRef
                    .get().await().toObject<FeedNewsFeed>()?.newsfeed


            feed?.let { NewsFeedClass(user.userName, it) }?.let { usersNewsFeedsList.add(it) }
        }
        return usersNewsFeedsList
    }

    suspend fun getUsersProfileInfo(userId: String): User? {

        return collectionRef.document(userId)
            .collection(Constants.UserProfileInfo).document(Constants.Profile)
            .get().await().toObject(User::class.java)
    }

    fun regUser(userInfo: User) {
        val dummy = Dummy()

        val documentReference = mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
        //setting a dummy field
        documentReference.set(dummy, SetOptions.merge())

        val profileDocumentReference = documentReference.collection(Constants.UserProfileInfo)
            .document(Constants.Profile)
        //setting user profile info
        profileDocumentReference.set(userInfo, SetOptions.merge())


    }


    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }


    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun getUserName(): String? {
        return getCurrentUser()?.userName
    }

    @RequiresApi(Build.VERSION_CODES.R)
    suspend fun getCurrentUser(): User? {
        val snapshot =
            mFireStore.collection(Constants.USERS).document(getCurrentUserId())
                .collection(Constants.UserProfileInfo).document(Constants.Profile).get()
                .addOnSuccessListener {
                }.await()
        return snapshot.toObject(User::class.java)
    }

    fun getUserID(): String {
        return getCurrentUserId()
    }
}
