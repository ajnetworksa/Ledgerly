package com.ledgerly.tracker.data.mapper

import com.ledgerly.parser.core.ParsedTransaction
import com.ledgerly.tracker.core.Constants
import com.ledgerly.tracker.data.database.entity.TransactionEntity
import com.ledgerly.tracker.data.database.entity.TransactionType
import com.ledgerly.shared.domain.mapping.SharedCategoryMapping
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Maps ParsedTransaction from parser-core to TransactionEntity
 */
fun ParsedTransaction.toEntity(): TransactionEntity {
    val dateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(timestamp),
        ZoneId.systemDefault()
    )

    // Normalize merchant name to proper case
    val normalizedMerchant = merchant?.let { normalizeMerchantName(it) }

    // Map TransactionType from parser-core to database entity
    val entityType = when (type) {
        com.ledgerly.parser.core.TransactionType.INCOME -> TransactionType.INCOME
        com.ledgerly.parser.core.TransactionType.EXPENSE -> TransactionType.EXPENSE
        com.ledgerly.parser.core.TransactionType.CREDIT -> TransactionType.CREDIT
        com.ledgerly.parser.core.TransactionType.TRANSFER -> TransactionType.TRANSFER
        com.ledgerly.parser.core.TransactionType.INVESTMENT -> TransactionType.INVESTMENT
        com.ledgerly.parser.core.TransactionType.BALANCE_UPDATE -> TransactionType.EXPENSE
    }

    return TransactionEntity(
        id = 0, // Auto-generated
        amount = amount,
        merchantName = normalizedMerchant ?: "Unknown Merchant",
        category = determineCategory(merchant, entityType),
        transactionType = entityType,
        dateTime = dateTime,
        description = null,
        smsBody = smsBody,
        bankName = bankName,
        smsSender = sender,
        accountNumber = accountLast4,
        balanceAfter = balance,
        transactionHash = transactionHash?.takeIf { it.isNotBlank() } ?: generateTransactionId(),
        isRecurring = false, // Will be determined later
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        currency = currency,
        fromAccount = fromAccount,
        toAccount = toAccount,
        reference = reference
    )
}

/**
 * Normalizes merchant name to consistent format.
 * Converts all-caps to proper case, preserves already mixed case.
 */
private fun normalizeMerchantName(name: String): String {
    val trimmed = name.trim()

    // If it's all uppercase, convert to proper case
    return if (trimmed == trimmed.uppercase()) {
        trimmed.lowercase().split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase() }
        }
    } else {
        // Already has mixed case, keep as is
        trimmed
    }
}

/**
 * Determines the category based on merchant name and transaction type.
 * Delegates to SharedCategoryMapping (single source of truth).
 */
private fun determineCategory(merchant: String?, type: TransactionType): String {
    val merchantName = merchant ?: return "Others"
    return SharedCategoryMapping.determineCategory(merchantName, type.name)
}

/**
 * Extension to map parser-core TransactionType to database entity TransactionType
 */
fun com.ledgerly.parser.core.TransactionType.toEntityType(): TransactionType {
    return when (this) {
        com.ledgerly.parser.core.TransactionType.INCOME -> TransactionType.INCOME
        com.ledgerly.parser.core.TransactionType.EXPENSE -> TransactionType.EXPENSE
        com.ledgerly.parser.core.TransactionType.CREDIT -> TransactionType.CREDIT
        com.ledgerly.parser.core.TransactionType.TRANSFER -> TransactionType.TRANSFER
        com.ledgerly.parser.core.TransactionType.INVESTMENT -> TransactionType.INVESTMENT
        com.ledgerly.parser.core.TransactionType.BALANCE_UPDATE -> TransactionType.EXPENSE
    }
}