package com.ledgerly.shared.domain.usecase

import com.ledgerly.shared.data.local.entity.SharedUnrecognizedSmsEntity
import com.ledgerly.shared.data.repository.SharedUnrecognizedSmsRepository
import com.ledgerly.shared.data.util.currentTimeMillis

class ManageUnrecognizedSmsUseCase(
    private val repository: SharedUnrecognizedSmsRepository
) {
    suspend fun add(sender: String, smsBody: String): Long {
        val now = currentTimeMillis()
        return repository.insert(
            SharedUnrecognizedSmsEntity(
                sender = sender,
                smsBody = smsBody,
                receivedAtEpochMillis = now,
                createdAtEpochMillis = now
            )
        )
    }
}
