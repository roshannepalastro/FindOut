package com.example.myapplication.activities.user_profile

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentProfileCardHostBinding
import com.example.myapplication.databinding.FragmentProfileCollapseBinding
import kotlinx.coroutines.DelicateCoroutinesApi


class ProfileCardHostFragment : Fragment() {

    private var _binding: FragmentProfileCardHostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileCardHostBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // val adapter = ProfileNewsfeedAdapter()
    }


}