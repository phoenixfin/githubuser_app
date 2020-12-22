package com.phoenix.githubuser.db

import android.net.Uri
import android.provider.BaseColumns

object UserContract {
    const val AUTHORITY = "com.phoenix.githubuser"
    const val SCHEME = "content"

    class UserColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "favorite_user"
            const val _ID = "_id"
            const val COL_USERNAME = "username"
            const val COL_AVATAR = "avatar_url"
            const val COL_NAME = "name"
            const val COL_COMPANY = "company"
            const val COL_LOCATION = "location"
            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }

}