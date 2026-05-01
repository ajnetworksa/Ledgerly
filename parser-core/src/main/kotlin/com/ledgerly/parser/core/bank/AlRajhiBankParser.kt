package com.ledgerly.parser.core.bank

import com.ledgerly.parser.core.TransactionType
import java.math.BigDecimal

/**
 * Parser for Al Rajhi Bank (Saudi Arabia) SMS messages.
 */
class AlRajhiBankParser : BaseSaudiBankParser() {

    override fun getBankName() = "Al Rajhi Bank"

    override fun canHandle(sender: String): Boolean {
        val normalized = sender.uppercase()
        return normalized.contains("ALRAJHI") ||
                normalized.contains("RAJHI") ||
                sender.contains("الراجحي")
    }

    override fun extractTransactionType(message: String): TransactionType? {
        return when {
            // Incoming (واردة = incoming)
            message.contains("واردة") -> TransactionType.INCOME
            message.contains("إيداع") -> TransactionType.INCOME

            // Expense types
            message.contains("شراء") -> TransactionType.EXPENSE           // purchase
            message.contains("سحب") -> TransactionType.EXPENSE            // withdrawal
            message.contains("صادرة") -> TransactionType.EXPENSE          // outgoing
            message.contains("خصم") -> TransactionType.EXPENSE            // deduction
            message.contains("سداد") -> TransactionType.EXPENSE           // payment/settlement

            else -> super.extractTransactionType(message)
        }
    }

    override fun extractMerchant(message: String, sender: String): String? {
        // Pattern 1: "لـMERCHANT" (to/for merchant)
        val toPattern = Regex("""لـ([^\n*]+?)(?:\n|\d{2}/\d|$)""")
        toPattern.find(message)?.let { match ->
            val raw = match.groupValues[1].trim()
            if (!raw.all { it == '*' || it.isDigit() || it == ';' || it.isWhitespace() }) {
                val merchant = if (raw.contains(";")) {
                    cleanMerchantName(raw.substringAfter(";").trim())
                } else {
                    cleanMerchantName(raw)
                }
                if (isValidMerchantName(merchant)) return merchant
            }
        }

        // Pattern 2: "الى:MERCHANT"
        val toColonPattern = Regex("""الى:([^\n]+?)(?:\n|الى:|الرسوم:|$)""")
        toColonPattern.find(message)?.let { match ->
            val raw = match.groupValues[1].trim()
            if (!raw.all { it == '*' || it.isDigit() }) {
                val merchant = cleanMerchantName(raw)
                if (isValidMerchantName(merchant)) return merchant
            }
        }

        // Pattern 3: "مكان السحب:LOCATION"
        val atmPattern = Regex("""مكان السحب:([^\n]+?)(?:\n|$)""")
        atmPattern.find(message)?.let { match ->
            val merchant = cleanMerchantName(match.groupValues[1].trim())
            if (isValidMerchantName(merchant)) return merchant
        }

        // Pattern 4: "من:SENDER"
        val fromPattern = Regex("""من:([^\n*]+?)(?:\n|\d{2}/\d|$)""")
        fromPattern.find(message)?.let { match ->
            val raw = match.groupValues[1].trim()
            if (raw.isNotBlank() && !raw.all { it == '*' || it.isDigit() }) {
                val merchant = cleanMerchantName(raw)
                if (isValidMerchantName(merchant)) return merchant
            }
        }

        if (message.contains("صراف آلي")) return "ATM Withdrawal"

        return super.extractMerchant(message, sender)
    }

    override fun extractBalance(message: String): BigDecimal? {
        // "المبلغ المتبقي: SAR 13827.48"
        val remainingPattern = Regex("""المبلغ المتبقي:\s*SAR\s+([0-9,]+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE)
        remainingPattern.find(message)?.let { match ->
            return try {
                BigDecimal(match.groupValues[1].replace(",", ""))
            } catch (e: Exception) {
                null
            }
        }
        return super.extractBalance(message)
    }

    override fun detectIsCard(message: String): Boolean {
        if (message.contains("مدى") || message.contains("بطاقة")) return true
        return super.detectIsCard(message)
    }

    override fun isTransactionMessage(message: String): Boolean {
        if (message.contains("رمز") || message.contains("OTP", ignoreCase = true)) return false
        val keywords = listOf("شراء", "سحب", "حوالة", "خصم", "سداد", "SAR")
        return keywords.any { message.contains(it) }
    }
}
