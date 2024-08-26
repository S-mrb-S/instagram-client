package com.shariati.instagrameditable.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shariati.instagrameditable.R
import com.shariati.instagrameditable.models.Story
import com.shariati.instagrameditable.databinding.ItemRecyclerStoriesInsightBinding
import com.shariati.instagrameditable.models.StoriesResponse

class InsightStoryAdapter(
    private val items: ArrayList<StoriesResponse.Data>,
    private val storyEv: InsightEvents,
    private val context: Context
) : RecyclerView.Adapter<InsightStoryAdapter.InsightStoryViewHolder>() {
    lateinit var binding: ItemRecyclerStoriesInsightBinding

    inner class InsightStoryViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun bindView(position: Int) {
            //set story size
            storyEv.setPostSize(binding.storyImageView, position, binding.storyReachedContainer)

            if (items[position].media_type == "IMAGE")
                Glide.with(context)
                    .load(items[position].media_url)
                    .error(R.drawable.img_error)
                    .into(binding.storyImageView)
             else
                Glide.with(context)
                    .load(items[position].thumbnail_url)
                    .error(R.drawable.img_error)
                    .into(binding.storyImageView)

            binding.insightStoryRecyclerNoView.text = items[position].story.reached
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsightStoryViewHolder {
        binding = ItemRecyclerStoriesInsightBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InsightStoryViewHolder()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: InsightStoryViewHolder, position: Int) {
        //holder.bindView(position)
        val item = items[position]
        //holder.bind(item)

        holder.bindView(position)

        // Example: Adjusting size based on position
        val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        if (position == 2) {
            layoutParams.width = 240 // Change the height for even positions
            layoutParams.height = 280 // Change the height for even positions
            //layoutParams. = 0
            layoutParams.rightMargin = 0
        } else {
            layoutParams.width = 210 // Change the height for odd positions
            layoutParams.height = 240 // Change the height for odd positions
        }
        holder.itemView.layoutParams = layoutParams
    }

    interface InsightEvents {
        fun setPostSize(story: ImageView, position: Int, storyReachedContainer: FrameLayout)
    }
}