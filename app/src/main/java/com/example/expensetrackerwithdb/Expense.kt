package com.example.expensetrackerwithdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int =0,
    var name: String,
    var amount: Double,
    var date: String,
    var category: String
)