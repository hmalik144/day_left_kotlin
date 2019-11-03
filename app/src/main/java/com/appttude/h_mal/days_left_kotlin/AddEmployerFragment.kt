package com.appttude.h_mal.days_left_kotlin

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.appttude.h_mal.days_left_kotlin.Objects.AbnObject
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.fragment_add_employer.*
import org.json.JSONArray
import java.util.HashMap
import androidx.core.content.ContextCompat.getSystemService



class AddEmployerFragment : Fragment() {
    val TAG = "AddEmployer"

    lateinit var searchView: SearchView
    lateinit var mFunctions: FirebaseFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFunctions = FirebaseFunctions.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_add_employer, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.app_bar_search)

        searchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.getWindowToken(), 0)

                query?.let {
                    abnLookUp(it).addOnCompleteListener{ task ->
                        spinning_pb.visibility = View.GONE
                        if (task.isSuccessful){
                            val jsonArray = task.result
                            var abnList = ArrayList<AbnObject>()

                            jsonArray?.let {
                                for (i in 0 until it.size){

                                    val item = hashIntoObj(it.get(i) as Map<String,Any>)
//                                    val abnobj = item.values as AbnObject
                                    abnList.add(item)
                                }
                            }

                            if (abnList.size > 0){
                                list_view.adapter = AbnListAdapter(context!!, abnList)
                                empty_list.visibility = View.GONE
                            }else{
                                empty_list.visibility = View.VISIBLE
                            }

                        }else{
                            val e = task.exception
                            if (e is FirebaseFunctionsException) {
                                val ffe = e as FirebaseFunctionsException?
                                val code = ffe!!.code
                                val details = ffe.details as String

                                Log.e(code.toString(),details)
                            }

                            Log.w(TAG, "addMessage:onFailure", e)
                            Toast.makeText(context, "An error occurred.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun abnLookUp(input : String) : Task<ArrayList<*>> {
        spinning_pb.visibility = View.VISIBLE
        // Create the arguments to the callable function.
        val data = HashMap<String, Any>()
        data["input"] = input
        data["push"] = true

        return mFunctions
            .getHttpsCallable("abnLooKUp")
            .call(data)
            .continueWith{ task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then getResult() will throw an Exception which will be
                // propagated down.
                val result = task.result?.data as ArrayList<*>

                Log.i(TAG, "then: " + result.toString())

                result
            }
    }

    fun hashIntoObj(map: Map<String,Any>): AbnObject {
        val abn = map.getValue("abn") as String
        val companyName = map.getValue("companyName") as String
        val postCode = map.getValue("postCode") as Int
        val state = map.getValue("state") as String

        return AbnObject(abn, companyName, postCode, state)
    }
}
