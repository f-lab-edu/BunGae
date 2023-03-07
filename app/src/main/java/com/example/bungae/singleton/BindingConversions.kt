package com.example.bungae.singleton

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.bungae.R

object BindingConversions {

    @BindingAdapter("imageUrl")
    @JvmStatic
    fun loadImage(imageView: ImageView, uri: String?) {
        if (uri != null) {
            Glide.with(imageView.context)
                .load(uri)
                .error(R.drawable.image_landscape)
                .placeholder(R.drawable.img_loading)
                .into(imageView)
        }
    }
}
