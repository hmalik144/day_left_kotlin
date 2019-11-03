package com.appttude.h_mal.days_left_kotlin.Login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.auth
import com.appttude.h_mal.days_left_kotlin.Login.FullscreenActivity.Companion.fragmentManagerLogin
import com.appttude.h_mal.days_left_kotlin.MainActivity
import com.appttude.h_mal.days_left_kotlin.R


class SplashFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val handler = Handler()
        handler.postDelayed({
            //check if logged in
            val user = auth.getCurrentUser()

            if (user == null) {

                fragmentManagerLogin
                    .beginTransaction()
                    .replace(R.id.container, LoginFragment())
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .commit()

            } else {
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                activity!!.finish()
            }
        }, 500)
    }
}
