package com.example.follow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class RecyclerViewAdapter(private val viewModel: MyViewModel):
    RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view,
            parent, false)
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = ItemViewBinding.inflate(inflater,parent,false)
        return RecyclerViewViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.setContents(position)
    }

    override fun getItemCount(): Int {
        // 팔로워 숫자
        return viewModel.items.size
    }

    inner class RecyclerViewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val profileImage: CircleImageView = itemView.findViewById(R.id.circleImageView)
        private val username: TextView = itemView.findViewById(R.id.userNametextView)
        private val followbutton: ImageButton = itemView.findViewById(R.id.followButton)
        private val deletebutton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun setContents(pos: Int){
            with(viewModel.items[pos]){
                // profileImage 세팅
                Glide.with(itemView).load(profileImageUrl).into(profileImage)
                username.text = followerUsername
            }
            followbutton.setOnClickListener {
                println("follow")
            }
            deletebutton.setOnClickListener {
                println("delete")
            }
        }

    }

}