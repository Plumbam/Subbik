package com.example.subbik

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemsAdapter(var items: List<Item>, var context: Context) :
    RecyclerView.Adapter<ItemsAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.item_list_image)
        val cos: TextView = view.findViewById(R.id.item_list_cos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_in_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]

        holder.cos.text = item.cost

        val imageName = when (item.name.lowercase()) {
            "netflix" -> "netflix"
            "okko" -> "okko"
            else -> "netflix" // для заметок
        }

        val imageId = context.resources.getIdentifier(
            imageName,
            "drawable",
            context.packageName
        )

        holder.image.setImageResource(
            if (imageId != 0) imageId else R.drawable.netflix
        )
    }
}
