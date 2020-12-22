package com.phoenix.consumerapp

import android.content.ContentValues
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.phoenix.consumerapp.MainActivity.Companion.USER
import com.phoenix.consumerapp.adapter.SectionsPagerAdapter
import com.phoenix.consumerapp.db.UserContract.UserColumns.Companion.COL_AVATAR
import com.phoenix.consumerapp.db.UserContract.UserColumns.Companion.COL_NAME
import com.phoenix.consumerapp.db.UserContract.UserColumns.Companion.COL_USERNAME
import com.phoenix.consumerapp.db.UserContract.UserColumns.Companion.CONTENT_URI
import com.phoenix.consumerapp.entity.User
import com.phoenix.consumerapp.helper.MappingHelper
import com.phoenix.consumerapp.helper.MappingHelper.obtainId
import kotlinx.android.synthetic.main.activity_detailed.*

class DetailedActivity : AppCompatActivity() {
    var user: User? = null
    var favoriteState : Boolean
        get() {
            val cursor = contentResolver?.query(CONTENT_URI, null, null, null, null)
            return MappingHelper.checkUsername(cursor, user?.username)
        }
        set(value) {
            val fab : FloatingActionButton = findViewById(R.id.fab_fav)
            if (value) {
                fab.setImageResource(R.drawable.baseline_favorite_white_18)
                val values = ContentValues()
                values.put(COL_USERNAME, user?.username)
                values.put(COL_AVATAR, user?.avatar)
                values.put(COL_NAME, user?.name)
                contentResolver.insert(CONTENT_URI, values)
            } else {
                fab.setImageResource(R.drawable.baseline_favorite_border_white_18)
                val cursor = contentResolver?.query(CONTENT_URI, null, null, null, null)
                val id = obtainId(cursor, COL_USERNAME, user?.username)
                val uriWithId = Uri.parse("$CONTENT_URI/$id")
                contentResolver.delete(uriWithId, null, null)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        obtainData()
        setupActionBar()
        setupAdapter()
        initializeFavoriteButton()
    }

    private fun initializeFavoriteButton() {
        fab_fav.setOnClickListener {
            Log.d("SEBELUM", favoriteState.toString())
            favoriteState = !favoriteState
            Log.d("SESUDAH", favoriteState.toString())
        }
        if (favoriteState) {
            val fab : FloatingActionButton = findViewById(R.id.fab_fav)
            fab.setImageResource(R.drawable.baseline_favorite_white_18)
        }
    }

    private fun setupActionBar() {
        val actionbar: ActionBar? = supportActionBar
        actionbar!!.title = "User Detail"
        actionbar.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
    }

    private fun setupAdapter() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        sectionsPagerAdapter.username = user?.username
        view_pager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(view_pager)
    }

    fun obtainData() {
        user = intent.getParcelableExtra(USER) as User
        val ivAvatar: ImageView = findViewById(R.id.imageView)
        val tvUsername: TextView = findViewById(R.id.txt_username)
        val tvName: TextView = findViewById(R.id.txt_name)
        val tvLocation: TextView = findViewById(R.id.txt_location_company)

        Glide.with(this).load(user?.avatar).into(ivAvatar);
        tvName.text = user?.name
        tvUsername.text = user?.username
        tvLocation.text = user?.location + " | " + user?.company
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
