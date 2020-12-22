package com.phoenix.githubuser.helper

import android.database.Cursor
import com.phoenix.githubuser.db.UserContract
import com.phoenix.githubuser.entity.User

object MappingHelper {

    fun mapCursorToArrayList(usersCursor: Cursor?): ArrayList<User> {
        val usersList = ArrayList<User>()
        usersCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(UserContract.UserColumns._ID))
                val username = getString(getColumnIndexOrThrow(UserContract.UserColumns.COL_USERNAME))
                val avatar = getString(getColumnIndexOrThrow(UserContract.UserColumns.COL_AVATAR))
                val name = getString(getColumnIndexOrThrow(UserContract.UserColumns.COL_NAME))
                val user = User(id, avatar, username, name)
                usersList.add(user)
            }
        }
        return usersList
    }
    fun mapCursorToObject(usersCursor: Cursor?): User {
        var user = User()
        usersCursor?.apply {
            moveToFirst()
            val id = getInt(getColumnIndexOrThrow(UserContract.UserColumns._ID))
            val username = getString(getColumnIndexOrThrow(UserContract.UserColumns.COL_USERNAME))
            val avatar = getString(getColumnIndexOrThrow(UserContract.UserColumns.COL_AVATAR))
            val name = getString(getColumnIndexOrThrow(UserContract.UserColumns.COL_NAME))
            user = User(id, avatar, username, name)
        }
        return user
    }

    fun checkUsername(usersCursor: Cursor?, key: String?): Boolean {
        var isExist = false
        usersCursor?.apply {
            while (moveToNext()) {
                val username = getString(getColumnIndexOrThrow(UserContract.UserColumns.COL_USERNAME))
                if (key == username) {
                    isExist = true
                    break
                }
            }
        }
        return isExist
    }

    fun obtainId(usersCursor: Cursor?, key: String, value: String?): Int {
        var id: Int = 0
        usersCursor?.apply {
            while (moveToNext()) {
                val check = getString(getColumnIndexOrThrow(key))
                if (check == value) {
                    id = getInt(getColumnIndexOrThrow(UserContract.UserColumns._ID))
                    break
                }
            }
        }
        return id
    }
}