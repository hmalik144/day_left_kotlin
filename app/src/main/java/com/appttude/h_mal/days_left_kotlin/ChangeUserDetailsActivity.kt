package com.appttude.h_mal.days_left_kotlin

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.auth
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_change_user_details.*

class ChangeUserDetailsActivity : AppCompatActivity() {

    private val TAG = "ChangeDetailsActivity"

    var user = auth.currentUser

    private val EMAIL_CONSTANT = "Email Address"
    private val PW_CONSTANT = "Password"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_user_details)

        change_profile_name.setOnClickListener{
            val dialog = AlertDialog.Builder(this@ChangeUserDetailsActivity)
            dialog.setTitle("Update Username")

            val titleBox = EditText(this@ChangeUserDetailsActivity)
            titleBox.setText(user?.getDisplayName())
            dialog.setView(titleBox)
            dialog.setPositiveButton(
                "Update"
            ) { dialog, which ->
                updateProfile(titleBox.text.toString().trim { it <= ' ' })
            }
            dialog.create().show()
        }

        change_email.setOnClickListener{
            showDialog(EMAIL_CONSTANT)
        }

        change_pw.setOnClickListener{
            showDialog(PW_CONSTANT)
        }

    }

    private fun updateProfile(profileName: String) {
        val profileUpdatesBuilder = UserProfileChangeRequest.Builder()

        if (!TextUtils.isEmpty(profileName)) {
            profileUpdatesBuilder.setDisplayName(profileName)
        }

        val profileUpdates = profileUpdatesBuilder.build()

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "User profile updated.")
                    //                            viewController.reloadDrawer();
                }
            }
            ?.addOnFailureListener {
                Toast.makeText(this@ChangeUserDetailsActivity, "Update Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun changeCredentials(email: String, password: String, changeText: String, selector: String) {
        //todo: change to function

        // Get auth credentials from the user for re-authentication
        val credential = EmailAuthProvider
            .getCredential(email, password) // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials
        user?.reauthenticate(credential)
            ?.addOnCompleteListener {
                Log.d(TAG, "User re-authenticated.")

                user = FirebaseAuth.getInstance().currentUser
                if (selector == EMAIL_CONSTANT) {

                    user?.updateEmail(changeText)!!
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User email address updated.")
                                Toast.makeText(
                                    this@ChangeUserDetailsActivity,
                                    "Email Update Successful",
                                    Toast.LENGTH_SHORT
                                ).show()

                                //                                                viewController.reloadDrawer();
                            } else {
                                Toast.makeText(
                                    this@ChangeUserDetailsActivity,
                                    "Email Update Unsuccessful",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
                if (selector == PW_CONSTANT) {
                    user?.updatePassword(changeText)!!
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User email address updated.")
                                Toast.makeText(
                                    this@ChangeUserDetailsActivity,
                                    "Password Update Successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@ChangeUserDetailsActivity,
                                    "Password Update Unsuccessful",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
    }

    private fun showDialog(update: String) {
        //Make new Dialog
        val dialog = AlertDialog.Builder(this@ChangeUserDetailsActivity)
        dialog.setTitle("Update $update")

        val layout = LinearLayout(this@ChangeUserDetailsActivity)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(28, 0, 56, 0)

        val box1 = EditText(this@ChangeUserDetailsActivity)
        box1.hint = "Current Email Address"
        box1.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        layout.addView(box1) // Notice this is an add method

        val box2 = EditText(this@ChangeUserDetailsActivity)
        box2.hint = "Current Password"
        box2.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        layout.addView(box2) // Another add method

        val box3 = EditText(this@ChangeUserDetailsActivity)
        if (update == PW_CONSTANT) {
            box3.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            box3.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
        box3.hint = "New $update"
        layout.addView(box3) // Another add method

        dialog.setView(layout)
        dialog.setPositiveButton("Update") { dialog, which ->
            val email = box1.text.toString().trim { it <= ' ' }
            val password = box2.text.toString().trim { it <= ' ' }
            val textThree = box3.text.toString().trim { it <= ' ' }

            changeCredentials(email, password, textThree, update)
        }
        dialog.create().show()
    }
}
