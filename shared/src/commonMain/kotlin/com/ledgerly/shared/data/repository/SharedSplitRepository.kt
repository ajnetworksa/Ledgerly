package com.ledgerly.shared.data.repository

import com.ledgerly.shared.data.local.entity.SharedTransactionSplitEntity
import kotlinx.coroutines.flow.Flow

interface SharedSplitRepository {
    fun observeSplits(transactionId: Long): Flow<List<SharedTransactionSplitEntity>>
    suspend fun replaceSplits(transactionId: Long, splits: List<SharedTransactionSplitEntity>)
}
