package com.example.follow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
        // 팔로워 수
        return viewModel.items.size
    }

    inner class RecyclerViewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val db = Firebase.firestore
        val userColRef = db.collection("user")


        private val profileImage: CircleImageView = itemView.findViewById(R.id.circleImageView)
        private val username: TextView = itemView.findViewById(R.id.userNametextView)
        private val followbutton: ImageButton = itemView.findViewById(R.id.followButton)
        private val deletebutton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun setContents(pos: Int){
            with(viewModel.items[pos]){
                // profileImage 세팅
                Glide.with(itemView).load(profileImageUrl).into(profileImage)
                // username 세팅
                username.text = followerUsername
            }
            followbutton.setOnClickListener {
                println("follow button clicked")

                val index = adapterPosition
                println(index)
                println(viewModel.items[index].followerUsername)


                // 팔로우 버튼 누르면 팔로우 버튼이 더이상 안 보이게 처리되고 기능 비활성화
                followbutton.visibility = View.INVISIBLE

                // 내 팔로잉 숫자 +1
                // 내 팔로잉 목록에 해당 유저 추가

                // 해당 유저 팔로워 숫자 +1
                // 해당 유저 팔로워 목록에 나 추가
            }
            deletebutton.setOnClickListener {
                println("delete button clicked")
                // 내 팔로워 숫자 -1
                // 내 팔로워 목록에서 해당 유저 삭제

                // 해당 유저 팔로잉 숫자 -1
                // 해당 유저 팔로잉 목록에서 나 삭제
            }
        }

    }

}