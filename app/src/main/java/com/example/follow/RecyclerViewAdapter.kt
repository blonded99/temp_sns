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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view_follower,
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
        val SignInUsername = "test"


        private val profileImage: CircleImageView = itemView.findViewById(R.id.circleImageView)
        private val followerUsername: TextView = itemView.findViewById(R.id.userNametextView)
        private val followbutton: ImageButton = itemView.findViewById(R.id.followButton)
        private val deletebutton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun setContents(pos: Int){
            with(viewModel.items[pos]){
                // profileImage 세팅
                Glide.with(itemView).load(profileImageUrl).into(profileImage)
                // username 세팅
                followerUsername.text = username

                // 이미 맞팔이면 팔로우 버튼 처음부터 안 뜨게 처리
                // <issue> 약간 느리게 나오는 이슈 있음, 다른 로직으로 바꿔야 할 듯
                userColRef.document(SignInUsername).get()
                    .addOnSuccessListener {
                        val followingList = it["following"] as MutableMap<String,String>
                        if(followingList.containsKey(username))
                            followbutton.visibility = View.INVISIBLE
                    }
            }


            followbutton.setOnClickListener {
                // 팔로우 버튼 누르면 팔로우 버튼이 더이상 안 보이게 처리되고 기능 비활성화
                followbutton.visibility = View.INVISIBLE

                // 내 팔로잉 숫자 +1, 내 팔로잉 목록에 해당 유저 추가
                // 해당 유저 팔로워 숫자 +1, 해당 유저 팔로워 목록에 나 추가
                followUser()

            }
            deletebutton.setOnClickListener {
                // 내 팔로워 숫자 -1 , 내 팔로워 목록에서 해당 유저 삭제
                // 해당 유저 팔로잉 숫자 -1 , 해당 유저 팔로잉 목록에서 나 삭제
                deleteUser()

                // <issue> fragment 업데이트는 어케?
                //팔로잉/팔로우 목록 업데이트 하는거 콜백함수로 불러줘야겠는데 일단 뷰모델로 해결
            }
        }

        private fun followUser(){
            val index = adapterPosition
            val clickedUser = viewModel.items[index] // 현재 로그인한 user의 팔로워 목록에서 팔로우 버튼 클릭 당한 user
            userColRef.document(SignInUsername).get()
                .addOnSuccessListener {
                    var followingList = mutableMapOf<String, String>() // 팔로우 버튼 누른 user의 원래 팔로잉 목록
                    followingList = it["following"] as MutableMap<String, String> // 현재 로그인한 user의 팔로잉 목록에
                    followingList.put(clickedUser.username, clickedUser.profileImageUrl) // 해당 유저 추가

                    userColRef.document(SignInUsername)
                        .update("following", followingList) // firestore 팔로잉 목록 update
                    userColRef.document(SignInUsername)
                        .update("following count", followingList.size) // firestore 팔로잉 수 update

                    val SignInUsernameProfileImage = it["profile image"].toString() // 현재 로그인한 user의 profile image

                    userColRef.document(clickedUser.username).get()
                        .addOnSuccessListener {
                            var followerList = mutableMapOf<String,String>() // 팔로우 버튼이 클릭 당한 user의 원래 팔로워 목록
                            followerList = it["follower"] as MutableMap<String, String> // 클릭당한 user의 팔로워 목록에
                            followerList.put(SignInUsername,SignInUsernameProfileImage) // 현재 로그인한 user 추가

                            userColRef.document(clickedUser.username).update("follower",followerList) // firestore 팔로워 목록 update
                            userColRef.document(clickedUser.username).update("follower count",followerList.size) // firestore 팔로워 수 update

                            viewModel.addItem2(Item(clickedUser.username,it["profile image"].toString()))
                        }
                }
        }

        private fun deleteUser(){

            // alert로 삭제하겠냐고 되묻는거 추가

            val index = adapterPosition
            println(adapterPosition)
            val clickedUser = viewModel.items[index] // 현재 로그인한 user의 팔로워 목록에서 삭제 버튼 클릭 당한 user
            userColRef.document(SignInUsername).get()
                .addOnSuccessListener {
                    var followerList = mutableMapOf<String,String>() // 삭제 버튼 누른 user의 원래 팔로워 목록
                    followerList = it["follower"] as MutableMap<String,String> // 현재 로그인한 user의 팔로워 목록에
                    followerList.remove(clickedUser.username) // 해당 유저 삭제

                    userColRef.document(SignInUsername).update("follower",followerList) // firestore 팔로워 목록 update
                    userColRef.document(SignInUsername).update("follower count",followerList.size) // firestore 팔로워 수 update
                }

            userColRef.document(clickedUser.username).get()
                .addOnSuccessListener {
                    var followingList = mutableMapOf<String,String>() // 삭제 버튼 클릭당한 user의 원래 팔로잉 목록
                    followingList = it["following"] as MutableMap<String, String> // 클릭당한 user의 팔로잉 목록에서
                    followingList.remove(SignInUsername) // 현재 로그인한 user 삭제

                    userColRef.document(clickedUser.username).update("following",followingList) // firestore 팔로잉 목록 update
                    userColRef.document(clickedUser.username).update("following count",followingList.size) // firestore 팔로잉 수 update
                }

            viewModel.deleteItem(adapterPosition)
        }

    }

}