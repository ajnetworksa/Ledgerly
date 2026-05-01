package com.ledgerly.parser.core.bank

import com.ledgerly.parser.core.TransactionType
import java.math.BigDecimal

/**
 * Parser for Riyad Bank (Saudi Arabia).
 */
class RiyadBankParser : BaseSaudiBankParser() {

    override fun getBankName() = "Riyad Bank"

    override fun canHandle(sender: String): Boolean {
        val normalized = sender.uppercase()
        return normalized.contains("RIYAD")
    }

    override fun extractTransactionType(message: String): TransactionType? {
        val lower = message.lowercase()
        return when {
            lower.contains("purchase") || lower.contains("withdrawal") -> TransactionType.EXPENSE
            lower.contains("salary") || lower.contains("transfer from") || lower.contains("incoming transfer") -> TransactionType.INCOME
            lower.contains("local transfer") && lower.contains("from:") -> TransactionType.INCOME
            lower.contains("refund") -> TransactionType.INCOME
            else -> super.extractTransactionType(message)
        }
    }

    override fun extractMerchant(message: String, sender: String): String? {
        // "From:barq"
        val fromPattern = Regex("""From\s*:\s*([^\n]+)""", RegexOption.IGNORE_CASE)
        fromPattern.find(message)?.let { match ->
            val merchant = cleanMerchantName(match.groupValues[1].trim())
            if (isValidMerchantName(merchant)) return merchant
        }

        // "Location:SRRAKA1L0345" for ATM
        val locationPattern = Regex("""Location\s*:\s*([^\n]+)""", RegexOption.IGNORE_CASE)
        locationPattern.find(message)?.let { match ->
            val location = match.groupValues[1].trim()
            return "ATM ($location)"
        }

        // "From:*0018; MOHAMMED HOSS***"
        val fromDetailedPattern = Regex("""From\s*:\s*\*?\d+;\s*([^;\n]+)""", RegexOption.IGNORE_CASE)
        fromDetailedPattern.find(message)?.let { match ->
            val merchant = cleanMerchantName(match.groupValues[1].trim())
            if (isValidMerchantName(merchant)) return merchant
        }

        return super.extractMerchant(message, sender)
    }

    override fun extractAccountLast4(message: String): String? {
        // "By:*8374;Mada"
        val byPattern = Regex("""By\s*:\s*\*?(\d{4})""", RegexOption.IGNORE_CASE)
        byPattern.find(message)?.let { return extractLast4Digits(it.groupValues[1]) }

        // "Account:*599940" -> 9940? Riyad sometimes shows more digits. We take last 4.
        val accountPattern = Regex("""Account\s*:\s*\*?(\d+)""", RegexOption.IGNORE_CASE)
        accountPattern.find(message)?.let { return extractLast4Digits(it.groupValues[1]) }

        // "From:*0018"
        val fromAccPattern = Regex("""From\s*:\s*\*?(\d{4})""", RegexOption.IGNORE_CASE)
        fromAccPattern.find(message)?.let { return extractLast4Digits(it.groupValues[1]) }

        return super.extractAccountLast4(message)
    }

    override fun isTransactionMessage(message: String): Boolean {
        val lower = message.lowercase()
        if (lower.contains("otp") || lower.contains("verification code")) return false
        
        val keywords = listOf("withdrawal", "purchase", "salary", "transfer", "amount", "sar")
        return keywords.any { lower.contains(it) }
    }
}
