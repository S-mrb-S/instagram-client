package com.shariati.instagrameditable.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.shariati.instagrameditable.R
import com.shariati.instagrameditable.models.StoriesResponse

class ImagePagerAdapter(
    private val items: ArrayList<StoriesResponse.Data>,
    private val context: Context,
    private val viewPager2: ViewPager2
) : RecyclerView.Adapter<ImagePagerAdapter.ImageviewHolder>() {

    class ImageviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.storyImage)
        val viewContainer: LinearLayout= itemView.findViewById(R.id.insight_story_recycler_no_view_container)
        val view: TextView = itemView.findViewById(R.id.insight_story_recycler_no_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageviewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewpager_items, parent, false)
        return ImageviewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getPageWidth(position: Int): Float {
        return 0.5f
    }

    override fun onBindViewHolder(holder: ImageviewHolder, position: Int) {
        if(items[position].caption=="it's video icon"){
        holder.viewContainer.visibility = View.GONE
        }else{
        holder.view.text = items[position].story.reached}

        if (items[position].media_type == "IMAGE")
            Glide.with(context)
                .load(items[position].media_url)
                .error(R.drawable.img_error)
                .into(holder.imageView)

        else
            Glide.with(context)
                .load(items[position].thumbnail_url)
                .error(R.drawable.img_error)
                .into(holder.imageView)
    }


}