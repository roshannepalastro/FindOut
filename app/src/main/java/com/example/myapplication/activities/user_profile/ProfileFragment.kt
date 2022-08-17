package com.example.myapplication.activities.user_profile

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLoginBinding
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.models.User
import com.example.myapplication.network.firebase_connection.FirestoreConnect
import kotlinx.coroutines.*


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ProfileNewsfeedAdapter()
        val profileRecyclerView: RecyclerView = view.findViewById(R.id.RVProfile)
        profileRecyclerView.layoutManager = LinearLayoutManager(view.context)
        profileRecyclerView.adapter = adapter

        GlobalScope.launch(Dispatchers.IO) {
            val db = FirestoreConnect()
            val user = async { db.getCurrentUser() }
            val list = async { user.await()?.let { db.getUsersNewsfeed(it) } }
            withContext(Dispatchers.Main) {

                profileData(user,binding.TVAddress,binding.TVEmail,binding.TVName,binding.TVCollegeName,binding.TVPhone)
                list.await()?.let { adapter.updateList(it) }

            }

        }

        binding.TVEdit.setOnClickListener {
            transitionToEditProfile()
        }

    }

    private fun transitionToEditProfile() {
        val editProfile = EditProfile()
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.FLFrameLayout, editProfile)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    private suspend fun profileData(
        user: Deferred<User?>,
        tvAddress: TextView,
        tvEmail: TextView,
        tvName: TextView,
        tvCollegeName: TextView,
        tvPhone: TextView
    ) {
        val mUser = user.await()
        tvName.text = mUser?.userName
        tvEmail.text = mUser?.email

        tvEmail.background=null
        tvAddress.background=null
        tvName.background=null
        tvPhone.background=null
        tvCollegeName.background=null

        if (mUser?.college == "") {
            tvCollegeName.hint = "eg: Oxford University"


        } else {
            tvCollegeName.text = mUser?.college
        }
        if (mUser?.address == "") {
            tvAddress.hint = "Address"
        } else {
            tvAddress.text = mUser?.address
        }

        if (mUser?.phoneNo == "") {
            tvPhone.hint = "+977-98xxxxxxxx"
        } else {
            tvPhone.text = mUser?.phoneNo
        }


    }

}

