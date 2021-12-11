package com.example.testingsystem

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.testingsystem.databinding.SearchItemBinding
import java.util.*

class SearchAdapter(private val userList: MutableList<User>): RecyclerView.Adapter<SearchAdapter.SearchHolder>(), Filterable {
    lateinit var filterUsers:  MutableList<User>
    var addedUsers = mutableListOf<User>()

    init{
        filterUsers = userList
    }

    inner class SearchHolder(item: View): RecyclerView.ViewHolder(item) {
        val binding = SearchItemBinding.bind(item)


        fun bind(user: User){
            binding.searchName.setText(user.name)
            binding.searchClass.setText(user.special)
            binding.dobavlen.setText("Не добавлен")
            addedUsers.forEach{
                if (it.name == user.name && it.special == user.special){
                    binding.dobavlen.setText("Добавлен")
                }
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        return SearchHolder(view)
    }

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        holder.bind(filterUsers[position])
        val binding = SearchItemBinding.bind(holder.itemView)
        holder.itemView.setOnClickListener{
            with(binding){
                if (binding.dobavlen.text == "Не добавлен"){
                    val user = User(binding.searchName.text.toString(), binding.searchClass.text.toString(), "student")
                    user.id1 = userList[position].id1
                    addedUsers.add(user)
                    dobavlen.setText("Добавлен")


                }
                else{
                    addedUsers.forEach{
                        if (it.name == searchName.text.toString() && it.special == searchClass.text.toString()){
                            addedUsers.remove(it)
                            dobavlen.setText("Не добавлен")
                        }
                    }

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return filterUsers.size
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()

                if (charSearch.isEmpty()) {
                    filterUsers = userList
                } else {
                    var resultList: MutableList<User> = mutableListOf()
                    for (row in userList) {

                        if (row.name.contains(charSearch, ignoreCase = true)) {
                            resultList.add(row)
                        }
                    }
                    filterUsers = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filterUsers
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterUsers = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<User>
                notifyDataSetChanged()
            }

        }
    }

}