package com.example.testingsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.testingsystem.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var myDatabase: DatabaseReference
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var current_user: String
    lateinit var name: String
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        /*if(intent.getBooleanExtra("Register", false) == true){
                user = User(
                    intent.getStringExtra("name")!!,
                    intent.getStringExtra("special")!!,
                    intent.getStringExtra("role")!!
                )
            nav_bok()

        }else{
            get_data()
        }

         */


        get_data()



    }


    fun get_data(){
        auth = FirebaseAuth.getInstance()
        current_user = intent.getStringExtra("user").toString()
        myDatabase = FirebaseDatabase.getInstance().getReference("Users")
        myDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (current_user == "student") {
                    user = dataSnapshot.child("Students").child(auth.currentUser?.uid.toString())
                        .getValue(User::class.java)!!
                } else {
                    user = dataSnapshot.child("Teachers").child(auth.currentUser?.uid.toString())
                        .getValue(User::class.java)!!
                }
                name = user.name


                nav_bok()





                supportFragmentManager.beginTransaction().apply {
                    val currentTaskFragment = CurrentTaskFragment()
                    val studentTestsFragment = StudentTestsFragment()
                    val bundle = Bundle()
                    bundle.putString("name", name)
                    currentTaskFragment.arguments = bundle
                    if(intent.getStringExtra("user") == "teacher") {
                        replace(R.id.fragmentMain, currentTaskFragment)
                    }
                    else{
                        replace(R.id.fragmentMain, studentTestsFragment)
                    }
                    commit()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    fun nav_bok() {
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val header: View = binding.navView.getHeaderView(0)
        val name: TextView =  header.findViewById(R.id.nav_header_name)
        name.setText(user.name)
        val role: TextView =  header.findViewById(R.id.nav_header_role)
        val special: TextView =  header.findViewById(R.id.nav_header_special)
        if (user.role == "student"){
            role.setText("Студент")
            special.setText("Класс: " + user.special)
        }
        else{
            role.setText("Преподаватель")
            special.setText("Предмет: " + user.special)
            binding.navView.menu.add(0,4, 0, "Создать тест")
        }



        binding.navView.setNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.item1 -> {

                    val currentTaskFragment = CurrentTaskFragment()
                    val studentTestsFragment = StudentTestsFragment()


                    supportFragmentManager.beginTransaction().apply {
                        val bundle = Bundle()
                        bundle.putString("name", user.name)
                        currentTaskFragment.arguments = bundle
                        if(intent.getStringExtra("user") == "teacher") {
                            replace(R.id.fragmentMain, currentTaskFragment)
                        }
                        else{
                            replace(R.id.fragmentMain, studentTestsFragment)
                        }
                        commit()
                    }
                }
                R.id.item3 -> {
                    this.finishAffinity()
                }
                4 ->{ val createTaskFragment = CreateTaskFragment()
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragmentMain, createTaskFragment)
                    commit()
                }
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

}
