package com.king.nutrynow

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ListViewAdapter(context: Context, objects: List<ItemData>) : ArrayAdapter<ItemData>(context, 0, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = (context as Activity).layoutInflater.inflate(R.layout.item_listview, parent, false)
        }
        val textView = convertView!!.findViewById<TextView>(R.id.name)
        val textView1 = convertView.findViewById<TextView>(R.id.brand)
        val textView2 = convertView.findViewById<TextView>(R.id.howmuch)

        val itemData = getItem(position)

        textView.text = itemData!!.name
        textView1.text = itemData.brand
        textView2.text = itemData.serving.toString() + "g"

        return convertView
    }

}
