package com.appttude.h_mal.days_left_kotlin.Login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.appttude.h_mal.days_left_kotlin.Login.FullscreenActivity.Companion.fragmentManagerLogin
import com.appttude.h_mal.days_left_kotlin.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_forgot_password.*


class ForgotPassword : Fragment() {

    internal var TAG = "forgotPasswordFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        reset_pw_sign_up.setOnClickListener{
            resetPassword(reset_pw.text.toString().trim())
        }
    }

    private fun resetPassword(emailAddress: String) {
        val auth = FirebaseAuth.getInstance()

        auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")

                    fragmentManagerLogin.popBackStack()
                } else {
                    Toast.makeText(context, "Could not reset Password", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
