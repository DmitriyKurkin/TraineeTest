package com.example.traineetest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.traineetest.R
import com.example.traineetest.data.model.User

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private var list = listOf<User>()
    fun submitList(newList: List<User>){
        list = newList
        notifyDataSetChanged()
    }
    class UserViewHolder(view: View): RecyclerView.ViewHolder(view){
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val id: TextView = view.findViewById(R.id.id)
        val firstName: TextView = view.findViewById(R.id.firstName)
        val lastName: TextView = view.findViewById(R.id.lastName)
        val userTag: TextView = view.findViewById(R.id.userTag)
        val department: TextView = view.findViewById(R.id.department)
        val position: TextView = view.findViewById(R.id.position)
        val birthday: TextView = view.findViewById(R.id.birthday)
        val phone: TextView = view.findViewById(R.id.phone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder{
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user,parent,false)
            return UserViewHolder(view)
    }

    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: UserViewHolder,position: Int) {
      val user = list[position]
        holder.id.text = user.id
        holder.firstName.text = user.firstName
        holder.lastName.text = user.lastName
        holder.userTag.text = user.userTag
        holder.department.text = user.department
        holder.position.text = user.position
        holder.birthday.text = user.birthday
        holder.phone.text = user.phone

        Glide.with(holder.itemView)
            .load(user.avatarUrl)
            .into(holder.avatar)
    }
}