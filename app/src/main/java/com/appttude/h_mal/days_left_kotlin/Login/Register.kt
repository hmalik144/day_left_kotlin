package com.appttude.h_mal.days_left_kotlin.Login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.auth
import com.appttude.h_mal.days_left_kotlin.Login.FullscreenActivity.Companion.isPasswordValid
import com.appttude.h_mal.days_left_kotlin.MainActivity
import com.appttude.h_mal.days_left_kotlin.R
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*
import java.util.regex.Pattern


class Register : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        email_sign_up.setOnClickListener{
            var cancel = false
            var focusView: View? = null

            if (TextUtils.isEmpty(name_register.text)) {
                name_register.setError(getString(R.string.error_field_required))
                focusView = name_register
                cancel = true
            }

            if (TextUtils.isEmpty(email_register.text)) {
                email_register.setError(getString(R.string.error_field_required))
                focusView = email
                cancel = true
            }

            if (TextUtils.isEmpty(password_top.text)) {
                password_top.setError(getString(R.string.error_field_required))
                focusView = password_top
                cancel = true
            }

            if (TextUtils.isEmpty(password_bottom.text)) {
                password_bottom.setError(getString(R.string.error_field_required))
                focusView = password_bottom
                cancel = true
            }

            if (!TextUtils.isEmpty(password_top.text) && !isPasswordValid(password_top.text.toString(), this.context!!)) {
                password_top.setError(getString(R.string.error_invalid_password))
                focusView = password_top
                cancel = true
            }

            if (password_top.text != password_bottom.text) {
                password_bottom.setError(getString(R.string.no_match_password))
                focusView = password_bottom
                cancel = true
            }

            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView!!.requestFocus()
            } else {
                pb.visibility = View.VISIBLE
                //create user
                auth.createUserWithEmailAndPassword(email.text.toString(), password_top.text.toString())
                    .addOnCompleteListener{task ->
                        pb.visibility = View.GONE
                            if (!task.isSuccessful) {
                                Toast.makeText(context, "Authentication failed." + task.exception!!,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val intent = Intent(context, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                                activity?.finish()
                            }
                    }

            }

        }
    }

}
