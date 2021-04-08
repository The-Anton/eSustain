package com.solvabit.climate.fragment.PlantTreesFragments

import android.graphics.LightingColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.solvabit.climate.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_staggered_recycler_view.view.*

class StaggeredRecyclerViewAdapter(private val images: MutableList<String>)
    : RecyclerView.Adapter<StaggeredRecyclerViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflate the custom view from xml layout file
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_staggered_recycler_view,parent,false)

        // return the view holder
        return ViewHolder(view)

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // display the current color
        holder.image.colorFilter = LightingColorFilter(0xFF7F7F7F.toInt(),0x00000000)
        Picasso.get().load(images[position]).into(holder.image)
//        holder.color.text = images[position]
    }


    override fun getItemCount(): Int {
        // number of items in the data set held by the adapter
        return images.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val image: ImageView = itemView.staggered_imageView
    }


    // this two methods useful for avoiding duplicate item
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }
}