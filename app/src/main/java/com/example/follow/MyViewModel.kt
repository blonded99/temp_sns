package com.example.follow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Item(val profileImageUrl: String, val followerUsername: String)

class MyViewModel: ViewModel() {
    val itemsListData = MutableLiveData<ArrayList<Item>>()
    val items = ArrayList<Item>()

    init{
        addItem(Item("abcd","blonded99"))
        addItem(Item("abcd","didls2654"))
    }

    fun addItem(item: Item){
        items.add(item)
        itemsListData.value = items
    }

    fun upDateItem(pos: Int, item: Item){
        items[pos] = item
        itemsListData.value = items
    }

    fun deleteItem(pos: Int){
        items.removeAt(pos)
        itemsListData.value = items
    }
}