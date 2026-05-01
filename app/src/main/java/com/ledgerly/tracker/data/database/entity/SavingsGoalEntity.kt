package com.ledgerly.tracker.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val currency: String,
    val targetDate: Long? = null,
    val color: String = "#FF6B6B", // Default color
    val iconName: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
