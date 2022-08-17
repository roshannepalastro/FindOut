package com.example.myapplication.activities.user_profile

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.models.User
import com.example.myapplication.network.firebase_connection.FirestoreConnect
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [EditProfile.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProfile : Fragment(R.layout.fragment_edit_profile) {
    lateinit var imageUri: Uri
    lateinit var ivProfileImage: ImageView
    lateinit var storageReference: StorageReference

    @RequiresApi(Build.VERSION_CODES.R)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveBtn: TextView = view.findViewById(R.id.TVSave)
        val etName: EditText = view.findViewById(R.id.ETName)
        val etCollegeName: EditText = view.findViewById(R.id.ETCollegeName)
        val etAddress: EditText = view.findViewById(R.id.ETAddress)
        val etPhone: EditText = view.findViewById(R.id.ETPhone)
        val tvEmail: TextView = view.findViewById(R.id.fep_TVEmail)
        val customiseColor: TextView = view.findViewById(R.id.TVCustomiseColor)
        val ivProfileImage: ImageView = view.findViewById(R.id.IVProfileImage)

        val prevName = etName.text.toString()
        val prevCollegeName = etCollegeName.text.toString()
        val prevAddress = etAddress.text.toString()
        val prevPhone = etPhone.text.toString()
        val db = FirestoreConnect()


        val user = User()


        GlobalScope.launch(Dispatchers.IO) {
            val mUserDef = async { db.getCurrentUser() }
            withContext(Dispatchers.Main) {
                setDataOnEditProfile(
                    user,
                    mUserDef,
                    etName,
                    etAddress,
                    etCollegeName,
                    etPhone,
                    tvEmail
                )
            }
        }


        ivProfileImage.setOnClickListener {
            selectImage()
        }

        saveBtn.setOnClickListener {

            val u = getUserData(
                user,
                etName,
                etCollegeName,
                etAddress,
                etPhone,
                prevAddress,
                prevName,
                prevCollegeName,
                prevPhone
            )
                uploadImage(user)
                db.insertProfileInfo(u, ProfileActivity())
                transactionsToProfile()
        }
    }

    private fun uploadImage(user: User) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Uploading Image")
        progressDialog.setCancelable(false)
        progressDialog.show()


        val formatter = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CANADA)
        val now = Date()
        val date = formatter.format(now)
        storageReference =
            FirebaseStorage
                .getInstance()
                .getReference("users/${user.id}/profileImage/$date")
        storageReference.putFile(imageUri).addOnSuccessListener {
            Toast.makeText(this.activity, "Image Uploaded", Toast.LENGTH_SHORT).show()
            if (progressDialog.isShowing) progressDialog.dismiss()
        }.addOnFailureListener {
            Toast.makeText(this.activity, "Failed to Upload", Toast.LENGTH_SHORT).show()
            Log.d("TAG", "uploadImage: $it")
            if (progressDialog.isShowing) progressDialog.dismiss()
        }
        storageReference.getFile(imageUri)
    }

    private fun getUserData(
        user: User,
        etName: EditText,
        etCollegeName: EditText,
        etAddress: EditText,
        etPhone: EditText,
        prevAddress: String,
        prevName: String,
        prevCollegeName: String,
        prevPhone: String
    ): User {

        val name = etName.text.toString()
        val collegeName = etCollegeName.text.toString()
        val address = etAddress.text.toString()
        val phone = etPhone.text.toString()


        if (name.filter { !it.isWhitespace() } != "" && name != prevName) {
            user.userName = name
        }

        if (collegeName != "" && collegeName != prevCollegeName) {
            user.college = collegeName
        }
        if (address != "" && address != prevAddress) {
            user.address = address
        }
        if (phone != "" && phone != prevPhone) {
            user.phoneNo = phone
        }


        return user
    }

    private suspend fun setDataOnEditProfile(
        user: User,
        mUserDef: Deferred<User?>,
        etName: EditText,
        etAddress: EditText,
        etCollegeName: EditText,
        etPhone: EditText,
        tvEmail: TextView
    ) {

        val mUser = mUserDef.await()
        //for insertion
        if (mUser != null) {
            user.id = mUser.id.toString()

            etName.setText(mUser.userName)
            tvEmail.text = mUser.email

            if (mUser.college == "") {
                etCollegeName.hint = "eg: Oxford University"
            } else {
                etCollegeName.setText(mUser.college)
            }

            if (mUser.address == "") {
                etAddress.hint = "Address"
            } else {
                etAddress.setText(mUser.address)
            }



            if (mUser.phoneNo == "") {
                etPhone.hint = "+977-98xxxxxxxx"
            } else {
                etPhone.setText(mUser.phoneNo)
            }


        }

    }

    private fun transactionsToProfile() {
        val profileFragment = ProfileFragment()
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.FLFrameLayout, profileFragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    private fun selectImage() {

        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Please Select"), 100)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageUri = data?.data!!
            ivProfileImage = view?.findViewById(R.id.IVProfileImage)!!
            ivProfileImage.setImageURI(imageUri)
        }
    }
}
