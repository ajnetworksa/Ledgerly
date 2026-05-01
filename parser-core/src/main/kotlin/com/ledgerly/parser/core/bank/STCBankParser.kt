package com.ledgerly.parser.core.bank

import com.ledgerly.parser.core.TransactionType
import java.math.BigDecimal

/**
 * Parser for STC Bank (Saudi Arabia).
 */
class STCBankParser : BaseSaudiBankParser() {

    override fun getBankName() = "STC Bank"

    override fun canHandle(sender: String): Boolean {
        val normalized = sender.uppercase().replace(Regex("[\\s\\-_]"), "")
        return normalized.contains("STCBANK") || normalized == "STC" || normalized == "STCPAY"
    }

    override fun extractTransactionType(message: String): TransactionType? {
        val lower = message.lowercase()
        return when {
            lower.contains("purchase") -> TransactionType.EXPENSE
            lower.contains("withdrawal") || lower.contains("withdraw") -> TransactionType.EXPENSE
            lower.contains("payment") -> TransactionType.EXPENSE
            lower.contains("debit") -> TransactionType.EXPENSE
            lower.contains("transfer out") || lower.contains("sent to") -> TransactionType.EXPENSE
            lower.contains("refund") -> TransactionType.INCOME
            lower.contains("incoming transfer") || lower.contains("deposit") -> TransactionType.INCOME
            lower.contains("credit") && !lower.contains("credit card") -> TransactionType.INCOME
            lower.contains("received") -> TransactionType.INCOME
            else -> super.extractTransactionType(message)
        }
    }

    override fun extractMerchant(message: String, sender: String): String? {
        // "At: Panda" or "At: NESTO"
        val atPattern = Regex("""At\s*:\s*([^\n]+?)(?:\n|Date\s*:|$)""", RegexOption.IGNORE_CASE)
        atPattern.find(message)?.let { match ->
            val merchant = cleanMerchantName(match.groupValues[1].trim())
            if (isValidMerchantName(merchant)) return merchant
        }

        // "From: TABBY"
        val fromPattern = Regex("""From\s*:\s*([^\n]+?)(?:\n|Card\s*:|Date\s*:|$)""", RegexOption.IGNORE_CASE)
        fromPattern.find(message)?.let { match ->
            val merchant = cleanMerchantName(match.groupValues[1].trim())
            if (isValidMerchantName(merchant)) return merchant
        }

        // "To: RECIPIENT"
        val toPattern = Regex("""To\s*:\s*([^\n]+?)(?:\n|Date\s*:|$)""", RegexOption.IGNORE_CASE)
        toPattern.find(message)?.let { match ->
            val merchant = cleanMerchantName(match.groupValues[1].trim())
            if (isValidMerchantName(merchant)) return merchant
        }

        return super.extractMerchant(message, sender)
    }

    override fun extractAccountLast4(message: String): String? {
        // "Card: *6591" or "Card:***6591"
        val cardPattern = Regex("""Card\s*:\s*\*+(\d{4})""", RegexOption.IGNORE_CASE)
        cardPattern.find(message)?.let { return extractLast4Digits(it.groupValues[1]) }

        // "Acc:5433*"
        val accPattern = Regex("""Acc\s*:\s*(\d{4})\*""", RegexOption.IGNORE_CASE)
        accPattern.find(message)?.let { return extractLast4Digits(it.groupValues[1]) }

        // Legacy patterns
        val starPattern = Regex("""\*+(\d{4})\b""")
        starPattern.find(message)?.let { return extractLast4Digits(it.groupValues[1]) }

        val viaPattern = Regex("""Via\s*:\s*(\d{4})""", RegexOption.IGNORE_CASE)
        viaPattern.find(message)?.let { return extractLast4Digits(it.groupValues[1]) }

        return super.extractAccountLast4(message)
    }

    override fun isTransactionMessage(message: String): Boolean {
        val lower = message.lowercase()
        if (lower.contains("otp") || lower.contains("verification code")) return false
        
        val keywords = listOf("purchase", "transfer", "refund", "deposit", "payment", "withdraw", "sar")
        return keywords.any { lower.contains(it) }
    }
}
