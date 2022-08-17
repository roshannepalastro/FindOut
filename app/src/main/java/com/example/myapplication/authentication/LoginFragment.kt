package com.example.myapplication.authentication

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLoginBinding
import com.example.myapplication.databinding.FragmentRegisterBinding
import com.example.myapplication.newsfeed.newsfeed_activities.MainActivity
import com.example.myapplication.utils.Utils
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.TVRegisterInLogin.setOnClickListener {
            transactionToRegisterActivity()
        }
        binding.BTLogin.setOnClickListener {
            login()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun transactionToRegisterActivity() {
        val registerFragment = RegisterFragment()
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.FLAuth, registerFragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    private fun login() {
        val progressDialog = ProgressDialog(this.context)
        progressDialog.setMessage("Logging in...")
        progressDialog.setCancelable(false)
        progressDialog.show()
//            val email = binding.ETEmail.text.toString()
//            val password =  binding.ETPassword.text.toString()
        val email = "roshan0011@gmail.com"
        val password = "roshan0011"


        if (email.isEmpty() || password.isEmpty()) {
            if (progressDialog.isShowing) progressDialog.dismiss()
            this.context?.let { Utils.toastIt("Please entered  all fields", it) }
        } else {
            try {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (progressDialog.isShowing) progressDialog.dismiss()
                            this.context?.let { Utils.toastIt("Logged in Successfully", it) }
                            val intent = Intent(this.context, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            if (progressDialog.isShowing) progressDialog.dismiss()
                            this.context?.let { Utils.toastIt("Logging in failed", it) }
                        }

                    }
            } catch (exception: Exception) {
                this.context?.let { it1 ->
                    Utils.toastIt("Please try again", it1)
                }
            }

        }
    }


}