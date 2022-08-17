package com.example.myapplication.authentication

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.myapplication.R
//Todo this class in not used
class ConfirmationDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage("Are you sure wanna register")
            .setPositiveButton(getString(R.string.yes)) { _, _ -> }
            .create()

    companion object {
        const val TAG = "ConfirmationDialog"
    }
}