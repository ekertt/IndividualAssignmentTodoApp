package com.example.individualtodolist.activities

import activities.BaseActivity
import android.app.DatePickerDialog
import com.example.individualtodolist.adapters.ToDoListItemsAdapter
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.individualtodolist.R
import com.example.individualtodolist.firebase.FirestoreClass
import kotlinx.android.synthetic.main.activity_todo_list.*
import com.example.individualtodolist.models.Category
import com.example.individualtodolist.models.ToDo
import com.example.individualtodolist.utils.Constants
import java.util.*

class ToDoListActivity : BaseActivity() {
    private lateinit var mCategoryDetails: Category

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        var categoryDocumentId = ""
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            categoryDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)
        }
        FirestoreClass().getCategoryDetails(this, categoryDocumentId)

        et_pick_date.setOnClickListener {
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                tv_date.setText("$mDay/$mMonth/$mYear")
            }, year, month, day)

            dpd.show()
        }

        ib_done_list_name.setOnClickListener {
            val listName = et_todo_list_name.text.toString()

            val listDate = tv_date.text.toString()

            if (listName.isNotEmpty() && listDate.isNotEmpty()) {
                createToDoList(listName, listDate)
            }
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_todo_list_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mCategoryDetails.name
        }
        toolbar_todo_list_activity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun categoryDetails(category: Category) {
        mCategoryDetails = category

        setUpActionBar()

        rv_todo_list.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL, false
        )
        rv_todo_list.setHasFixedSize(true)

        val adapter = ToDoListItemsAdapter(this, category.todoList)
        rv_todo_list.adapter = adapter
    }

    fun addUpdateToDoListSuccess() {
        FirestoreClass().getCategoryDetails(this, mCategoryDetails.documentId)
    }

    private fun createToDoList(todoListName: String, todoListDate: String) {
        val todo = ToDo(todoListName, todoListDate, FirestoreClass().getCurrentUserID())
        mCategoryDetails.todoList.add(mCategoryDetails.todoList.size, todo)

        FirestoreClass().addUpdateToDoList(this, mCategoryDetails)
    }
}