package com.example.testingsystem.models

class Test(var arr: MutableList<Task>? = null) {

    fun size(): Int? {
        return arr?.size
    }
}