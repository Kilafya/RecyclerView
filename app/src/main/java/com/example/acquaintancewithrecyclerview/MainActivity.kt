package com.example.acquaintancewithrecyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.acquaintancewithrecyclerview.adapter.UserActionListener
import com.example.acquaintancewithrecyclerview.adapter.UsersAdapter
import com.example.acquaintancewithrecyclerview.databinding.ActivityMainBinding
import com.example.acquaintancewithrecyclerview.model.User
import com.example.acquaintancewithrecyclerview.model.UserListener
import com.example.acquaintancewithrecyclerview.model.UserService

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAdapter: UsersAdapter

    private val usersService: UserService
        get() = (applicationContext as App).userService

    private val usersListener: UserListener = {
        mAdapter.users = it
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val mLayoutManager = LinearLayoutManager(this)
        mAdapter = UsersAdapter(object: UserActionListener {
            override fun onUserMove(user: User, moveBy: Int) {
                usersService.moveUser(user, moveBy)
            }

            override fun onUserDelete(user: User) {
                usersService.deleteUser(user)
            }

            override fun onFireUser(user: User) {
                usersService.fireUser(user)
            }

            override fun onUserDetails(user: User) {
                Toast.makeText(this@MainActivity, "User: ${user.name}", Toast.LENGTH_LONG).show()
            }

        })
        mBinding.userList.layoutManager = mLayoutManager
        mBinding.userList.adapter = mAdapter

        usersService.addListener(usersListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        usersService.removeListener(usersListener)
    }
}