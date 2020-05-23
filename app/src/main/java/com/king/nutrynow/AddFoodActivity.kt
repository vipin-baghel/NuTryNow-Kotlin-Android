package com.king.nutrynow

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_food.*
import kotlinx.android.synthetic.main.fragment_food.*

import java.net.URL
import java.util.ArrayList
import java.util.HashMap

class AddFoodActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    internal var uid: String? = null
    internal var consumedAt:String = "Breakfast"
    internal var db = FirebaseFirestore.getInstance()
    internal val tag = "Tag"
    internal var servingInput: Float = 0.toFloat()
    internal var servingDefault: Float = 0.toFloat()
    internal var calConsumed: Float = 0.toFloat()
    internal var proteinConsumed: Float = 0.toFloat()
    internal var fatConsumed: Float = 0.toFloat()
    internal var carbsConsumed: Float = 0.toFloat()
    private lateinit var itemName:String
    private lateinit var itemBrand:String
    private lateinit var itemId:String

    private val isNetworkConnected: Boolean
        get() {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)
        //Hide the action bar
        val actionBar = supportActionBar
        actionBar?.hide()

        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        itemName = intent.getStringExtra("item_name")
        itemBrand = intent.getStringExtra("item_brand")
        itemId = intent.getStringExtra("item_id")
        uid = sharedPreferences.getString("uid", "")
        /*val itemName = intent.getStringExtra("item_name")
        val itemBrand = intent.getStringExtra("item_brand")
        val itemId = intent.getStringExtra("item_id")*/
        servingDefault = intent.getFloatExtra("serving", 0f)
        val spinner = findViewById<Spinner>(R.id.spinner3)

        tv_itemName.text = itemName
        tv_itemBrand.text = itemBrand
        et_serving.setText(servingDefault.toString())

        // Spinner click listener
        spinner.onItemSelectedListener = this

        // Spinner Drop down elements
        val categories = ArrayList<String>()
        categories.add("Breakfast")
        categories.add("Lunch")
        categories.add("Snacks")
        categories.add("Dinner")

        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(
                this, android.R.layout.simple_spinner_item, categories)
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // attaching data adapter to spinner
        spinner.adapter = dataAdapter

        val u = "https://api.nutritionix.com/v1_1/item?id=" + itemId +
                "&appId=2c7d3f6e&appKey=1df3d4f8744c14326890d336dccab128"

        log_btn.setOnClickListener(View.OnClickListener {
            try {
                servingInput = java.lang.Float.parseFloat(et_serving.text.toString())
                if (servingInput == 0f || servingInput > 900 || servingInput < 0) {
                    et_serving.error = "Invalid amount"
                    et_serving.requestFocus()
                    return@OnClickListener
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                et_serving.error = "Please enter amount"
                et_serving.requestFocus()
                return@OnClickListener
            }

            if (isNetworkConnected) {
                ApiTask4().execute(u)
                log_btn.isEnabled = false
            } else {
                Toast.makeText(applicationContext,
                        "Check your Internet Connection",
                        Toast.LENGTH_SHORT).show()
            }
        })

    }

    //Performing action onItemSelected and onNothing selected
    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

        // On selecting a spinner item
        consumedAt = parent.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    internal inner class ApiTask4 : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar4!!.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg urls: String): String {
            return URL(urls[0]).readText()
        }

        override fun onPostExecute(response: String) {
            val response = response

            Log.i("Item response", response)

            val arr = response.split(",".toRegex()).dropLastWhile{ it.isEmpty() }.toTypedArray()
            for (i in arr.indices) {
                arr[i] = arr[i].replace("\"".toRegex(), "")
                arr[i] = arr[i].replace("_".toRegex(), " ")
                arr[i] = arr[i].replace("null".toRegex(), "0")
                if (arr[i].contains("nf")) {
                    arr[i] = arr[i].replace("nf".toRegex(), "")
                }
                if (arr[i].contains("calories:")) {
                    val numberOnly = arr[i].replace("[^.0-9]".toRegex(), "")
                    val number: Float
                    if (numberOnly != "") {
                        number = java.lang.Float.parseFloat(numberOnly)
                        calConsumed = number / servingDefault * servingInput
                    }

                } else if (arr[i].contains("protein")) {
                    val numberOnly = arr[i].replace("[^.0-9]".toRegex(), "")
                    val number: Float
                    if (numberOnly != "") {
                        number = java.lang.Float.parseFloat(numberOnly)
                        proteinConsumed = number / servingDefault * servingInput
                    }

                } else if (arr[i].contains("carb")) {
                    val numberOnly = arr[i].replace("[^.0-9]".toRegex(), "")
                    val number: Float
                    if (numberOnly != "") {
                        number = java.lang.Float.parseFloat(numberOnly)
                        carbsConsumed = number / servingDefault * servingInput
                    }

                } else if (arr[i].contains("total fat")) {
                    val numberOnly = arr[i].replace("[^.0-9]".toRegex(), "")
                    val number: Float
                    if (numberOnly != "") {
                        number = java.lang.Float.parseFloat(numberOnly)
                        fatConsumed = number / servingDefault * servingInput
                    }
                }
            }

            Log.d("fat consumed", fatConsumed.toString())
            Log.d("protein consumed", proteinConsumed.toString())
            Log.d("carbs consumed", carbsConsumed.toString())
            Log.d("calories consumed", calConsumed.toString())

            val food = HashMap<String, Any>()
            food["name"] = itemName
            food["brand"] = itemBrand
            food["id"] = itemId
            food["serving"] = servingInput
            food["when"] = consumedAt
            food["calConsumed"] = calConsumed
            food["carbsConsumed"] = carbsConsumed
            food["proteinConsumed"] = proteinConsumed
            food["fats_consumed"] = fatConsumed

            // Add food to correct user (watch uid) in database
            db.collection("users").document(uid!!)
                    .collection("ItemData")
                    .add(food)
                    .addOnSuccessListener {
                        Log.d(tag, "Food Added")
                        Toast.makeText(applicationContext, "Food Added", Toast.LENGTH_SHORT).show()
                        progressBar4.visibility = View.GONE
                        log_btn.isEnabled = true
                    }
                    .addOnFailureListener { e ->
                        Log.w(tag, "Error adding food", e)
                        Toast.makeText(applicationContext, "Error adding food", Toast.LENGTH_SHORT).show()
                        progressBar4.visibility = View.GONE
                        log_btn.isEnabled = true
                    }
        }
    }
}
