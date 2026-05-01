package com.ledgerly.shared.data.statement

import com.ledgerly.shared.data.model.SharedTransactionType

data class SharedParsedStatementTransaction(
    val amountMinor: Long,
    val transactionType: SharedTransactionType,
    val merchant: String?,
    val reference: String?,
    val accountLast4: String?,
    val bankName: String?,
    val timestampEpochMillis: Long,
    val rawText: String
)
