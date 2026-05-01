package com.ledgerly.tracker.domain.usecase

import com.ledgerly.tracker.data.database.entity.CategoryEntity
import com.ledgerly.tracker.data.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    fun execute(): Flow<List<CategoryEntity>> {
        return categoryRepository.getAllCategories()
    }
}