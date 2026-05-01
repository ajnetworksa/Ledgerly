package com.ledgerly.shared.data.bootstrap

import com.ledgerly.shared.data.repository.SharedCategoryRepository
import com.ledgerly.shared.data.util.currentTimeMillis

class SharedDataInitializer(
    private val categoryRepository: SharedCategoryRepository
) {
    suspend fun seedDefaultCategoriesIfNeeded() {
        if (categoryRepository.countCategories() > 0) return
        categoryRepository.insertCategories(
            DefaultSharedCategories.create(
                nowEpochMillis = currentTimeMillis()
            )
        )
    }
}
