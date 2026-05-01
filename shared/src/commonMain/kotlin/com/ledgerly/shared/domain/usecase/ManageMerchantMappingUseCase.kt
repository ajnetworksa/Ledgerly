package com.ledgerly.shared.domain.usecase

import com.ledgerly.shared.data.local.entity.SharedMerchantMappingEntity
import com.ledgerly.shared.data.repository.SharedMerchantMappingRepository
import com.ledgerly.shared.data.util.currentTimeMillis

class ManageMerchantMappingUseCase(
    private val repository: SharedMerchantMappingRepository
) {
    suspend fun map(merchantName: String, category: String) {
        val now = currentTimeMillis()
        repository.upsert(
            SharedMerchantMappingEntity(
                merchantName = merchantName.trim(),
                category = category.trim(),
                createdAtEpochMillis = now,
                updatedAtEpochMillis = now
            )
        )
    }
}
