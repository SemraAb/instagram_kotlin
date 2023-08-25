package com.samra.instagramkotlin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.samra.instagramkotlin.databinding.RecyclerRowBinding
import com.samra.instagramkotlin.model.Post
import com.squareup.picasso.Picasso

class FeedRecyclerAdapter(private var postList: ArrayList<Post>): RecyclerView.Adapter<FeedRecyclerAdapter.FeedViewHolder>() {
    class FeedViewHolder(var binding:RecyclerRowBinding ) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return FeedViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.binding.recyclerEmailText.text = postList.get(position).email
        holder.binding.recyclerCommentText.text = postList.get(position).comment
        Picasso.get().load(postList.get(position).downloadUrl).into(holder.binding.recyclerImageView)
    }
}