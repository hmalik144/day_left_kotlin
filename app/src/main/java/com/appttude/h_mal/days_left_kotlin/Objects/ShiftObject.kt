package com.appttude.h_mal.days_left_kotlin.Objects

import java.io.Serializable

data class ShiftObject(
    var shiftDate: String = "",
    var dateTimeAdded: String = "",
    var abnObject: AbnObject? = AbnObject(),
    var taskObject: TaskObject? = TaskObject(),
    var unitsCount: Float? = 0f,
    var timeObject: TimeObject? = TimeObject()
)