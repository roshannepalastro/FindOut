package com.example.myapplication.authentication

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentRegisterBinding
import com.example.myapplication.models.User
import com.example.myapplication.network.firebase_connection.FirestoreConnect
import com.example.myapplication.newsfeed.newsfeed_activities.MainActivity
import com.example.myapplication.utils.Utils
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val mAuth = FirebaseAuth.getInstance()

        binding.TVLoginINRegister.setOnClickListener {
            Log.d("test", "button presses")
            transactionToLoginFragment()
        }

        binding.BTRegister.setOnClickListener {
            register(binding,mAuth)
        }
        // Inflate the layout for this fragment
    }

    private fun transactionToLoginFragment() {
        val loginFragment = LoginFragment()
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.FLAuth, loginFragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    private fun register(binding: FragmentRegisterBinding, mAuth: FirebaseAuth) {
        val progressDialog = ProgressDialog(this.context)
        progressDialog.setMessage("Registering...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val username = binding.ETUsername.text.toString()
        val email = binding.ETEmail.text.toString()
        val password = binding.ETPassword.text.toString()

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            if (progressDialog.isShowing) progressDialog.dismiss()
            this.context?.let { Utils.toastIt("Please entered all fields", it) }
        } else if (password.length <= 5) {
            if (progressDialog.isShowing) progressDialog.dismiss()
            this.context?.let {
                Utils.toastIt("Make sure password is longer than 6 characters",
                    it
                )
            }
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (progressDialog.isShowing) progressDialog.dismiss()
                        registerUser(task,username,email)

                    } else {
                        if (progressDialog.isShowing) progressDialog.dismiss()
                        this.context?.let { Utils.toastIt("Failed", it) }
                    }

                }
        }
    }

    private fun registerUser(task: Task<AuthResult>, username: String, email: String) {
        val firebaseConnect = FirestoreConnect()
        val firebaseUser: FirebaseUser = task.result!!.user!!
        val user = User(
            firebaseUser.uid,
            username,
            email,
        )
        firebaseConnect.regUser(user)
        startActivity(Intent(this.context, MainActivity::class.java))

    }

}