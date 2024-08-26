package com.shariati.instagrameditable.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shariati.instagrameditable.R
import com.shariati.instagrameditable.models.Post
import com.shariati.instagrameditable.databinding.ItemRecyclerPostsBinding
import com.shariati.instagrameditable.models.PostsResponse

class PostAdapter(
    private val items: ArrayList<PostsResponse.Data>,
    private val postEv: PostEvents,
    private val context: Context
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    lateinit var binding: ItemRecyclerPostsBinding

    inner class PostViewHolder : RecyclerView.ViewHolder(binding.root) {
        fun bindView(position: Int) {

            Glide.with(context).load(items[position].media_url)
                .into(binding.postImageView)
           // binding.postImageView.setImageBitmap(bitmap)

            //set post size 1*1 in profile fragment
            postEv.setPostSize(binding.postImageView)

            /* if(items[position].icon!=0){
             binding.postIcon.setImageResource(items[position].icon)}
             else{
                 binding.postIcon.setImageResource(R.drawable.ic_transparent)
             }*/
           /* binding.root.setOnLongClickListener {
                postEv.postEvent(adapterPosition)
                true
            }*/

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        binding =
            ItemRecyclerPostsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bindView(position)
    }

    interface PostEvents {
        fun setPostSize(post: ImageView)
        fun postEvent(position: Int)
    }

}