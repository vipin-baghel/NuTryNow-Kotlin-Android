package com.king.nutrynow

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log

class NutritionData(internal var context: Context) {

    private var bmi: Float = 0.toFloat()
    private var mCalories: Float = 0.toFloat()
    private var lCalories: Float = 0.toFloat()
    private var gCalories: Float = 0.toFloat()
    private var fat: Float = 0.toFloat()
    private var carb: Float = 0.toFloat()
    private var protein: Float = 0.toFloat()
    private var weightLbs: Float = 0.toFloat()
    private var weight: Float = 0.toFloat()
    private var genderMf: Float = 0.toFloat()
    private var activityMf: Float = 0.toFloat()
    internal var height: Float = 0.toFloat()
    private var goal: String? = null
    private var exLvl: String? = null
    private var gender: String? = null
    private var h: String? = null
    private var w: String? = null
    private lateinit var healthStat: String
    private  var sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        this.goal = sharedPreferences.getString("goal", "")
        this.w = sharedPreferences.getString("weight", "")
        this.h = sharedPreferences.getString("height", "")
        this.exLvl = sharedPreferences.getString("exLvl", "")
        this.gender = sharedPreferences.getString("gender", "")

        Log.d("Gender value ::: ",gender)
        genderMf = when (gender!!) {
            "Male" -> 1F
            "Female" -> 0.8F
            else -> 0f
        }

        Log.d("exLvl value ::: ",exLvl)
        Log.i("exLvl ::: ",exLvl)
        when (exLvl!!) {
            "Sedentary" -> activityMf = 1f
            "Moderate" -> activityMf = 1.2f
            "Intense" -> activityMf = 1.4f
        }
        Log.d("exercise ka mf :: ",activityMf.toString())
        height = java.lang.Float.parseFloat(h!!)
        weight = java.lang.Float.parseFloat(w!!)

        this.bmi = weight / (height * height / 10000)

        weightLbs = weight * 2.20462f
        mCalories = weightLbs * 15f * genderMf * activityMf
        lCalories = (weightLbs - weightLbs * 0.20f) * 15f * genderMf * activityMf
        gCalories = (weightLbs + weightLbs * 0.25f) * 15f * genderMf * activityMf

        Log.d("Goal value :::",goal)
        when (goal!!) {
            "Maintain" -> {
                protein = weightLbs * genderMf * activityMf
                fat = mCalories * 0.25f / 9f
                carb = (mCalories - (fat * 9 + protein * 4)) / 4
            }

            "Fat Loss" -> {
                protein = weightLbs * 1.3f * genderMf * activityMf
                fat = lCalories * 0.2f / 9
                carb = (lCalories - (fat * 9 + protein * 4)) / 4
            }

            "Muscle Gain" -> {
                protein = weightLbs * genderMf * activityMf
                fat = gCalories * 0.25f / 9f
                carb = (gCalories - (fat * 9 + protein * 4)) / 4
            }
        }

        Log.d("total cal req", mCalories.toString())
        Log.d("total protein req ", protein.toString())
        Log.d("total carbs req", carb.toString())
        Log.d("total fats req", fat.toString())

        sharedPreferences.edit().putFloat("bmi", bmi).apply()
        sharedPreferences.edit().putFloat("protein", protein).apply()
        sharedPreferences.edit().putFloat("fat", fat).apply()
        sharedPreferences.edit().putFloat("carb", carb).apply()
        sharedPreferences.edit().putFloat("cal", mCalories).apply()

        if (bmi.toDouble() == 0.00)
            healthStat = "something went wrong !!"
        else if (bmi <= 10.00)
            healthStat = "you are severely underweight !!"
        else if (bmi > 10.00 && bmi <= 16.00)
            healthStat = "you are  underweight !!"
        else if (bmi > 16.00 && bmi <= 18.50)
            healthStat = "you are slightly underweight !!"
        else if (bmi > 18.5 && bmi <= 25)
            healthStat = "you are healthy !! "
        else if (bmi > 25.00 && bmi <= 27.00)
            healthStat = "you are slightly overweight !!"
        else if (bmi > 27.00 && bmi <= 30.00)
            healthStat = "you are overweight !!"
        else if (bmi > 30.00)
            healthStat = "you are severely overweight !!"

        sharedPreferences.edit().putString("hs", healthStat).apply()
    }
}
