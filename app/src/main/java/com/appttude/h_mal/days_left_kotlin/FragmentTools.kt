package com.appttude.h_mal.days_left_kotlin

import android.Manifest
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.FileProvider
import com.appttude.h_mal.days_left_kotlin.FirebaseClass.Companion.auth
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_tools.*
import java.io.File
import java.net.URL
import java.util.HashMap

class FragmentTools : Fragment() {

    private val TAG = "FragmentTools"

    val mFunctions = FirebaseFunctions.getInstance()
    val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tools, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        compile.setOnClickListener(onClickListener)

        summary_button.setOnClickListener(onClickListener)
    }

    internal val onClickListener = View.OnClickListener { view ->
        requestPermissions().let {
            if (it) {
                if (view.id == R.id.compile){
                    writeToExcel().addOnCompleteListener(complete)
                }else if(view.id == R.id.summary_button){
                    writeToExcelVisa().addOnCompleteListener(complete)
                }
            }
        }

    }

    internal val complete = OnCompleteListener<String>{task ->
        if (!task.isSuccessful) {
            val e = task.exception
            if (e is FirebaseFunctionsException) {
                val ffe = e as FirebaseFunctionsException?
                val code = ffe!!.code
                val details = ffe.details
            }

            Log.w(TAG, "addMessage:onFailure", e)
            Toast.makeText(context, "An error occurred.", Toast.LENGTH_SHORT).show()

        }else{
            // [START_EXCLUDE]
            val result = task.result as String
            Log.i(TAG, "onComplete: $result")

            val strings = result.split("/").toTypedArray()

            val fbstore = storage.reference.child(result)

            val savePath = Environment.getExternalStorageDirectory().toString() + "/DaysLeftTemp"
            val file = File(savePath)
            if (!file.exists()) {
                file.mkdirs()
            }

            val myFile = File(savePath, strings.last())

            fbstore.getFile(myFile).addOnSuccessListener {
                // Local temp file has been created
                val data =
                    FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".provider", myFile)
                activity?.grantUriPermission(
                    activity?.getPackageName(),
                    data,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                val intent1 = Intent(Intent.ACTION_VIEW)
                    .setDataAndType(data, "application/vnd.ms-excel")
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                try {
                    activity?.startActivity(intent1)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(activity, "No Application Available to View Excel", Toast.LENGTH_SHORT)
                        .show()
                }


            }.addOnFailureListener {
                // Handle any errors

            }
        }
    }

    fun writeToExcel(): Task<String> {
        // Create the arguments to the callable function.
        val data = HashMap<String, Any>()
        data["push"] = true

        return mFunctions
            .getHttpsCallable("writeFireToExcel")
            .call(data)
            .continueWith{ task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then getResult() will throw an Exception which will be
                // propagated down.

                val result = task.result?.data as String

                Log.i(TAG, "then: " + result)

                result
            }
    }

    fun writeToExcelVisa(): Task<String> {
        // Create the arguments to the callable function.
        val data = HashMap<String, Any>()
        data["push"] = true

        return mFunctions
            .getHttpsCallable("writeFireToExcelVisa")
            .call(data)
            .continueWith{ task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then getResult() will throw an Exception which will be
                // propagated down.
                //todo: change to file
                val result = task.result?.data as String

                Log.i(TAG, "then: " + result)

                result
            }
    }

    fun requestPermissions() : Boolean{
        if (checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context,"not granted",Toast.LENGTH_SHORT).show()

            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                443)

            return false
        }else{

            return true
        }


    }
}
