package com.phoenix.githubuser.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.phoenix.githubuser.R
import com.phoenix.githubuser.entity.User
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val users: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.ListViewHolder>() {
    interface OnItemClickCallback {
        fun onItemClicked(data: User) {
        }
    }
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_user, viewGroup, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user = users[position]
        Glide.with(holder.itemView.context)
            .load(user.avatar)
            .apply(RequestOptions().override(350, 550))
            .into(holder.imgPhoto)

        holder.txtName.text = user.name
        holder.txtUsername.text = user.username
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(users[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun getItem(i: Int): Any {
        return users[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }


    inner class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtName: TextView = view.findViewById(R.id.txt_name)
        var txtUsername: TextView = view.findViewById(R.id.txt_username)
        var imgPhoto: CircleImageView = view.findViewById(R.id.img_photo)

    }

}