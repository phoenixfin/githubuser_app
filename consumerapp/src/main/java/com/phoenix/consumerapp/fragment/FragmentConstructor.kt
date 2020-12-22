package com.phoenix.consumerapp.fragment

import android.os.Bundle
import com.phoenix.consumerapp.db.UserContract.UserColumns.Companion.COL_USERNAME

class FragmentConstructor {
    companion object {
        fun initFollowing(username: String?): FollowingFragment {
            val fragment = FollowingFragment()
            val bundle = Bundle()
            bundle.putString(COL_USERNAME, username)
            fragment.arguments = bundle
            return fragment
        }

        fun initFollowers(username: String?): FollowersFragment {
            val fragment = FollowersFragment()
            val bundle = Bundle()
            bundle.putString(COL_USERNAME, username)
            fragment.arguments = bundle
            return fragment
        }
    }
}