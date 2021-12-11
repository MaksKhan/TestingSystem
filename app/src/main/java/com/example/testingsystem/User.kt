package com.example.testingsystem

import com.example.testingsystem.models.Test

class User(var name: String = "", var special: String = "", var role:String = ""){
    var id1: String = ""
    var currentTasks = mapOf<String, String>()// Учитель, название, сам тест хранится в Tests
    override fun toString(): String {
        return "User(name='$name', special='$special', role='$role', id= ${id1})"
    }
}