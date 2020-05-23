package com.king.nutrynow

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class RecyclerViewAdapter(private val context: Context, private val data: List<ItemData>) : RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerViewHolder {

        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.list_item, viewGroup, false)

        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(recyclerViewHolder: RecyclerViewHolder, i: Int) {
        recyclerViewHolder.item.text = data[i].name
        recyclerViewHolder.brand.text = "Brand: " + data[i].brand

        recyclerViewHolder.itemView.setOnClickListener {
            val intent = Intent(context, ItemInfoActivity::class.java)
            intent.putExtra("item_id", data[i].id)
            intent.putExtra("item_name", data[i].name)
            intent.putExtra("item_brand", data[i].brand)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var item: TextView = itemView.findViewById(R.id.item)
        internal var brand: TextView = itemView.findViewById(R.id.brand)

    }
}

