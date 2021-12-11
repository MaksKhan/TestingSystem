package com.example.testingsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testingsystem.databinding.ActivityLoginBinding
import com.example.testingsystem.models.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class LoginActivity : AppCompatActivity() {
    lateinit var button: Button
    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityLoginBinding
    lateinit var myDatabase: DatabaseReference
    lateinit var finishLogin: LoginActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        finishLogin = this

        auth = FirebaseAuth.getInstance()
        button= binding.button
        button.setOnClickListener{doo()}


        val register_button: Button = binding.toRegister
        register_button.setOnClickListener{
            val intent: Intent = Intent(this, RegisterActivity::class.java)

            startActivity(intent)
            finish()
        }



    }
    fun doo() {
        val em: EditText = binding.emailLog
        val pass: EditText = binding.pasLog
        when {
            em.text.toString() == "" -> {
                Toast.makeText(
                    this, "Введите email",
                    Toast.LENGTH_SHORT
                ).show()

            }

            pass.text.toString() == "" -> {
                Toast.makeText(
                    this, "Введите пароль.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> auth.signInWithEmailAndPassword(em.text.toString(), pass.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this, "Авторизация удалась",
                            Toast.LENGTH_SHORT
                        ).show()

                        //Это для учеников
                        myDatabase = FirebaseDatabase.getInstance().getReference("Users")
                        myDatabase.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                if(dataSnapshot.child("Students").child(auth.currentUser?.uid.toString()).getValue() != null){
                                    intent.putExtra("user", "student")
                                }
                                else if(dataSnapshot.child("Teachers").child(auth.currentUser?.uid.toString()).getValue() != null){
                                    intent.putExtra("user", "teacher")
                                }
                                startActivity(intent)
                                finish()



                            }
                            override fun onCancelled(databaseError: DatabaseError) {

                            }

                        })


                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            this, "Авторизация не удалась",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
        }
    }
}