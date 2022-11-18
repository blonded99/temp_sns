package com.example.follow

import android.util.Log
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
        val currentUid = "currentUserUid"


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
                userColRef.document(currentUid).get()
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
            val clickedUser = viewModel.items[index] // 현재 로그인한 user의 팔로워 목록에서 팔로우당한 user
            userColRef.document(currentUid).get()
                .addOnSuccessListener {
//                    var followingList = mutableMapOf<String, String>() // 현재 로그인한 user의 원래 팔로잉 목록
                    val followingList = it["following"] as MutableMap<String, String> // 현재 로그인한 user의 팔로잉 목록에
                    followingList.put(clickedUser.username, clickedUser.profileImageUrl) // 해당 유저 추가

                    userColRef.document(currentUid)
                        .update("following", followingList) // firestore 팔로잉 목록 update
                    userColRef.document(currentUid)
                        .update("following count", followingList.size) // firestore 팔로잉 수 update



                    val currentUsername = it["username"].toString() // 현재 로그인한 user의 username 받아오기
                    val currentUserProfileImage = it["profile image"].toString() // 현재 로그인한 user의 profile image 받아오기

                    userColRef.whereEqualTo("username",clickedUser.username).get()
                        .addOnSuccessListener {
                            for(doc in it){
                                val followerList = doc["follower"] as MutableMap<String, String> // 팔로우당한 user의 팔로워 목록에
                                followerList.put(currentUsername,currentUserProfileImage) // 현재 로그인한 user 추가

                                userColRef.document(doc.id)
                                    .update("follower",followerList) // firestore 팔로워 목록 update
                                userColRef.document(doc.id)
                                    .update("follower count",followerList.size) // firestore 팔로워 수 update

                                // 앱에서 보여지는 현재 로그인한 user의 팔로잉 목록 업데이트
                                // <issue> follower 탭에서 팔로우시 following탭에 즉시 반영되지 않는(오락가락함) 이슈 있는데
                                // 일단 notifyDataSetChanged() 호출 문제는 아님. refresh로 호출해주는데도 변화가 없음.
                                viewModel.addItem2(Item(clickedUser.username,doc["profile image"].toString()))
                                Log.d("viewModelStatusLog","${viewModel.items2.get(0)}")
                                // viewModel에는 정상적으로 들어감
                            }
                        }

                }
        }

        private fun deleteUser(){

            // alert로 삭제하겠냐고 되묻는거 추가

            val index = adapterPosition
            val clickedUser = viewModel.items[index] // 현재 로그인한 user의 팔로워 목록에서 삭제당한 user
            userColRef.document(currentUid).get()
                .addOnSuccessListener {
//                    var followerList = mutableMapOf<String,String>() // 삭제 버튼 누른 user의 원래 팔로워 목록
                    val followerList = it["follower"] as MutableMap<String,String> // 현재 로그인한 user의 팔로워 목록에서
                    followerList.remove(clickedUser.username) // 해당 유저 삭제

                    userColRef.document(currentUid)
                        .update("follower",followerList) // firestore 팔로워 목록 update
                    userColRef.document(currentUid)
                        .update("follower count",followerList.size) // firestore 팔로워 수 update




                    val currentUsername = it["username"].toString() // 현재 로그인한 user의 username 받아오기


                    userColRef.whereEqualTo("username",clickedUser.username).get()
                        .addOnSuccessListener {
                            for (doc in it){
                                val followingList = doc["following"] as MutableMap<String, String> // 삭제당한 user의 팔로잉 목록에서
                                followingList.remove(currentUsername) // 현재 로그인한 user 삭제

                                userColRef.document(doc.id)
                                    .update("following",followingList) // firestore 팔로잉 목록 update
                                userColRef.document(doc.id)
                                    .update("following count",followingList.size) // firestore 팔로잉 수 update
                            }
                        }

//                    userColRef.document(clickedUser.username).get()
//                        .addOnSuccessListener {
////                    var followingList = mutableMapOf<String,String>() // 삭제 버튼 클릭당한 user의 원래 팔로잉 목록
//                            val followingList = it["following"] as MutableMap<String, String> // 삭제당한 user의 팔로잉 목록에서
//                            followingList.remove(currentUsername) // 현재 로그인한 user 삭제
//
//                            userColRef.document(clickedUser.username)
//                                .update("following",followingList) // firestore 팔로잉 목록 update
//                            userColRef.document(clickedUser.username)
//                                .update("following count",followingList.size) // firestore 팔로잉 수 update
//                        }


                }

//            userColRef.document(clickedUser.username).get()
//                .addOnSuccessListener {
////                    var followingList = mutableMapOf<String,String>() // 삭제 버튼 클릭당한 user의 원래 팔로잉 목록
//                    val followingList = it["following"] as MutableMap<String, String> // 삭제당한 user의 팔로잉 목록에서
//                    followingList.remove(currentUid) // 현재 로그인한 user 삭제
//
//                    userColRef.document(clickedUser.username)
//                        .update("following",followingList) // firestore 팔로잉 목록 update
//                    userColRef.document(clickedUser.username)
//                        .update("following count",followingList.size) // firestore 팔로잉 수 update
//                }

            // 앱에서 보여지는 현재 로그인한 user의 팔로워 목록 업데이트
            viewModel.deleteItem(index)
        }

    }

}