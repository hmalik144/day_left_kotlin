package com.appttude.h_mal.days_left_kotlin.Login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.auth
import com.appttude.h_mal.days_left_kotlin.Login.FullscreenActivity.Companion.fragmentManagerLogin
import com.appttude.h_mal.days_left_kotlin.Login.FullscreenActivity.Companion.isPasswordValid
import com.appttude.h_mal.days_left_kotlin.MainActivity
import com.appttude.h_mal.days_left_kotlin.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       return inflater.inflate(R.layout.fragment_login, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        password.setOnEditorActionListener(TextView.OnEditorActionListener { textView, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        forgot.setOnClickListener{
            fragmentManagerLogin.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_in)
                .replace(R.id.container, ForgotPassword())
                .addToBackStack("forgot_pw").commit()
        }

        register_button.setOnClickListener {
            fragmentManagerLogin.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_in)
                .replace(R.id.container, Register())
                .addToBackStack("register")
                .commit()
        }

        email_sign_in_button.setOnClickListener{
            attemptLogin()
        }
    }

    private fun attemptLogin() {

        // Reset errors.
        email.setError(null)
        password.setError(null)

        // Store values at the time of the login attempt.
        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
//        if (!TextUtils.isEmpty(password.text)) {
//            password.setError(getString(R.string.error_invalid_password))
//            focusView = password
//            cancel = true
//        }

        // Check for a valid email address.
//        if (!isPasswordValid(email.text.toString(), context!!)) {
//            email.setError(getString(R.string.error_field_required))
//            focusView = email
//            cancel = true
//        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView!!.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            login_progress.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener{ task ->
                    login_progress.visibility = View.GONE
                    if (task.isSuccessful) {
                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    } else {
                        Toast.makeText(context, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

}
