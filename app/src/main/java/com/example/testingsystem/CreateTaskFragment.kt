package com.example.testingsystem

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testingsystem.databinding.FragmentCreateTaskBinding
import com.example.testingsystem.models.Task
import com.example.testingsystem.models.Test
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mcdev.quantitizerlibrary.HorizontalQuantitizer
import com.mcdev.quantitizerlibrary.VerticalQuantitizer


class CreateTaskFragment : Fragment(R.layout.fragment_create_task) {


    private var _binding : FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!
    lateinit var test: Test
    lateinit var auth: FirebaseAuth
    lateinit var myDatabase: DatabaseReference
    lateinit var users: MutableList<User>
    lateinit var recyclerView: RecyclerView
    lateinit var searchAdapter: SearchAdapter

    //private var layoutManager: RecyclerView.LayoutManager? = null
    //private var adapter = RecyclerView.Adapter<SearchAdapter.SearchHolder>? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreateTaskBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()

        myDatabase = FirebaseDatabase.getInstance().getReference("Users")
        recyclerView = binding.createTaskRecycler
        recyclerView.layoutManager = LinearLayoutManager(activity)
        users = mutableListOf()

        myDatabase.child("Students").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.forEach {
                    users.add(it.getValue(User::class.java)!!)
                    users.last().id1 = it.key.toString()


                }
                searchAdapter = SearchAdapter(users)
                recyclerView.adapter = searchAdapter


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        search()







        //super.onViewCreated(view, savedInstanceState)



        test = Test(mutableListOf())
        binding.createTaskNumQuestion.text = "1/1"
        var num = 0
        binding.createTaskNext.setOnClickListener {


            if (binding.creaeteTaskQuestion.text.toString() == "") {
                Toast.makeText(activity, "Введите вопрос", Toast.LENGTH_SHORT).show()
            } else if (binding.createTaskAnswer.text.toString() == "") {
                Toast.makeText(activity, "Введите Ответ", Toast.LENGTH_SHORT).show()
            }
            else{
                var task = Task(
                    binding.creaeteTaskQuestion.text.toString(),
                    binding.createTaskAnswer.text.toString()
                )

                if(num == test.size()){
                    test.arr?.add(task)


                    num += 1

                    binding.creaeteTaskQuestion.text = null
                    binding.createTaskAnswer.text = null
                    binding.createTaskNumQuestion.text = "${num + 1}/${test.size()?.plus(1)}"
                    //hQ.value= num + 1

                }
                else{
                    if(num + 1 < test.arr?.size!!){
                        Log.i("123", "a")
                        test.arr!![num].que = binding.creaeteTaskQuestion.text.toString()
                        test.arr!![num].ans = binding.createTaskAnswer.text.toString()
                        binding.creaeteTaskQuestion.setText(test.arr?.get(num + 1)?.que)
                        binding.createTaskAnswer.setText(test.arr?.get(num + 1)?.ans)

                    }
                    else if(num + 1 == test.arr!!.size){
                        test.arr!![num].que = binding.creaeteTaskQuestion.text.toString()
                        test.arr!![num].ans = binding.createTaskAnswer.text.toString()
                        binding.creaeteTaskQuestion.setText("")
                        binding.createTaskAnswer.setText("")
                        }
                    else{
                        Log.i("123", "b")
                        binding.creaeteTaskQuestion.setText("")
                        binding.createTaskAnswer.setText("")


                    }
                    num += 1
                    binding.createTaskNumQuestion.text = "${num + 1}/${test.size()?.plus(1)}"
                    //hQ.value= num + 1

                }



            }
        }
        binding.createTaskPrevious.setOnClickListener {

            //Сюда бы еще добавить, когда user на последнем вопросе, и нажимает назад, чтобы вопрос сохранялся
            if(num == 0){
                Toast.makeText(activity, "Вы на первом вопросе", Toast.LENGTH_SHORT).show()
            }

            else {

                if (num != test.size()) {
                    test.arr!![num].que = binding.creaeteTaskQuestion.text.toString()
                    test.arr!![num].ans = binding.createTaskAnswer.text.toString()
                    }

                num -= 1
                binding.creaeteTaskQuestion.setText(test.arr?.get(num)?.que)
                binding.createTaskAnswer.setText(test.arr?.get(num)?.ans)


                binding.createTaskNumQuestion.text = "${num + 1}/${test.size()?.plus(1)}"
                    //hQ.value= num + 1


            }
        }
        binding.createTaskCreateTest.setOnClickListener {

            if (binding.creaeteTaskQuestion.text.toString() == "") {
                Toast.makeText(activity, "Введите вопрос", Toast.LENGTH_SHORT).show()
            } else if (binding.createTaskAnswer.text.toString() == "") {
                Toast.makeText(activity, "Введите Ответ", Toast.LENGTH_SHORT).show()
            }
            else if (binding.createTaskName.text.toString() == "") {
                Toast.makeText(activity, "Введите Название теста", Toast.LENGTH_SHORT).show()
            }
            else if(num == test.arr?.size){
                val task = Task(binding.creaeteTaskQuestion.text.toString(), binding.createTaskAnswer.text.toString())
                test.arr!!.add(task)
                searchAdapter.addedUsers.forEach {


                    myDatabase.child("Students").child(it.id1).child("Tests").child(auth.currentUser!!.uid).child(binding.createTaskName.text.toString()).setValue(test)
                    //students / id студента / Tests/ id учителя / название / сам тест
                    myDatabase.child("Teachers").child(auth.currentUser!!.uid).child("Tests").child(binding.createTaskName.text.toString()).child(it.id1).setValue(0)
                    // Teachers / id учителя / Tests / название / id студента / результат
                }

                val taskDatabase = FirebaseDatabase.getInstance().getReference("Tasks")
                taskDatabase.child(auth.uid.toString()).child(binding.createTaskName.text.toString()).setValue(test)
            }

        }



    }
    override fun onDestroy(){
        super.onDestroy()
        _binding = null
    }
    fun search(){
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchAdapter.filter.filter(newText)
                return false
            }

        })
    }

}