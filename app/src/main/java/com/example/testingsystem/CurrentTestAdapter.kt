package com.example.testingsystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testingsystem.databinding.FragmentStudentTestsBinding
import com.example.testingsystem.databinding.SearchItemBinding
import com.example.testingsystem.databinding.TeacherCurrentTaskBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.properties.Delegates


class CurrentTestAdapter(
    private val names: List<String>,
    private val testResult: MutableMap<String, MutableList<Int>>,
    private val teacherNames: List<String>,
    private val context: Context?

) :
    RecyclerView.Adapter<CurrentTestAdapter.MyViewHolder>() {


    lateinit var auth: FirebaseAuth
    lateinit var myDatabase: DatabaseReference
    lateinit var myDatabase1: DatabaseReference
    lateinit var testNames: MutableList<String>
    lateinit var testResults: MutableList<String>
    lateinit var users: MutableSet<User>
    lateinit var searchAdapter: SearchAdapter
    lateinit var currentUser: String
    var allQuestions by Delegates.notNull<Int>()
    //lateinit var recyclerView: RecyclerView


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {



        var name: TextView? = null
        var result:TextView? = null
        var recyclerView: RecyclerView? = null


        init {
            recyclerView = itemView.findViewById(com.example.testingsystem.R.id.teacherCurrentRecyclerResult)
            name = itemView.findViewById(com.example.testingsystem.R.id.teacher_current_name)
            result = itemView.findViewById(com.example.testingsystem.R.id.result_spisok)

        }
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            Log.i("NADA", names.toString() + " " + teacherNames.toString())
            val itemView =
                LayoutInflater.from(parent.context)
                    .inflate(com.example.testingsystem.R.layout.teacher_current_task, parent, false)
            currentUser = "Teacher"
            auth = FirebaseAuth.getInstance()
            myDatabase = FirebaseDatabase.getInstance().getReference("Users")
            myDatabase1 = FirebaseDatabase.getInstance().getReference("Tasks")
            myDatabase.child("Students").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        if (it.key.toString() == auth.currentUser?.uid.toString()) {
                            Log.i("ZDES", it.key.toString())
                            currentUser = "Student"
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.name?.text = names[position]
            holder.result?.text = testResult[names[position]]?.get(0).toString() + " / " + testResult[names[position]]?.get(
                1
            ).toString()



            holder.itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {



                    Log.i("ZDES", currentUser)


                    if (currentUser == "Teacher") {
                        //Log.i("TUT312", "lala")// Если сейчас учитель
                        testNames = mutableListOf()
                        testResults = mutableListOf()
                        users = mutableSetOf()
                        val context: Context = v!!.context
                        holder.recyclerView?.layoutManager = LinearLayoutManager(context)

                        allQuestions = 0

                        myDatabase1.child(auth.currentUser!!.uid)
                            .child(holder.name?.text.toString()).child("arr")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    snapshot.children.forEach {
                                        allQuestions += 1
                                        Log.i("ABOBA", allQuestions.toString())

                                    }


                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })


                        myDatabase.child("Teachers").child(auth.currentUser?.uid.toString())
                            .child("Tests").addValueEventListener(
                                object :
                                    ValueEventListener {
                                    override fun onCancelled(error: DatabaseError) {

                                    }

                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (e in snapshot.children) {
                                            if (e.key.toString() == holder.name?.text.toString()) {


                                                for (e1 in e.children) {
                                                    testNames.add(e1.key.toString()) // добавляет имена учеников теста в список

                                                    testResults.add(e1.value.toString()) // добавляет результаты учеников теста в список


                                                    myDatabase.child("Students")
                                                        .addValueEventListener(
                                                            object : ValueEventListener {
                                                                override fun onDataChange(snapshot: DataSnapshot) {

                                                                    snapshot.children.forEach {
                                                                        if (it.key.toString() in testNames && !(it.getValue(
                                                                                User::class.java
                                                                            )!! in users)
                                                                        ) {
                                                                            users.add(
                                                                                it.getValue(
                                                                                    User::class.java
                                                                                )!!
                                                                            )
                                                                            users.last().id1 =
                                                                                it.key.toString()
                                                                        }


                                                                    }
                                                                    Log.i("TUT11", users.toString())
                                                                    var answer =
                                                                        mutableListOf<String>()
                                                                    var k = 0
                                                                    for (i in users) {
                                                                        if (k >= testResults.size) {
                                                                            break
                                                                        }
                                                                        answer.add(
                                                                            i.name + " - " + testResults.get(
                                                                                k
                                                                            ) + "/" + allQuestions.toString()
                                                                        )
                                                                        k++
                                                                    }


                                                                    Toast.makeText(
                                                                        v.getContext(),
                                                                        answer.toString(),
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                    val users1 =
                                                                        mutableListOf<User>()
                                                                    for (i in users) {
                                                                        users1.add(i)
                                                                    }
                                                                    Log.i(
                                                                        "TUT333",
                                                                        allQuestions.toString()
                                                                    )
                                                                    searchAdapter =
                                                                        SearchAdapter(users1)
                                                                    holder.recyclerView?.adapter =
                                                                        searchAdapter


                                                                }

                                                                override fun onCancelled(error: DatabaseError) {
                                                                    TODO("Not yet implemented")
                                                                }
                                                            })


                                                }
                                            }
                                        }

                                    }


                                })
                    } else {//если ученикv
                        Log.i("TUT312", "lala")
                        val a: MainActivity = context as MainActivity
                        val StudentCurrentTest = StudentCurrentTest()
                        val bundle = Bundle()

                        val binding = TeacherCurrentTaskBinding.bind(holder.itemView)
                        bundle.putString("name", binding.teacherCurrentName.text.toString())
                        Log.i("TIT321", binding.teacherCurrentName.text.toString())
                        StudentCurrentTest.arguments = bundle
                        //a.supportFragmentManager.beginTransaction().apply {
                            //replace(R.id.fragmentMain, StudentCurrentTest)
                            //commit()
                        //}
                        val teacherName = teacherNames[names.indexOf(binding.teacherCurrentName.text.toString())]
                        bundle.putString("teacherName", teacherName)
                       a.supportFragmentManager.beginTransaction().apply {


                            replace(com.example.testingsystem.R.id.fragmentMain, StudentCurrentTest)
                            commit()
                        }

                    }
                }
            })

        }


        override fun getItemCount() = names.size
}

