package com.phoenix.githubuser

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.phoenix.githubuser.MainActivity.Companion.USER
import com.phoenix.githubuser.MainActivity.Companion.token
import com.phoenix.githubuser.adapter.UserAdapter
import com.phoenix.githubuser.db.UserContract.UserColumns.Companion.CONTENT_URI
import com.phoenix.githubuser.entity.User
import com.phoenix.githubuser.helper.MappingHelper
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class FavoriteActivity : AppCompatActivity() {
    private lateinit var rvUser: RecyclerView
    private val title: String = "Favorite Users"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeView()
        showFavoriteList()
    }

    private fun initializeView() {
        setContentView(R.layout.activity_favorite)
        setActionBarTitle(title)
        progressBarFavorite.visibility = View.INVISIBLE
        rvUser = findViewById(R.id.rv_users_fav)
        rvUser.setHasFixedSize(true)
    }

    private fun showSelectedUser(user: User) {
        val detailedIntent = Intent(this@FavoriteActivity, DetailedActivity::class.java)
        detailedIntent.putExtra(USER, user)
        startActivity(detailedIntent)
    }

    private fun setActionBarTitle(title: String) {
        if (supportActionBar != null) {
            (supportActionBar as ActionBar).title = title
        }
    }

    private fun showFavoriteList() {
        rvUser.layoutManager = LinearLayoutManager(this)
        GlobalScope.launch(Dispatchers.Main) {
            progressBarFavorite.visibility = View.VISIBLE
            val deferredUsers = async(Dispatchers.IO) {
                val cursor = contentResolver?.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val list = deferredUsers.await()
            progressBarFavorite.visibility = View.INVISIBLE
            if (list.size > 0) {
                val listAdapter = UserAdapter(ArrayList(list.toSet()))
                rvUser.adapter = listAdapter
                listAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: User) {
                        val url = "https://api.github.com/users/${data.username}"
                        createRequest(url)
                    }
                })
            } else {
                Toast.makeText(this@FavoriteActivity, "No Favorite User", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createRequest(url: String) {
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token $token")
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                try {
                    val jsonObject = JSONObject(String(responseBody))
                    val username = jsonObject.getString("login")
                    val user = User(
                        0, jsonObject.getString("avatar_url"),
                        username, jsonObject.getString("name"),
                        jsonObject.getString("location"),
                        jsonObject.getString("company")
                    )
                    showSelectedUser(user)
                } catch (e: Exception) {
                    Toast.makeText(this@FavoriteActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@FavoriteActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
