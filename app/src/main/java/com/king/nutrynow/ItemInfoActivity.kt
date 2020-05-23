package com.king.nutrynow

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import java.net.URL

class ItemInfoActivity : AppCompatActivity() {

    private lateinit var tv1: TextView
    private lateinit var tv2: TextView
    internal lateinit var tv3: TextView
    internal lateinit var progressBar: ProgressBar
    private lateinit var itemId: String
    private lateinit var itemName: String
    private lateinit var itemBrand: String
    internal lateinit var imageView: ImageView
    private lateinit var applyButton: Button
    private lateinit var editText: EditText
    internal var servingInput: Float = 0.toFloat()
    internal var servingDefault = 0f
    internal var calConsumed: Float = 0.toFloat()
    internal var proteinConsumed: Float = 0.toFloat()
    internal var fatConsumed: Float = 0.toFloat()
    internal var carbsConsumed: Float = 0.toFloat()

    private val isNetworkConnected: Boolean
        get() {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_info)

        //Hide the action bar
        val actionBar = supportActionBar
        actionBar?.hide()
        //Hide keyboard from popping up
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        tv1 = findViewById(R.id.textView4)
        tv2 = findViewById(R.id.textView5)
        tv3 = findViewById(R.id.textView6)
        progressBar = findViewById(R.id.progressBar2)
        imageView = findViewById(R.id.addfood)
        applyButton = findViewById(R.id.apply_btn)
        editText = findViewById(R.id.edittext_serving)


        itemId = intent.getStringExtra("item_id")
        itemName = intent.getStringExtra("item_name")
        itemBrand = intent.getStringExtra("item_brand")
        val serving = intent.getFloatExtra("serving", 100f)
        tv1.text = itemName
        tv2.text = "Brand: $itemBrand"
        tv3.text = ""
        editText.setText(serving.toString())
        imageView.setOnClickListener {
            if (servingDefault != 0f) {
                val intent = Intent(this@ItemInfoActivity, AddFoodActivity::class.java)
                intent.putExtra("item_id", itemId)
                intent.putExtra("item_name", itemName)
                intent.putExtra("item_brand", itemBrand)
                intent.putExtra("serving", servingDefault)
                startActivity(intent)
            }
        }

        val u = "https://api.nutritionix.com/v1_1/item?id=" + itemId +
                "&appId=2c7d3f6e&appKey=1df3d4f8744c14326890d336dccab128"

        if (isNetworkConnected) {
            ApiTask2().execute(u)
        } else {
            Toast.makeText(this@ItemInfoActivity,
                    "Check your Internet Connection",
                    Toast.LENGTH_SHORT).show()
        }
        applyButton.setOnClickListener {
            servingInput = java.lang.Float.parseFloat(editText.text.toString())
            if (isNetworkConnected) {
                ApiTask3().execute(u)
            } else {
                Toast.makeText(this@ItemInfoActivity,
                        "Check your Internet Connection",
                        Toast.LENGTH_SHORT).show()
            }
        }

    }

    internal inner class ApiTask2 : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg urls: String): String {
            return URL(urls[0]).readText()

        }

        override fun onPostExecute(response: String) {
            val response = response
            Log.i("Item response", response)
            progressBar.visibility = View.GONE
            val arr = response.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            for (i in arr.indices) {
                arr[i] = arr[i].replace("\"".toRegex(), "")
                arr[i] = arr[i].replace("_".toRegex(), " ")
                arr[i] = arr[i].replace("null".toRegex(), "0")
                if (arr[i].contains("nf")) {
                    arr[i] = arr[i].replace("nf".toRegex(), "")
                }
                if (arr[i].contains("serving weight gram")) {
                    tv3.append(arr[i])
                    tv3.append("\n\n")
                    val numberOnly = arr[i].replace("[^.0-9]".toRegex(), "")
                    Log.d("Serving Default : ",numberOnly)
                    if (numberOnly == "0" || numberOnly == "") {
                        imageView.isEnabled = false
                        Toast.makeText(applicationContext,
                                "Cannot add this food due to incomplete information",
                                Toast.LENGTH_SHORT).show()
                    } else {
                        servingDefault = java.lang.Float.parseFloat(numberOnly)
                    }

                }
                when {
                    arr[i].contains("calories") -> {
                        tv3.append(arr[i])
                        tv3.append(" kcal")
                        tv3.append("\n\n")
                    }
                    arr[i].contains("fat") -> {
                        tv3.append(arr[i])
                        tv3.append(" g")
                        tv3.append("\n\n")
                    }
                    arr[i].contains("cholesterol") -> {
                        tv3.append(arr[i])
                        tv3.append(" mg/dL")
                        tv3.append("\n\n")
                    }
                    arr[i].contains("sodium") -> {
                        tv3.append(arr[i])
                        tv3.append(" mg")
                        tv3.append("\n\n")
                    }
                    arr[i].contains("carb") -> {
                        tv3.append(arr[i])
                        tv3.append(" g")
                        tv3.append("\n\n")
                    }
                    arr[i].contains("fiber") -> {
                        tv3.append(arr[i])
                        tv3.append(" g")
                        tv3.append("\n\n")
                    }
                    arr[i].contains("protein") -> {
                        tv3.append(arr[i])
                        tv3.append(" g")
                        tv3.append("\n\n")
                    }
                    arr[i].contains("vitamin") -> {
                        tv3.append(arr[i])
                        tv3.append(" µg")
                        tv3.append("\n\n")
                    }
                    arr[i].contains("calcium") -> {
                        tv3.append(arr[i])
                        tv3.append(" mg")
                        tv3.append("\n\n")
                    }
                    arr[i].contains("iron") -> {
                        tv3.append(arr[i])
                        tv3.append(" mg")
                        tv3.append("\n\n")
                    }
                    arr[i].contains("potassium") -> {
                        tv3.append(arr[i])
                        tv3.append(" mg")
                        tv3.append("\n\n")
                    }
                }
            }
        }
    }

    internal inner class ApiTask3 : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
            tv3.text = ""
        }

        override fun doInBackground(vararg urls: String): String {
            return URL(urls[0]).readText()
        }

        override fun onPostExecute(response: String) {
            val response = response
            Log.i("Item response", response)
            progressBar.visibility = View.GONE
            val arr = response.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in arr.indices) {
                arr[i] = arr[i].replace("\"".toRegex(), "")
                arr[i] = arr[i].replace("_".toRegex(), " ")
                arr[i] = arr[i].replace("null".toRegex(), "0")
                if (arr[i].contains("nf")) {
                    arr[i] = arr[i].replace("nf".toRegex(), "")
                }
                when {
                    arr[i].contains("calories:") -> {
                        val numberOnly = arr[i].replace("[^.0-9]".toRegex(), "")
                        val number: Float
                        Log.d("Calorie value : ", numberOnly)
                        if (numberOnly != "") {
                            number = java.lang.Float.parseFloat(numberOnly)
                            calConsumed = (number / servingDefault) * servingInput
                        }
                        tv3.append("Calories: $calConsumed")
                        tv3.append(" kcal")
                        tv3.append("\n\n")
                    }
                    arr[i].contains("protein") -> {
                        val numberOnly = arr[i].replace("[^.0-9]".toRegex(), "")
                        val number: Float
                        if (numberOnly != "") {
                            number = java.lang.Float.parseFloat(numberOnly)
                            proteinConsumed = (number / servingDefault) * servingInput
                        }
                        tv3.append("Protein: $proteinConsumed")
                        tv3.append(" g")
                        tv3.append("\n\n")
                    }
                    arr[i].contains("carb") -> {
                        val numberOnly = arr[i].replace("[^.0-9]".toRegex(), "")
                        val number: Float
                        if (numberOnly != "") {
                            number = java.lang.Float.parseFloat(numberOnly)
                            carbsConsumed = (number / servingDefault) * servingInput
                        }
                        tv3.append("Carbs: $carbsConsumed")
                        tv3.append(" g")
                        tv3.append("\n\n")
                    }
                    arr[i].contains("total fat") -> {
                        val numberOnly = arr[i].replace("[^.0-9]".toRegex(), "")
                        val number: Float
                        if (numberOnly != "") {
                            number = java.lang.Float.parseFloat(numberOnly)
                            fatConsumed = (number / servingDefault) * servingInput
                        }
                        tv3.append("Fats: $fatConsumed")
                        tv3.append(" g")
                        tv3.append("\n\n")
                    }
                }
            }
            Log.d("fat consumed", fatConsumed.toString())
            Log.d("protein consumed", proteinConsumed.toString())
            Log.d("carbs consumed", carbsConsumed.toString())
            Log.d("calories consumed", calConsumed.toString())
        }
    }
}

