package com.example.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class FollowerFragment : Fragment() {
//    private lateinit var binding: FragmentFollowerBinding // 일단 binding 쓰는건 보류
    private val viewModel by viewModels<MyViewModel>()

    val db = Firebase.firestore
    val SignInUserEmail = "didls2654@hansung.ac.kr"

    //collection ref
    val followerList = db.collection("user").document(SignInUserEmail).collection("follower")
    //document ref
    val userRef = db.collection("user").document(SignInUserEmail)

//    private val followerUsers = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_follower, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var followerCount = 0
        userRef.get().addOnSuccessListener {
            followerCount = 2
//            followerCount = it["follower count"].toString()
        }

        //firestore DB에서 팔로워 목록 긁어와 username과 프로필사진 출력
        followerList.get().addOnSuccessListener {
            for (u in it){
                viewModel.addItem(Item(u["profile_image"].toString(),u.id))
            }
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = RecyclerViewAdapter(viewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.itemsListData.observe(viewLifecycleOwner){
            adapter.notifyDataSetChanged()
        }


//        val textview1 = view.findViewById<TextView>(R.id.username)
//        val circleimageview1 = view.findViewById<CircleImageView>(R.id.circleImageView)
//        val followbutton1 = view.findViewById<ImageButton>(R.id.followButton)
//        val deletebutton1 = view.findViewById<ImageButton>(R.id.deleteButton)
//
//        //firestore DB에서 팔로워 목록 긁어와 username과 프로필사진 출력
//        followerList.get().addOnSuccessListener {
//            for (u in it){
//                println("###########SUCCEED TO HERE###########")
////                binding.textView.text = u.id.toString()
//                textview1.text = u.id
//                Glide.with(view).load(u["profile_image"]).into(circleimageview1)
//                break
//            }
//        }
//
//        followbutton1.setOnClickListener {
//            println("follow")
//            // 팔로우 버튼 누르면 팔로우 버튼이 더이상 안 보이게 처리되고
//            followbutton1.visibility = View.INVISIBLE
//
//            // 기능도 비활성화 되어야 함
//
//            // 내 팔로잉 숫자 +1
//            // 내 팔로잉 목록에 해당 유저 추가
//
//            // 해당 유저 팔로워 숫자 +1
//            // 해당 유저 팔로워 목록에 나 추가
//        }
//
//        deletebutton1.setOnClickListener {
//            println("delete")
//
//            // 내 팔로워 숫자 -1
//            // 내 팔로워 목록에서 해당 유저 삭제
//
//            // 해당 유저 팔로잉 숫자 -1
//            // 해당 유저 팔로잉 목록에서 나 삭제
//        }

    }

}