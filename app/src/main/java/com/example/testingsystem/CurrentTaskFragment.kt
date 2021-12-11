package com.example.testingsystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testingsystem.databinding.FragmentCurrentTaskBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class CurrentTaskFragment : Fragment(R.layout.fragment_current_task) {

    lateinit var auth: FirebaseAuth
    lateinit var myDatabase: DatabaseReference

    private var _binding : FragmentCurrentTaskBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        auth = FirebaseAuth.getInstance()
        myDatabase = FirebaseDatabase.getInstance().getReference("Users")
        _binding = FragmentCurrentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)

        var testNames = mutableListOf<String>()
        var testResult = mutableMapOf("123" to mutableListOf(1, 2))
        myDatabase.child("Teachers").child(auth.currentUser?.uid.toString()).child("Tests").addValueEventListener(
            object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (e in snapshot.children) {
                        testNames.add(e.key.toString())
                        var not_zeroes = 0
                        var al = 0
                        for (i in e.children){
                            if (i.value.toString() != "0"){
                                not_zeroes += 1
                            }
                            al += 1
                        }
                        testResult[e.key.toString()] = ( mutableListOf(not_zeroes, al))

                    }
                    val recyclerView: RecyclerView = binding.teacherCurrentRecycler

                    val teacherNames = mutableListOf<String>();
                    recyclerView.layoutManager = LinearLayoutManager(activity) //в адаптере для учеников нужно имя учителя, который создал тест, а для учителей не надо
                    recyclerView.adapter = CurrentTestAdapter(testNames, testResult,teacherNames, context)



                }


            })


    }
    override fun onDestroy(){
        super.onDestroy()
        _binding = null
    }
}