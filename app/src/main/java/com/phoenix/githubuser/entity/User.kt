package com.phoenix.githubuser.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var id: Int = 0,
    var avatar: String = "",
    var username: String = "",
    var name: String = "",
    var location: String = "",
    var company: String = ""
) : Parcelable