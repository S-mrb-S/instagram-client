package com.shariati.instagrameditable.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shariati.instagrameditable.R
import com.shariati.instagrameditable.models.Story
import com.shariati.instagrameditable.databinding.ItemRecyclerArchiveStoriesBinding
import com.shariati.instagrameditable.models.StoriesResponse
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class StoryAdapter(
    private val items: ArrayList<StoriesResponse.Data>,
    private val storyEv: StoryEvents,
    private val context: Context
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
    lateinit var binding: ItemRecyclerArchiveStoriesBinding

    inner class StoryViewHolder : RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindView(position: Int) {

            Glide.with(context)
                .load(items[position].thumbnail_url)
                .error(R.drawable.img_error)
                .into(binding.storyArchiveImageView)

            /* if (items[position].hasLabel) {
                 binding.dayMonthContainer.visibility = View.VISIBLE
                 binding.storyArchiveDay.text = items[position].date.substring(0, 2)
                 binding.storyArchiveMonth.text = items[position].date.substring(3, 6)
             } else {
                 binding.dayMonthContainer.visibility = View.GONE
             }*/

            binding.dayMonthContainer.visibility = View.VISIBLE

            if (items[position].story.showDate){
                binding.dayMonthContainer.visibility = View.VISIBLE

                val d = items[position].timestamp.substring(0, 8)

                val date = LocalDate.parse(d, DateTimeFormatter.BASIC_ISO_DATE)

                val formattedDate = date.format(DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH))

                binding.storyArchiveDay.text = formattedDate.substring(4, 6)

                binding.storyArchiveMonth.text = formattedDate.substring(0, 3)

            } else binding.dayMonthContainer.visibility = View.GONE


//          set post size 9*16 in archive fragment
            storyEv.setStorySize(binding.storyArchiveImageView)
            binding.storyArchiveImageView.transitionName = "transition$position"

           /* binding.root.setOnLongClickListener {
                storyEv.storyEvent(adapterPosition)
                true
            }*/

            binding.root.setOnClickListener {
                storyEv.showStory(binding.storyArchiveImageView, position)
            }

            //items.clear()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        binding = ItemRecyclerArchiveStoriesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoryViewHolder()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bindView(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface StoryEvents {
        fun setStorySize(story: ImageView)
        fun storyEvent(position: Int)
        fun showStory(view: ImageView, position: Int)
    }
}