package com.example.acquaintancewithrecyclerview

import android.app.Application
import com.example.acquaintancewithrecyclerview.model.UserService

class App: Application() {
    val userService = UserService()
}