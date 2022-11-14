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

    // folllower collection ref
    val followerList = db.collection("user").document(SignInUserEmail).collection("follower")
    // useremail document ref
    val userRef = db.collection("user").document(SignInUserEmail)

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

//        var followerCount = 0
//        userRef.get().addOnSuccessListener {
//            followerCount = 2
////            followerCount = it["follower count"].toString()
//        }

        // firestore DB에서 팔로워 목록 긁어와 username과 프로필사진url을 viewmodel에 넣음
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

    }

}