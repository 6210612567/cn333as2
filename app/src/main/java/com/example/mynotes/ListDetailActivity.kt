package com.example.mynotes

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.example.mynotes.models.TaskList
import com.example.mynotes.ui.detail.ListDetailFragment

class ListDetailActivity : AppCompatActivity() {
    lateinit var list: TaskList
    lateinit var sharedPreferences: SharedPreferences
    lateinit var listDetailEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_detail_activity)
        list = intent.getParcelableExtra(MainActivity.INTENT_LIST_KEY)!!
        title = list.name

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ListDetailFragment.newInstance())
                .commitNow()
        }
    }

    override fun onBackPressed() {
        sharedPreferences = getSharedPreferences("", MODE_PRIVATE)
        listDetailEditText = findViewById(R.id.list_editText)

        val edited = listDetailEditText.text.toString()

        listDetailEditText.setText(edited)
        sharedPreferences.edit().putString(list.name, edited).apply()

        super.onBackPressed()
    }

    public override fun onPostCreate(savedInstanceState: Bundle?) {

        sharedPreferences = getSharedPreferences("", MODE_PRIVATE)
        listDetailEditText = findViewById(R.id.list_editText)
        val loadText = sharedPreferences.getString(list.name,"")
        listDetailEditText.setText(loadText)

        super.onPostCreate(savedInstanceState)
    }
}