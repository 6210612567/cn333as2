package com.example.mynotes

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.mynotes.databinding.MainActivityBinding
import com.example.mynotes.models.TaskList
import com.example.mynotes.ui.detail.ListDetailFragment
import com.example.mynotes.ui.main.MainFragment
import com.example.mynotes.ui.main.MainViewModel
import com.example.mynotes.ui.main.MainViewModelFactory

class MainActivity : AppCompatActivity(), MainFragment.MainFragmentInteractionListener {
    private lateinit var binding: MainActivityBinding
    private lateinit var viewModel: MainViewModel

    lateinit var sharedPreferences: SharedPreferences
    lateinit var listDetailEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this,
            MainViewModelFactory(PreferenceManager.getDefaultSharedPreferences(this))
        )
            .get(MainViewModel::class.java)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val mainFragment = MainFragment.newInstance()
            mainFragment.clickListener = this
            val fragmentContainerViewId: Int = if (binding.mainFragmentContainer == null){
                R.id.container }
            else{
                R.id.main_fragment_container
            }

            supportFragmentManager.commit{
                setReorderingAllowed(true)
                add(fragmentContainerViewId, mainFragment)
            }
        }
        binding.addButton.setOnClickListener{
            showCreateListDialog()
        }
    }

    private fun showCreateListDialog(){
        val dialogTitle = getString(R.string.name_of_list)
        val positiveButtonTitle = getString(R.string.create_list)

        val builder = AlertDialog.Builder(this)
        val listTitleEditText = EditText(this)
        listTitleEditText.inputType = InputType.TYPE_CLASS_TEXT

        builder.setTitle(dialogTitle)
        builder.setView(listTitleEditText)

        builder.setPositiveButton(positiveButtonTitle){ dialog, _ ->
            dialog.dismiss()
            val taskList = TaskList(listTitleEditText.text.toString())
            viewModel.saveList(TaskList(listTitleEditText.text.toString()))
            showListDetail(taskList)
        }
        builder.create().show()
    }

    private fun showListDetail(list: TaskList){
        if(binding.mainFragmentContainer == null) {
            val listDetailIntent = Intent(this, ListDetailActivity::class.java)
            listDetailIntent.putExtra(INTENT_LIST_KEY, list)
            startActivity(listDetailIntent)
        }else{
            title = list.name
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.list_detail_fragment_container, ListDetailFragment.newInstance())
                binding.addButton.setOnClickListener{
                    showCreateListDialog()
                }
            }
        }
    }

    companion object{
        const val INTENT_LIST_KEY = "list"
        var LIST_NAME = "My Notes"
    }

    override fun listItemTapped(list: TaskList) {
        LIST_NAME = list.name
        showListDetail(list)
    }

    fun LoadEditText() {
        sharedPreferences = getSharedPreferences("", MODE_PRIVATE)
        listDetailEditText = findViewById(R.id.list_editText)
        var loadText = sharedPreferences.getString(LIST_NAME,"")
        listDetailEditText.setText(loadText)

    }

    override fun onBackPressed() {
        val listDetailFragment = supportFragmentManager.findFragmentById(R.id.list_detail_fragment_container)
        if (listDetailFragment == null) {
            super.onBackPressed()
        }else{
            title = resources.getString(R.string.app_name)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                remove(listDetailFragment)
            }
            binding.addButton.setOnClickListener{
                showCreateListDialog()
            }
            sharedPreferences = getSharedPreferences("", MODE_PRIVATE)
            listDetailEditText = findViewById(R.id.list_editText)

            val edited = listDetailEditText.text.toString()
            sharedPreferences.edit().putString(LIST_NAME, edited).apply()
        }
    }
}