package com.king.nutrynow

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.net.URL
import java.util.ArrayList
import java.util.regex.Pattern


class FragmentFood : Fragment() {

    internal lateinit var progressBar: ProgressBar
    private lateinit var et: EditText
    private lateinit var searchButton: ImageView
    private lateinit var input: String
    internal lateinit var recyclerView: RecyclerView
    internal lateinit var context: Context
    internal lateinit var logtv: TextView

    private val isNetworkConnected: Boolean
        get() {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootview = inflater.inflate(R.layout.fragment_food, container, false)
        progressBar = rootview.findViewById(R.id.progressBar)
        searchButton = rootview.findViewById(R.id.search)
        et = rootview.findViewById(R.id.edittext)
        logtv = rootview.findViewById(R.id.log_tv)
        recyclerView = rootview.findViewById(R.id.recyclerView)
        searchButton.setOnClickListener {
            context = getContext()!!
            input = et.text.toString()
            val u: String = "https://api.nutritionix.com/v1_1/search/" +
                    input + "?results=0:50&fields=item_name,brand_name,item_id," +
                    "nf_calories&appId=2c7d3f6e&appKey=c9ad3f040d91a18ac4979a5a3e544829"
            if (isNetworkConnected) {
                ApiTask().execute(u)
            } else {
                Toast.makeText(getContext(), "Check your Internet Connection",
                        Toast.LENGTH_SHORT).show()
            }
        }
        return rootview
    }

    internal inner class ApiTask : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg urls: String): String {
            return URL(urls[0]).readText()
        }

        override fun onPostExecute(response: String) {
            val response: String = response

            Log.d("response ::::", response)
            progressBar.visibility = View.GONE
            logtv.visibility = View.GONE
            try {
                val jsonObject = JSONObject(response)
                val extract = jsonObject.getString("hits")
                val arr = JSONArray(extract)
                val extract2: StringBuilder = StringBuilder()
                for (i in 0 until arr.length()) {

                    val jsonObject1 = arr.getJSONObject(i)
                    extract2.append(jsonObject1.getString("fields"))

                }
                Log.i("Json :::", extract2.toString())

                recyclerView.layoutManager = LinearLayoutManager(getContext())
                val items = ArrayList<ItemData>()

                val p = Pattern.compile("item_name\":\"(.*?)\",\"brand_name")
                val p1 = Pattern.compile("brand_name\":\"(.*?)\",\"nf_calories")
                val p2 = Pattern.compile("item_id\":\"(.*?)\",\"item_name")
                val m = p.matcher(extract2)
                val m1 = p1.matcher(extract2)
                val m2 = p2.matcher(extract2)
                while (m.find() && m1.find() && m2.find()) {

                    val data = ItemData(m.group(1), m2.group(1), m1.group(1), 0f)
                    items.add(data)
                }

                recyclerView.adapter = RecyclerViewAdapter(context, items)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }
}
