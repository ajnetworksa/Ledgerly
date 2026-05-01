package com.ledgerly.shared.domain.usecase

import com.ledgerly.shared.data.local.entity.SharedExchangeRateEntity
import com.ledgerly.shared.data.repository.SharedExchangeRateRepository
import com.ledgerly.shared.data.util.currentTimeMillis

class ManageExchangeRateUseCase(
    private val repository: SharedExchangeRateRepository
) {
    suspend fun upsertRate(
        fromCurrency: String,
        toCurrency: String,
        rateMicros: Long,
        ttlMillis: Long = 24L * 60L * 60L * 1000L
    ) {
        val now = currentTimeMillis()
        repository.upsert(
            SharedExchangeRateEntity(
                fromCurrency = fromCurrency.uppercase(),
                toCurrency = toCurrency.uppercase(),
                rateMicros = rateMicros,
                provider = "manual",
                updatedAtEpochMillis = now,
                expiresAtEpochMillis = now + ttlMillis
            )
        )
    }
}
