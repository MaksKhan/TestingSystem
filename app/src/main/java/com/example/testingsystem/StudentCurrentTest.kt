package com.example.testingsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testingsystem.databinding.FragmentCreateTaskBinding
import com.example.testingsystem.databinding.FragmentStudentCurrentTestBinding
import com.example.testingsystem.models.Task
import com.example.testingsystem.models.Test
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class StudentCurrentTest : Fragment() {
    private var _binding : FragmentStudentCurrentTestBinding? = null
    private val binding get() = _binding!!
    lateinit var test: Test
    lateinit var auth: FirebaseAuth
    lateinit var myDatabase: DatabaseReference
    lateinit var myDatabaseTest: DatabaseReference
    lateinit var users: MutableList<User>
    lateinit var questions: MutableList<String>
    lateinit var answers: MutableList<String>
    lateinit var checkAnswers: MutableList<Boolean>
    lateinit var userAnswers: Array<String>

    lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStudentCurrentTestBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        val bundle = arguments

        myDatabase = FirebaseDatabase.getInstance().getReference("Users")
        myDatabaseTest = FirebaseDatabase.getInstance().getReference("Tasks").child(bundle?.getString("teacherName").toString()).child(bundle?.getString("name").toString()).child("arr")


        questions = mutableListOf<String>()
        answers = mutableListOf()

        myDatabaseTest.addValueEventListener(
            object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                        for (e in snapshot.children){
                            questions.add(e.child("que").value.toString())
                            answers.add(e.child("ans").value.toString())
                            binding.currentTaskNumQuestion.setText("1 /" + questions.size)
                            binding.creaeteTaskQuestion.setText(questions.get(0))
                            userAnswers = Array(questions.size, {""})
                            checkAnswers = Array(questions.size, {false}).toMutableList()
                            binding.createTaskName.setText(bundle?.getString("name") )



                    }

                }

            }
        )


        binding.createTaskNext.setOnClickListener {
            val current = binding.currentTaskNumQuestion.text.get(0).toInt() - '0'.toInt().toInt()
            val answer = binding.createTaskAnswer.text.toString()
            Log.i("tit", current.toString())

            if (current >= questions.size){
                Toast.makeText(activity, "Вы на последнем вопросе", Toast.LENGTH_SHORT).show()
            }
            else {
                binding.createTaskAnswer.setText(userAnswers[current]) // если что эту сверху к иф
                binding.createTaskAnswer.setText(userAnswers[current])
                binding.creaeteTaskQuestion.setText(
                    questions.get(current)
                )
                Log.i("ALALALA", answer  )
                userAnswers[current - 1] = answer
                Log.i("P", (current - 1).toString() + " " + answer)
                binding.currentTaskNumQuestion.setText(
                    (current + 1).toString() + "/" + questions.size
                )
                for (i in userAnswers){
                    Log.i("ABOBA", i +" userAnswers" )
                }

            }
        }
        binding.createTaskPrevious.setOnClickListener {
            val current = binding.currentTaskNumQuestion.text.get(0).toInt() - '0'.toInt().toInt()
            val answer = binding.createTaskAnswer.text.toString()
            if (current == 1){
                Toast.makeText(activity, "Вы на первом вопросе", Toast.LENGTH_SHORT).show()
            }
            else {
                binding.createTaskAnswer.setText(userAnswers[current - 2]) // если что эту перенести сверху в иф
                Log.i("asd", current.toString())
                binding.creaeteTaskQuestion.setText(
                    questions.get(current - 2)
                )
                userAnswers[current - 1] = answer // возможно тут 1
                binding.currentTaskNumQuestion.setText(
                    (current - 1).toString() + "/" + questions.size
                )
                for (i in userAnswers){
                    Log.i("ABOBA", i + " userAnswers" )
                }

            }
        }

        binding.currentTaskFinal.setOnClickListener {
            val current = binding.currentTaskNumQuestion.text.get(0).toInt() - '0'.toInt().toInt()
            for (i in userAnswers){
                Log.i("BEBRA", i  )
            }

            userAnswers[current - 1] = binding.createTaskAnswer.text.toString()
            var k = 0;
            for (i in 0 until userAnswers.size){
                if(userAnswers[i].toLowerCase() == answers[i].toLowerCase()){
                    k++;
                }
            }
            myDatabase.child("Teachers").child(bundle?.getString("teacherName").toString()).child("Tests").child(bundle?.getString("name").toString()).child(
                auth.currentUser?.uid.toString()).setValue(k)

            Toast.makeText(activity, "Результат - " + k.toString() + " из " + questions.size, Toast.LENGTH_SHORT).show()
        }



        users = mutableListOf()



    }
    override fun onDestroy(){
        super.onDestroy()
    }


}