package com.example.acquaintancewithrecyclerview.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.acquaintancewithrecyclerview.R
import com.example.acquaintancewithrecyclerview.databinding.ItemUserBinding
import com.example.acquaintancewithrecyclerview.model.User
import android.view.View
import android.view.Menu
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil

class UsersAdapter(private val actionListener: UserActionListener)
    : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>(), View.OnClickListener {

    var users = emptyList<User>()
        @SuppressLint("NotifyDataSetChanged")
        set(newValue) {
            val diffCallback = UserDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    class UsersViewHolder(val binding: ItemUserBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)

        binding.root.setOnClickListener(this)
        binding.userSettings.setOnClickListener(this)

        return (UsersViewHolder(binding))
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val user = users[position]
        val context = holder.itemView.context
        holder.itemView.tag = user
        with(holder.binding) {
            userSettings.tag = user
            userName.text = user.name
            userCompany.text =
                if (user.company.isBlank()) context.getString(R.string.unemployed)
                else user.company
            if (user.photo.isNotBlank()) {
                Glide.with(userPhoto.context)
                    .load(user.photo)
                    .circleCrop()
                    .placeholder(R.drawable.ic_account)
                    .error(R.drawable.ic_account)
                    .into(userPhoto)
            } else {
                userPhoto.setImageResource(R.drawable.ic_account)
            }
        }
    }

    override fun getItemCount() = users.size

    override fun onClick(view: View) {
        val user = view.tag as User

        when(view.id) {
            R.id.user_settings -> showSettingMenu(view)
            else -> actionListener.onUserDetails(user)
        }
    }

    private fun showSettingMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val context = view.context
        val user = view.tag as User
        val position = users.indexOfFirst { it.id == user.id }

        popupMenu.menu.add(0, ID_MOVE_UP, Menu.NONE, context.getString(R.string.move_up))
            .apply { isEnabled = position > 0 }
        popupMenu.menu.add(0, ID_MOVE_DOWN, Menu.NONE, context.getString(R.string.move_down))
            .apply { isEnabled = position < users.lastIndex }
        popupMenu.menu.add(0, ID_FIRE, Menu.NONE, context.getString(R.string.fire))
            .apply { isEnabled = user.company.isNotBlank() }
        popupMenu.menu.add(0, ID_REMOVE_ITEM, Menu.NONE, context.getString(R.string.remove_item))

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                ID_MOVE_UP -> actionListener.onUserMove(user, -1)
                ID_MOVE_DOWN -> actionListener.onUserMove(user, 1)
                ID_FIRE -> actionListener.onFireUser(user)
                ID_REMOVE_ITEM -> actionListener.onUserDelete(user)
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    companion object {
        private const val ID_MOVE_UP = 1
        private const val ID_MOVE_DOWN = 2
        private const val ID_REMOVE_ITEM = 3
        private const val ID_FIRE = 4
    }
}