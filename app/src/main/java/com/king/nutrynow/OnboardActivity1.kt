package com.king.nutrynow

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.RadioButton
import kotlinx.android.synthetic.main.activity_onboard1.*


class OnboardActivity1 : AppCompatActivity() {

    internal var height: Float = 0.toFloat()
    private var weight: Float = 0.toFloat()
    internal var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard1)


        //Hide the action bar
        val actionBar = supportActionBar
        actionBar?.hide()

        val sharedPreferences:SharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
        name = sharedPreferences.getString("name", "   ")

        tv4.text = "Hi, $name can you please tell us your "
        next.setOnClickListener(View.OnClickListener {
            val editor = sharedPreferences.edit()
            try {
                height = Integer.parseInt(e2.text.toString()).toFloat()
                if (height == 0f || height > 300 || height < 0) {
                    e2.error = "Invalid height"
                    e2.requestFocus()
                    return@OnClickListener
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                e2.error = "Please enter height"
                e2.requestFocus()
                return@OnClickListener
            }

            editor.putString("height", height.toString())
            try {
                weight = Integer.parseInt(e3.text.toString()).toFloat()

                if (weight == 0f || weight > 600 || weight < 0) {
                    e3.error = "Invalid weight"
                    e3.requestFocus()
                    return@OnClickListener

                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                e3.error = "Please enter weight"
                e3.requestFocus()
                return@OnClickListener
            }

            editor.putString("weight", weight.toString())
            editor.apply()

            val rid = rg1.checkedRadioButtonId
            val radioButton1: RadioButton = findViewById(rid)
            val s1 = radioButton1.text.toString()
            editor.putString("gender", s1)
            editor.apply()

            startActivity(Intent(this@OnboardActivity1, OnboardActivity2::class.java))
            finish()
        })
    }
}
