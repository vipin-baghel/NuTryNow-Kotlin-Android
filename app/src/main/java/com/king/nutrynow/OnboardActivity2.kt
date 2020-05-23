package com.king.nutrynow

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import android.widget.RadioButton
import kotlinx.android.synthetic.main.activity_onboard2.*

class OnboardActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard2)

        //Hide the action bar
        val actionBar = supportActionBar
        actionBar?.hide()

        val sharedPreferences:SharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)

        next2.setOnClickListener {
            sharedPreferences.edit().putString("goal", "Fat Loss").apply()
            sharedPreferences.edit().putString("ex_lvl", "Sedentary").apply()

            val rid2 = rg2.checkedRadioButtonId
            val radioButton2:RadioButton = findViewById(rid2)
            val s2 = radioButton2.text.toString()
            sharedPreferences.edit().putString("goal", s2).apply()

            val rid3 = rg3.checkedRadioButtonId
            val radioButton3:RadioButton = findViewById(rid3)
            val s3 = radioButton3.text.toString()
            sharedPreferences.edit().putString("exLvl", s3).apply()

            NutritionData(applicationContext)

            startActivity(Intent(this@OnboardActivity2, MainActivity::class.java))
            finish()
        }
    }

}
