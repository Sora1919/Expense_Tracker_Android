package com.example.expensetrackerwithdb

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var rvExpenses: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var tvTotal: TextView

    // Room
    private lateinit var db: AppDatabase
    private lateinit var dao: ExpenseDao

    // RecyclerView list (UI list)
    private val expenses = mutableListOf<Expense>()
    private lateinit var adapter: ExpenseAdapter

    private val nf = NumberFormat.getNumberInstance(Locale.US)

    // Predefined categories
    private val categories = listOf(
        "Food",
        "Transportation",
        "Utilities",
        "Shopping",
        "Bills",
        "Health",
        "Entertainment",
        "Other"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvExpenses = findViewById(R.id.rvExpenses)
        fabAdd = findViewById(R.id.fabAdd)
        tvTotal = findViewById(R.id.tvTotal)

        // spacing between items
        val spacePx = (12 * resources.displayMetrics.density).toInt()
        rvExpenses.addItemDecoration(SpacingItemDecoration(spacePx))

        // adapter
        adapter = ExpenseAdapter(expenses) { position ->
            showEditDeleteDialog(position)
        }
        rvExpenses.adapter = adapter

        // Room init
        db = AppDatabase.getInstance(this)
        dao = db.expenseDao()

        // Observe DB -> update UI list automatically
        lifecycleScope.launch {
            dao.getAllExpense().collectLatest { list ->
                expenses.clear()
                expenses.addAll(list)
                adapter.notifyDataSetChanged()
                updateTotal()
            }
        }

        // Add new
        fabAdd.setOnClickListener {
            showAddOrEditDialog(editIndex = null)
        }
    }

    private fun updateTotal() {
        val total = expenses.sumOf { it.amount }
        tvTotal.text = "${nf.format(total)} Ks"
    }

    private fun showEditDeleteDialog(position: Int) {
        val item = expenses[position]

        AlertDialog.Builder(this)
            .setTitle(item.name)
            .setItems(arrayOf("Edit", "Delete")) { _, which ->
                when (which) {
                    0 -> showAddOrEditDialog(editIndex = position)
                    1 -> confirmDelete(position)
                }
            }
            .show()
    }

    private fun confirmDelete(position: Int) {
        val item = expenses[position]

        AlertDialog.Builder(this)
            .setTitle("Delete this expense?")
            .setMessage("This will remove the entry permanently.")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    dao.deleteExpense(item)

                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddOrEditDialog(editIndex: Int?) {
        val isEdit = editIndex != null

        val view = LayoutInflater.from(this).inflate(R.layout.dialog_expense, null)
        val etName = view.findViewById<TextInputEditText>(R.id.etName)
        val etAmount = view.findViewById<TextInputEditText>(R.id.etAmount)
        val etDate = view.findViewById<TextInputEditText>(R.id.etDate)
        val actCategory = view.findViewById<AutoCompleteTextView>(R.id.actCategory)

        // Category dropdown adapter
        actCategory.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
        )

        // Pre-fill for edit
        if (isEdit) {
            val item = expenses[editIndex!!]
            etName.setText(item.name)
            etAmount.setText(item.amount.toString())
            etDate.setText(item.date)
            actCategory.setText(item.category, false)
        }

        // Date picker
        etDate.setOnClickListener {
            openDatePicker(etDate)
        }

        AlertDialog.Builder(this)
            .setTitle(if (isEdit) "Edit Expense" else "Add Expense")
            .setView(view)
            .setPositiveButton(if (isEdit) "Update" else "Add", null) // override below
            .setNegativeButton("Cancel", null)
            .create()
            .also { dialog ->
                dialog.setOnShowListener {
                    val btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    btn.setOnClickListener {

                        val name = etName.text?.toString()?.trim().orEmpty()
                        val amountStr = etAmount.text?.toString()?.trim().orEmpty()
                        val date = etDate.text?.toString()?.trim().orEmpty()
                        val category = actCategory.text?.toString()?.trim().orEmpty()

                        val amount = amountStr.toDoubleOrNull()

                        when {
                            name.isEmpty() -> toast("Please enter a name.")
                            amount == null -> toast("Please enter a valid amount.")
                            amount < 0 -> toast("Amount cannot be negative.")
                            !isValidDateFormat(date) -> toast("Please select a valid date (YYYY-MM-DD).")
                            category.isEmpty() -> toast("Please select a category.")
                            else -> {
                                lifecycleScope.launch {
                                    if (isEdit) {
                                        val old = expenses[editIndex!!]
                                        val updated = old.copy(
                                            name = name,
                                            amount = amount,
                                            date = date,
                                            category = category
                                        )
                                        dao.updateExpense(updated)
                                    } else {
                                        dao.insertExpense(
                                            Expense(
                                                name = name,
                                                amount = amount,
                                                date = date,
                                                category = category
                                            )
                                        )
                                    }
                                }
                                dialog.dismiss()
                            }
                        }
                    }
                }
                dialog.show()
            }
    }

    private fun openDatePicker(etDate: TextInputEditText) {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            val formatted = String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)
            etDate.setText(formatted)
        }, year, month, day).show()
    }

    private fun isValidDateFormat(date: String): Boolean {
        val regex = Regex("""\d{4}-\d{2}-\d{2}""")
        return regex.matches(date)
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
