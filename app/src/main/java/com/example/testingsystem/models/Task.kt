package com.example.testingsystem.models

class Task(_que: String,  _ans: String) {
    var que = _que
    var ans = _ans
    override fun toString(): String {
        return "Task(que='$que', ans='$ans')"
    }
}