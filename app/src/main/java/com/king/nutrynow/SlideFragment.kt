package com.king.nutrynow

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class SlideFragment : Fragment() {

    private var bmi: Float = 0.toFloat()
    private var protein: Float = 0.toFloat()
    private var carbs: Float = 0.toFloat()
    private var fats: Float = 0.toFloat()
    private var cal: Float = 0.toFloat()
    internal var name: String? = null
    private var healthStat: String? = null
    private var position: Int? = null
    private var consumedCal = 0.0
    private var consumedProtein = 0.0
    private var consumedFats = 0.0
    private var consumedCarbs = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewGroup = inflater.inflate(R.layout.slide_fragment, container, false) as ViewGroup
        val textView: TextView = viewGroup.findViewById(R.id.textView)
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        name = sharedPreferences.getString("name", "")
        healthStat = sharedPreferences.getString("hs", "")
        bmi = sharedPreferences.getFloat("bmi", 0f)
        protein = sharedPreferences.getFloat("protein", 0f)
        carbs = sharedPreferences.getFloat("carb", 0f)
        fats = sharedPreferences.getFloat("fat", 0f)
        cal = sharedPreferences.getFloat("cal", 0f)


        position = arguments!!.getInt("position")
        consumedCal = arguments!!.getDouble("consumedCal")
        consumedProtein = arguments!!.getDouble("consumedProtein")
        consumedFats = arguments!!.getDouble("consumedFats")
        consumedCarbs = arguments!!.getDouble("consumedCarbs")


        Log.d("total cal viewpager", consumedCal.toString())
        Log.d("total protein viewpage ", consumedProtein.toString())
        Log.d("total carbs viewpager", consumedCarbs.toString())
        Log.d("total fats viewpager", consumedFats.toString())

        when (position) {
            0 -> textView.text = "Hi " + name + ", " + healthStat +
                    "\nYour BMI : " + bmi
            1 -> textView.text = "Calories\n" + "Daily req: " + cal + "kcal\n" +
                    "Consumed: " + consumedCal + "kcal"
            2 -> textView.text = "Protein\n" + "Daily req: " + protein + "g\n" +
                    "Consumed: " + consumedProtein + "g"
            3 -> textView.text = "Fats\n" + "Daily req: " + fats + "g\n" +
                    "Consumed: " + consumedFats + "g"
            4 -> textView.text = "Carbohydrates\n" + "Daily req: " + carbs + "g\n" +
                    "Consumed: " + consumedCarbs + "g"
        }

        return viewGroup
    }
}
