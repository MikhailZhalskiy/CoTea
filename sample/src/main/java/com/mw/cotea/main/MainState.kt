package com.mw.cotea.main

import com.mw.cotea.Resource


data class MainState(
    val countOneClick: Int = 0,
    val countTwoClick: Int = 0,
    val one: Resource<Int> = Resource.Loading,
    val two: Resource<Int> = Resource.Loading,
)
