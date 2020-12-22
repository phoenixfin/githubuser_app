package com.phoenix.githubuser

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.phoenix.githubuser.adapter.UserAdapter
import com.phoenix.githubuser.entity.User
import com.phoenix.githubuser.fragment.SettingFragment
import com.phoenix.githubuser.receiver.AlarmReceiver
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var rvUser: RecyclerView
    private var list: ArrayList<User> = arrayListOf()
    private val title: String = "GitHub User Search"

    companion object {
        val USER: String = "USER"
        val token: String = "339315b0fc3497a12096824085e64e27e2f43585"
        private val TAG = MainActivity::class.java.simpleName
        val alarmReceiver = AlarmReceiver()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeView()
    }

    private fun initializeView() {
        setContentView(R.layout.activity_main)
        setActionBarTitle(title)
        progressBar.visibility = View.INVISIBLE
        rvUser = findViewById(R.id.rv_users)
        rvUser.setHasFixedSize(true)
    }

    private fun showSelectedUser(user: User) {
        val detailedIntent = Intent(this@MainActivity, DetailedActivity::class.java)
        detailedIntent.putExtra(USER, user)
        startActivity(detailedIntent)
    }

    private fun setActionBarTitle(title: String) {
        if (supportActionBar != null) {
            (supportActionBar as ActionBar).title = title
        }
    }

    private fun showRecyclerList() {
        rvUser.layoutManager = LinearLayoutManager(this)
        val listAdapter = UserAdapter(ArrayList(list.toSet()))
        rvUser.adapter = listAdapter
        listAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                showSelectedUser(data)
            }
        })
    }

    private fun getDetailedData(url: String) {
        fun handler(result: String):Unit {
            val jsonObject = JSONObject(result)
            val username = jsonObject.getString("login")
            val user = User(
                0, jsonObject.getString("avatar_url"),
                username, jsonObject.getString("name"),
                jsonObject.getString("location"),
                jsonObject.getString("company")
            )
            list.add(user)
            showRecyclerList()
        }
        createRequest(url, false, ::handler)
    }

    private fun getListUser(key: String) {
        fun handler (result: String):Unit {
            val obj = JSONObject(result)
            val jsonArray = obj.getJSONArray("items")
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val detail = jsonObject.getString("url")
                getDetailedData(detail)
            }
        }

        progressBar.visibility = View.VISIBLE
        val url = "https://api.github.com/search/users?q=$key"
        Log.d(TAG, "getListUser")
        createRequest(url, true, ::handler)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_action_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                list.clear()
                if (query != "") {
                    getListUser(query)
                }

                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_favorites) {
            val intent = Intent(this@MainActivity, FavoriteActivity::class.java)
            startActivity(intent)
        } else if (item.itemId == R.id.action_settings) {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }


    private fun createRequest(url: String, stopProgressBar: Boolean, successHandler: (String) -> Unit) {
        val client = AsyncHttpClient()
        Log.d(TAG, "requeset $url")
        client.addHeader("Authorization", "token $token")
        client.addHeader("User-Agent", "request")
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                try {
                    Log.d(TAG, "panggil handler")
                    successHandler(String(responseBody))
                    if (stopProgressBar) {
                        progressBar.visibility = View.INVISIBLE
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                if (stopProgressBar) {
                    progressBar.visibility = View.INVISIBLE
                }
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}