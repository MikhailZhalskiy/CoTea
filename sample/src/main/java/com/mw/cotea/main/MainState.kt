package com.mw.cotea.main

import com.mw.cotea.Resource


data class MainState(
    val inputText: String = "",
    val words: Resource<List<String>> = Resource.Data(listOf()),
    val loadData: Resource<Int> = Resource.Data(0)
)
