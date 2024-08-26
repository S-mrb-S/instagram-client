package com.shariati.instagrameditable.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shariati.instagrameditable.models.Highlight
import com.shariati.instagrameditable.databinding.ItemRecyclerHighlightsBinding

class HighlightAdapter(private val highlightEv:HighlightEvents, private val items:ArrayList<Highlight>, private val context: Context):RecyclerView.Adapter<HighlightAdapter.HighlightViewHolder>(){
    lateinit var binding:ItemRecyclerHighlightsBinding
    inner class HighlightViewHolder:RecyclerView.ViewHolder(binding.root){
        fun bindView(position: Int){
            binding.itemHighlightName.text = items[position].name
//get the path from profile fragment and show that
            // دریافت رشته Base64
            val previouslyEncodedImage: String = items[position].path
            // بررسی آیا رشته معتبر است
            if (previouslyEncodedImage.isNotBlank()) {
                // تبدیل رشته Base64 به آرایه بایت
                val b: ByteArray = Base64.decode(previouslyEncodedImage, Base64.DEFAULT)

                // تبدیل آرایه بایت به Bitmap
                val bitmap: Bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)

                // نمایش تصویر در ImageView
                binding.itemHighlightImage.setImageBitmap(bitmap)

                binding.root.setOnLongClickListener {
                    highlightEv.highlightEvent(adapterPosition)
                    true
                }
        }
    }}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighlightViewHolder {
        binding = ItemRecyclerHighlightsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HighlightViewHolder()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: HighlightViewHolder, position: Int) {
        holder.bindView(position)
    }

    interface HighlightEvents{
        fun highlightEvent(position: Int)
    }
}