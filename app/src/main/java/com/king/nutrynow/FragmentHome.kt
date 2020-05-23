package com.king.nutrynow

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.preference.PreferenceManager

import com.baoyz.swipemenulistview.SwipeMenuCreator
import com.baoyz.swipemenulistview.SwipeMenuItem
import com.baoyz.swipemenulistview.SwipeMenuListView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast



import java.util.ArrayList
import java.util.HashMap

class FragmentHome : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    private var consumedCal = 0.0
    private var consumedProtein = 0.0
    private var consumedFats = 0.0
    private var consumedCarbs = 0.0
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var btn: Button? = null
    private var uid: String? = null
    private val db = FirebaseFirestore.getInstance()
    private var foodref: CollectionReference? = null
    private val TAG = "tag"
    private var tB: TextView? = null
    private var tL: TextView? = null
    private var tS: TextView? = null
    private var tD: TextView? = null
    private var breakfast: SwipeMenuListView? = null
    private var lunch: SwipeMenuListView? = null
    private var snacks: SwipeMenuListView? = null
    private var dinner: SwipeMenuListView? = null
    private var progressBar: ProgressBar? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val rootview = inflater.inflate(R.layout.fragment_home, container, false)

        NutritionData(context!!)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        progressBar = rootview.findViewById(R.id.progress)
        progressBar!!.visibility = View.VISIBLE
        uid = sharedPreferences!!.getString("uid", "")

        foodref = db.collection("users")
                .document(uid!!)
                .collection("ItemData")

        viewPager = rootview.findViewById(R.id.viewpager)
        //viewPager.setAdapter(buildAdapter());
        tabLayout = rootview.findViewById(R.id.tab_layout)
        tabLayout!!.setupWithViewPager(viewPager, true)
        breakfast = rootview.findViewById(R.id.breakfast_listView)
        lunch = rootview.findViewById(R.id.lunch_listView)
        snacks = rootview.findViewById(R.id.snacks_listView)
        dinner = rootview.findViewById(R.id.dinner_listView)
        tB = rootview.findViewById(R.id.nofood_b)
        tL = rootview.findViewById(R.id.nofood_l)
        tS = rootview.findViewById(R.id.nofood_s)
        tD = rootview.findViewById(R.id.nofood_d)

        setupfooddata(breakfast, "Breakfast", tB)
        setupfooddata(lunch, "Lunch", tL)
        setupfooddata(snacks, "Snacks", tS)
        setupfooddata(dinner, "Dinner", tD)

        btn = rootview.findViewById(R.id.clear_logs)
        btn!!.setOnClickListener {
            val progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Clearing all Logs")
            progressDialog.show()
            foodref!!.get()
                    .addOnSuccessListener { queryDocumentSnapshots ->
                        for (snapshots in queryDocumentSnapshots) {
                            Log.d(TAG, "DELETING ====>>> " + snapshots.id + ":" + snapshots.data)
                            foodref!!.document(snapshots.id).delete()
                        }
                        consumedCal = 0.0
                        consumedProtein = 0.0
                        consumedFats = 0.0
                        consumedCarbs = 0.0
                        setupfooddata(breakfast, "Breakfast", tB)
                        setupfooddata(lunch, "Lunch", tL)
                        setupfooddata(snacks, "Snacks", tS)
                        setupfooddata(dinner, "Dinner", tD)
                        progressDialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG, e.toString())
                        Toast.makeText(context, "Error Clearing Logs", Toast.LENGTH_SHORT).show()

                        progressDialog.dismiss()
                    }
        }

        return rootview
    }


    private fun buildAdapter(): PagerAdapter {
        return ScreenSlidePagerAdapter(childFragmentManager)
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val slideFragment = SlideFragment()
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putDouble("consumedCal", consumedCal)
            bundle.putDouble("consumedProtein", consumedProtein)
            bundle.putDouble("consumedFats", consumedFats)
            bundle.putDouble("consumedCarbs", consumedCarbs)

            slideFragment.arguments = bundle

            return slideFragment
        }

        override fun getCount(): Int {
            return 5
        }
    }

    private fun createSwipeMenu(listView: SwipeMenuListView) {
        val creator = SwipeMenuCreator { menu ->
            // create "delete" item
            val deleteItem = SwipeMenuItem(
                    context)
            // set item background
            deleteItem.background = ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25))
            // set item width
            deleteItem.width = 150
            // set a icon
            deleteItem.setIcon(R.drawable.ic_delete_black_24dp)
            // add to menu
            menu.addMenuItem(deleteItem)
        }

        // set creator
        listView.setMenuCreator(creator)
    }

    private fun setupfooddata(listView: SwipeMenuListView?, s: String, t: TextView?) {

        foodref!!.whereEqualTo("when", s)
                .get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    progressBar!!.visibility = View.VISIBLE
                    val list = ArrayList<ItemData>()
                    val docId = ArrayList<String>()
                    for (queryDocumentSnapshot in queryDocumentSnapshots) {
                        Log.d(TAG, queryDocumentSnapshot.id + " ===>>> " + queryDocumentSnapshot.data)
                        docId.add(queryDocumentSnapshot.id)
                        val data = queryDocumentSnapshot.toObject<ItemData>(ItemData::class.java)
                        list.add(data)
                        try {
                            consumedCal += queryDocumentSnapshot.getDouble("calConsumed")!!
                            consumedProtein += queryDocumentSnapshot.getDouble("proteinConsumed")!!
                            consumedFats += queryDocumentSnapshot.getDouble("fats_consumed")!!
                            consumedCarbs += queryDocumentSnapshot.getDouble("carbsConsumed")!!
                            consumedCal = Math.round(consumedCal).toDouble()
                            consumedProtein = Math.round(consumedProtein).toDouble()
                            consumedCarbs = Math.round(consumedCarbs).toDouble()
                            consumedFats = Math.round(consumedFats).toDouble()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                    if (context != null) {
                        val adapter = ListViewAdapter(context!!, list)
                        listView!!.adapter = adapter
                        createSwipeMenu(listView)
                        listView.setOnMenuItemClickListener { position, menu, index ->
                            foodref!!.document(docId[position]).delete()
                            consumedCal = 0.0
                            consumedProtein = 0.0
                            consumedFats = 0.0
                            consumedCarbs = 0.0
                            setupfooddata(breakfast, "Breakfast", tB)
                            setupfooddata(lunch, "Lunch", tL)
                            setupfooddata(snacks, "Snacks", tS)
                            setupfooddata(dinner, "Dinner", tD)
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                            // false : close the menu; true : not close the menu
                            false
                        }
                        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                            val data = adapter.getItem(position)
                            val name = data!!.name
                            val brand = data.brand
                            val item_id = data.id
                            val serving = data.serving
                            val i = Intent(context, ItemInfoActivity::class.java)
                            i.putExtra("item_name", name)
                            i.putExtra("item_brand", brand)
                            i.putExtra("item_id", item_id)
                            i.putExtra("serving", serving)
                            startActivity(i)
                        }
                        Utility.setListViewHeightBasedOnChildren(listView)
                        if (adapter.isEmpty) {
                            t!!.visibility = View.VISIBLE
                        } else {
                            t!!.visibility = View.GONE
                        }
                        Log.d("total cal consumed", consumedCal.toString())
                        Log.d("total protein consumed", consumedProtein.toString())
                        Log.d("total carbs consumed", consumedCarbs.toString())
                        Log.d("total fats consumed", consumedFats.toString())

                        viewPager!!.adapter = buildAdapter()
                        progressBar!!.visibility = View.GONE

                    }
                }.addOnFailureListener { e -> Log.d(TAG, e.toString()) }
    }

}
