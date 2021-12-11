package com.example.testingsystem

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.testingsystem.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    lateinit var auth: FirebaseAuth
    lateinit var button_register: Button
    lateinit var myDatabse: DatabaseReference
    lateinit var switch: Switch


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        myDatabse = FirebaseDatabase.getInstance().getReference("Users")





        button_register = binding.register
        button_register.setOnClickListener { register_student() }


        val teacherFragment = teacher_reg()
        val studentFragment = student_reg()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, studentFragment)
            commit()
        }
        switch = binding.switch1
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                switch.text = "Учитель"
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, teacherFragment)
                    commit()
                }
            } else {
                switch.text = "Ученик"
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment, studentFragment)
                    commit()
                }
            }
        }

    }

    fun register_student() {

        val em: EditText = binding.emailReg
        val pass: EditText = binding.pasReg
        val name: EditText = binding.nameReg
        val surname: EditText = binding.surnameReg


        when {
            name.text.toString() == "" -> {
                println("Тут")
                Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show()
                println("Тут1")
            }
            surname.text.toString() == "" -> {
                Toast.makeText(this, "Введите Фамилию", Toast.LENGTH_SHORT).show()
            }
            em.text.toString() == "" -> {
                Toast.makeText(this, "Введите почту", Toast.LENGTH_SHORT).show()
            }
            pass.text.toString() == "" -> {
                Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show()
            }
            else -> auth.createUserWithEmailAndPassword(em.text.toString(), pass.text.toString())
                .addOnCompleteListener(
                    this@RegisterActivity,
                    OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@RegisterActivity, "Успешно!", Toast.LENGTH_SHORT)
                                .show()


                            val id = auth.currentUser?.uid.toString()


                            lateinit var subject: Spinner
                            lateinit var num_class: Spinner
                            lateinit var let_class: Spinner

                            lateinit var special: String

                            val intent = Intent(this, LoginActivity::class.java)

                            if (switch.isChecked) {
                                subject = findViewById(R.id.spinner_teach)
                                val user = User(name.text.toString() + " " + surname.text.toString(), subject.selectedItem.toString(), "teacher")
                                myDatabse.child("Teachers").child(id)
                                    .setValue(
                                        user
                                    )
                                special = subject.selectedItem.toString()
                                //intent.putExtra("role", "teacher")



                            } else {
                                num_class = findViewById(R.id.spinner_num)
                                let_class = findViewById(R.id.spinner_let)
                                val user = User(
                                    name.text.toString() + " " + surname.text.toString(),
                                    num_class.selectedItem.toString() +
                                    let_class.selectedItem.toString(), "student"
                                )


                                myDatabse.child("Students").child(id).setValue(user)
                                val classDatabse = FirebaseDatabase.getInstance().getReference("Classes")
                                classDatabse.child(num_class.selectedItem.toString() + let_class.selectedItem.toString()).child(id).setValue(id)
                                special = num_class.selectedItem.toString() + let_class.selectedItem.toString()
                                //intent.putExtra("role", "student")





                            }
                            /*intent.putExtra("Register", true)
                            intent.putExtra("name", (name.text.toString() + surname.text.toString()))
                            intent.putExtra("special", special)

                             */
                            startActivity(intent)
                            finish()



                        } else {
                            if (task.exception is FirebaseAuthUserCollisionException) {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Почта уже используется!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
        }

    }
}

