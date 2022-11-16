package com.example.follow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Item(val username: String, val profileImageUrl: String)

class MyViewModel: ViewModel() {
    val itemsListData = MutableLiveData<ArrayList<Item>>()
    val items = ArrayList<Item>()

    val itemsListData2 = MutableLiveData<ArrayList<Item>>()
    val items2 = ArrayList<Item>()

//    var mapItems = mutableMapOf<String,String>()
//    val mapsListData = MutableLiveData<MutableMap<String,String>>()

    fun addItem(item: Item){
        items.add(item)
        itemsListData.value = items
    }

    fun deleteItem(pos: Int){
        items.removeAt(pos)
        itemsListData.value = items
    }

//    fun upDateItem(pos: Int, item: Item){
//        items[pos] = item
//        itemsListData.value = items
//    }

    fun addItem2(item: Item){
        items2.add(item)
        itemsListData2.value = items2
    }

    fun deleteItem2(pos: Int){
        items2.removeAt(pos)
        itemsListData2.value = items2
    }

}