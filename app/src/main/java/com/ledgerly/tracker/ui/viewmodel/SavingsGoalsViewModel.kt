package com.ledgerly.tracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledgerly.tracker.data.database.entity.SavingsGoalEntity
import com.ledgerly.tracker.data.repository.SavingsGoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavingsGoalsViewModel @Inject constructor(
    private val repository: SavingsGoalRepository
) : ViewModel() {

    val goals: StateFlow<List<SavingsGoalEntity>> = repository.getAllGoals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createGoal(name: String, targetAmount: Double, currency: String, color: String, targetDate: Long?) {
        viewModelScope.launch {
            repository.createGoal(
                SavingsGoalEntity(
                    name = name,
                    targetAmount = targetAmount,
                    currency = currency,
                    color = color,
                    targetDate = targetDate
                )
            )
        }
    }

    fun addContribution(id: Long, amount: Double) {
        viewModelScope.launch {
            repository.addContribution(id, amount)
        }
    }

    fun deleteGoal(goal: SavingsGoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }
}
