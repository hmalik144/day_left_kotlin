package com.appttude.h_mal.days_left_kotlin.Login

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.auth
import com.appttude.h_mal.days_left_kotlin.R
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class FullscreenActivity : AppCompatActivity() {
    companion object {
        lateinit var fragmentManagerLogin : FragmentManager

        fun isPasswordValid(password: String, context:Context): Boolean {
            var validityScore = 0
            var str = ""

            if (password.length > 6){
                validityScore = validityScore + 1
            }else{
                str = str + "more than 6 characters, "
            }

            if (password.toLowerCase() == password){
                validityScore = validityScore + 1
            }else{
                str = str + "uppercase character, "
            }

            if (Pattern.compile( "[0-9]" ).matcher( password ).find()){
                validityScore = validityScore + 1
            }else{
                str = str + "number"
            }

            if(validityScore == 3){
                return true
            }else{
                Toast.makeText(context,"Password Requires : $str", Toast.LENGTH_SHORT).show()
                return false
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)

        auth = FirebaseAuth.getInstance()

        fragmentManagerLogin = supportFragmentManager

        fragmentManagerLogin.beginTransaction().replace(
            R.id.container,
            SplashFragment()
        ).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (fragmentManagerLogin.fragments.size > 1) {
            fragmentManagerLogin.popBackStack()
        }
    }


}
