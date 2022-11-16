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

class RecyclerViewAdapter2(private val viewModel: MyViewModel):
    RecyclerView.Adapter<RecyclerViewAdapter2.RecyclerViewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view_following,
            parent, false)
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = ItemViewBinding.inflate(inflater,parent,false)
        return RecyclerViewViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.setContents(position)
    }

    override fun getItemCount(): Int {
        // 팔로잉 수
        return viewModel.items2.size
    }

    inner class RecyclerViewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val db = Firebase.firestore
        val userColRef = db.collection("user")
        val SignInUsername = "test"


        private val profileImage: CircleImageView = itemView.findViewById(R.id.circleImageView)
        private val followerUsername: TextView = itemView.findViewById(R.id.userNametextView)
        private val unfollowButton: ImageButton = itemView.findViewById(R.id.followStatusButton)

        fun setContents(pos: Int){
            with(viewModel.items2[pos]){
                // profileImage 세팅
                Glide.with(itemView).load(profileImageUrl).into(profileImage)
                // username 세팅
                followerUsername.text = username
            }

            unfollowButton.setOnClickListener {
                // 내 팔로잉 숫자 -1 , 내 팔로잉 목록에서 해당 유저 삭제
                // 해당 유저 팔로워 숫자 -1 , 해당 유저 팔로워 목록에서 나 삭제
                unfollowUser()

            }
        }

        private fun unfollowUser(){

            // alert로 언팔로우 하겠냐고 되묻는거 추가

            // <issue> 맞팔중인 상태에서 팔로잉 목록에서 unfollow 했을때, 팔로워 목록에 있는 해당 유저에 Follow 버튼이 다시 활성화 되어야 하는데 보이지 않음.

            val index = adapterPosition
            println(adapterPosition)
            val clickedUser = viewModel.items2[index] // 현재 로그인한 user의 팔로잉 목록에서 언팔로우 버튼 클릭 당한 user
            userColRef.document(SignInUsername).get()
                .addOnSuccessListener {
                    var followingList = mutableMapOf<String,String>() // 언팔로우 버튼 누른 user의 원래 팔로잉 목록
                    followingList = it["following"] as MutableMap<String,String> // 현재 로그인한 user의 팔로잉 목록에
                    followingList.remove(clickedUser.username) // 해당 유저 삭제

                    userColRef.document(SignInUsername).update("following",followingList) // firestore 팔로잉 목록 update
                    userColRef.document(SignInUsername).update("following count",followingList.size) // firestore 팔로잉 수 update
                }

            userColRef.document(clickedUser.username).get()
                .addOnSuccessListener {
                    var followerList = mutableMapOf<String,String>() // 언팔로우 버튼 클릭당한 user의 원래 팔로워 목록
                    followerList = it["follower"] as MutableMap<String, String> // 클릭당한 user의 팔로워 목록에서
                    followerList.remove(SignInUsername) // 현재 로그인한 user 삭제

                    userColRef.document(clickedUser.username).update("follower",followerList) // firestore 팔로워 목록 update
                    userColRef.document(clickedUser.username).update("follower count",followerList.size) // firestore 팔로워 수 update
                }

            viewModel.deleteItem2(adapterPosition)

            // <issue> 팔로우 버튼 다른 버튼으로(다시 팔로우하는) 변경하는거 추가해야함
            // 한 가지 대안으로, 버튼 텍스트를 "팔로잉" 말고 "팔로잉 중" 으로 바꾸고 follower 탭에서 삭제 버튼과 유사한 로직으로 그냥 삭제시켜버리면
            // 굳이 버튼을 교체하지 않아도 됨.
        }

    }

}