package com.king.nutrynow

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_onboard2.*
import java.util.HashMap

class FragmentSettings : Fragment() {

    override fun onPause() {
        super.onPause()
        updateUserData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_settings, container, false)

        fragmentManager!!.beginTransaction().add(R.id.replaceMe, MyPreferenceFragment()).commit()

        return rootView
    }

    class MyPreferenceFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(bundle: Bundle?, s: String?) {

            addPreferencesFromResource(R.xml.preferences)

            val height: EditTextPreference = preferenceScreen.findPreference(
                    "height") as EditTextPreference
            height.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                if (o.toString().trim { it <= ' ' } == "") {

                    Toast.makeText(activity, "Height can not be empty",
                            Toast.LENGTH_SHORT).show()
                    return@OnPreferenceChangeListener false
                } else if (o.toString().trim { it <= ' ' } == "0") {
                    Toast.makeText(activity, "Height can not be 0 ",
                            Toast.LENGTH_SHORT).show()
                    return@OnPreferenceChangeListener false
                } else if (java.lang.Float.parseFloat(o.toString()) > 300f || java.lang.Float.parseFloat(o.toString()) < 0f) {
                    Toast.makeText(activity, "Please enter valid height",
                            Toast.LENGTH_SHORT).show()
                    return@OnPreferenceChangeListener false
                }
                true
            }

            val weight: EditTextPreference = preferenceScreen.findPreference(
                    "weight") as EditTextPreference
            weight.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                if (o.toString().trim { it <= ' ' } == "") {
                    Toast.makeText(activity, "Weight can not be empty",
                            Toast.LENGTH_SHORT).show()
                    return@OnPreferenceChangeListener false
                } else if (o.toString().trim { it <= ' ' } == "0") {
                    Toast.makeText(activity, "Weight can not be 0 ",
                            Toast.LENGTH_SHORT).show()
                    return@OnPreferenceChangeListener false
                } else if (java.lang.Float.parseFloat(o.toString()) > 600.0f || java.lang.Float.parseFloat(o.toString()) < 0f) {
                    Toast.makeText(activity, "Please enter valid weight",
                            Toast.LENGTH_SHORT).show()
                    return@OnPreferenceChangeListener false
                }
                true
            }

            val contact = preferenceScreen.findPreference(
                    "contact")
            contact.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                try {
                    val emailIntent = Intent(Intent.ACTION_SENDTO,
                            Uri.fromParts("mailto", "vipinbaghel1999@gmail.com", null))
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "NuTryNow")
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, Vipin\nThis app is amazing, I'm loving it.")
                    startActivity(Intent.createChooser(emailIntent, "Send email using.."))

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context,
                            "No app found to send mail",
                            Toast.LENGTH_SHORT).show()
                }

                true
            }

            val signOut = preferenceScreen.findPreference(
                    "signout")
            signOut.onPreferenceClickListener = Preference.OnPreferenceClickListener {

                FirebaseAuth.getInstance().signOut()
                val i = Intent(context, SignInActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(i)

                true
            }
        }
    }

    private fun updateUserData() {

        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        // Create document to be added (say user) to database
        val goal = sharedPreferences.getString("goal", "")
        val weight = sharedPreferences.getString("weight", "")
        val height = sharedPreferences.getString("height", "")
        val exLvl = sharedPreferences.getString("exLvl", "")
        val gender = sharedPreferences.getString("gender", "")
        val name = sharedPreferences.getString("name", "")
        val uid = sharedPreferences.getString("uid", "")

        val user = HashMap<String, Any>()
        user["uid"] = uid!!
        user["name"] = name!!
        user["goal"] = goal!!
        user["weight"] = weight!!
        user["height"] = height!!
        user["exLvl"] = exLvl!!
        user["gender"] = gender!!
        val db = FirebaseFirestore.getInstance()
        // Add document to correct user (watch uid) in database
        db.collection("users").document(uid!!)
                .set(user)
                .addOnSuccessListener {
                    Log.d("Preference changes", "database update success")

                }
                .addOnFailureListener { e -> Log.d("error", e.toString()) }
    }

}
