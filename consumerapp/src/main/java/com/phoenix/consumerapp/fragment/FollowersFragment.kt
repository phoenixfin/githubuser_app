package com.phoenix.consumerapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.phoenix.consumerapp.MainActivity.Companion.token
import com.phoenix.consumerapp.R
import com.phoenix.consumerapp.adapter.UserAdapter
import com.phoenix.consumerapp.entity.User
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fragment_followers.view.*
import org.json.JSONArray


class FollowersFragment : Fragment() {
    private lateinit var rvFollowers: RecyclerView
    private var list: ArrayList<User> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_followers, container, false)
        view.progressBarFollowers.visibility = View.INVISIBLE
        rvFollowers = view.findViewById<View>(R.id.rv_followers) as RecyclerView
        rvFollowers.setHasFixedSize(true)
        getData(arguments?.getString("username"))

        return view
    }

    private fun showRecyclerList() {
        rvFollowers.layoutManager = LinearLayoutManager(activity)
        val listAdapter = UserAdapter(list)
        rvFollowers.adapter = listAdapter
        listAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                return
            }
        })
    }

    private fun getData(username: String?) {
        view?.progressBarFollowers?.visibility = View.VISIBLE
        val url = "https://api.github.com/users/$username/followers"
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token $token")
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                try {
                    view?.progressBarFollowers?.visibility = View.INVISIBLE
                    val jsonArray = JSONArray(String(responseBody))
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val avatar = jsonObject.getString("avatar_url")
                        val username = jsonObject.getString("login")
                        val user = User(avatar= avatar, username=username)
                        list.add(user)
                    }
                    showRecyclerList()
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                view?.progressBarFollowers?.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}

