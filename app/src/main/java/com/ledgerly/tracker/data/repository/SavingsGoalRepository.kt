package com.ledgerly.tracker.data.repository

import com.ledgerly.tracker.data.database.dao.SavingsGoalDao
import com.ledgerly.tracker.data.database.entity.SavingsGoalEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavingsGoalRepository @Inject constructor(
    private val savingsGoalDao: SavingsGoalDao
) {
    fun getAllGoals(): Flow<List<SavingsGoalEntity>> = savingsGoalDao.getAllGoals()

    suspend fun getGoalById(id: Long): SavingsGoalEntity? {
        return savingsGoalDao.getGoalById(id)
    }

    suspend fun createGoal(goal: SavingsGoalEntity): Long {
        return savingsGoalDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: SavingsGoalEntity) {
        savingsGoalDao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: SavingsGoalEntity) {
        savingsGoalDao.deleteGoal(goal)
    }

    suspend fun addContribution(id: Long, amount: Double) {
        savingsGoalDao.addContribution(id, amount)
    }
}
