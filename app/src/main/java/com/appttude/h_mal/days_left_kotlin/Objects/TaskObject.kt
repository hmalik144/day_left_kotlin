package com.appttude.h_mal.days_left_kotlin.Objects

import java.io.Serializable

data class TaskObject (
    var workType: String? = "",
    var rate: Float = 0.toFloat(),
    var task: String? = ""
) : Serializable