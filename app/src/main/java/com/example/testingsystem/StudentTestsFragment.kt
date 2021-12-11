package com.example.testingsystem

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testingsystem.databinding.FragmentStudentTestsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class StudentTestsFragment : Fragment(R.layout.fragment_student_tests) {


    lateinit var auth: FirebaseAuth
    lateinit var myDatabase: DatabaseReference

    private var _binding :FragmentStudentTestsBinding? = null
    private val binding get() = _binding!!




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        auth = FirebaseAuth.getInstance()
        myDatabase = FirebaseDatabase.getInstance().getReference("Users")
        _binding = FragmentStudentTestsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        var testNames = mutableListOf<String>()
        var teacherNames = mutableListOf<String>()
        var testResult = mutableMapOf("123" to mutableListOf(1, 2))
        myDatabase.child("Students").child(auth.currentUser?.uid.toString()).child("Tests").addValueEventListener(
            object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (e in snapshot.children) {
                        var a = e.value.toString()
                        a = a.substringBefore('=')
                        a = a.substringAfter('{')
                        testNames.add(a)
                        teacherNames.add(e.key.toString())
                        testResult[e.key.toString()] = ( mutableListOf(0, 0))


                        var k = 0
                        for(i in  e.child(a).child("arr").value.toString()){
                            if (i == '{') k++
                        }
                        Log.i("TUT123",
                            k.toString())
                        testResult[a] = mutableListOf(0, k)




                    }

                    val recyclerView: RecyclerView = binding.studentCurrentRecycler
                    recyclerView.layoutManager = LinearLayoutManager(activity)
                    recyclerView.adapter = CurrentTestAdapter(testNames, testResult, teacherNames, context)
                }
                }
        )




    }
    override fun onDestroy(){
        super.onDestroy()

    }
}



