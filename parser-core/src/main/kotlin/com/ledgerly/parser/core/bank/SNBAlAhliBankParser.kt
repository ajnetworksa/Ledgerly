package com.ledgerly.parser.core.bank

import com.ledgerly.parser.core.TransactionType
import java.math.BigDecimal

/**
 * Parser for Saudi National Bank / Al Ahli Bank (SNB-AlAhli, Saudi Arabia).
 */
class SNBAlAhliBankParser : BaseSaudiBankParser() {

    override fun getBankName() = "Saudi National Bank"

    override fun canHandle(sender: String): Boolean {
        val normalized = sender.uppercase()
        return normalized.contains("SNB") ||
                normalized.contains("ALAHLI") ||
                normalized.contains("AL-AHLI") ||
                normalized.contains("AL AHLI") ||
                sender.contains("الأهلي")
    }

    override fun extractTransactionType(message: String): TransactionType? {
        return when {
            message.contains("واردة") || message.contains("إيداع") -> TransactionType.INCOME
            message.contains("شراء") || message.contains("سحب") || 
            message.contains("صادرة") || message.contains("خصم") || 
            message.contains("سداد") -> TransactionType.EXPENSE
            else -> super.extractTransactionType(message)
        }
    }

    override fun extractMerchant(message: String, sender: String): String? {
        val fromPattern = Regex("""من\s+([^\n]+?)(?:\n|$)""")
        fromPattern.find(message)?.let { match ->
            val raw = match.groupValues[1].trim()
            if (raw.isNotBlank() && !raw.all { it == '*' || it.isDigit() }) {
                val merchant = cleanMerchantName(raw)
                if (isValidMerchantName(merchant)) return merchant
            }
        }

        val toPattern = Regex("""الى\s*:?\s*([^\n]+?)(?:\n|$)""")
        toPattern.find(message)?.let { match ->
            val merchant = cleanMerchantName(match.groupValues[1].trim())
            if (isValidMerchantName(merchant)) return merchant
        }

        if (message.contains("صراف")) return "ATM Withdrawal"

        return super.extractMerchant(message, sender)
    }

    override fun extractAccountLast4(message: String): String? {
        val madaPattern = Regex("""مدى\s*\*+\s*(\d{3,4})""")
        madaPattern.find(message)?.let { return extractLast4Digits(it.groupValues[1]) }

        val cardPattern = Regex("""بطاقة\s*\*+\s*(\d{3,4})""")
        cardPattern.find(message)?.let { return extractLast4Digits(it.groupValues[1]) }

        return super.extractAccountLast4(message)
    }

    override fun extractBalance(message: String): BigDecimal? {
        val balancePattern = Regex("""الرصيد(?:\s*المتاح)?\s*:?\s*SAR\s*([0-9,]+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE)
        balancePattern.find(message)?.let { match ->
            return try {
                BigDecimal(match.groupValues[1].replace(",", ""))
            } catch (e: Exception) {
                null
            }
        }
        return super.extractBalance(message)
    }

    override fun detectIsCard(message: String): Boolean {
        if (message.contains("مدى") || message.contains("بطاقة") ||
            message.contains("نقاط بيع") || message.contains("SamsungPay", ignoreCase = true) ||
            message.contains("ApplePay", ignoreCase = true)
        ) return true
        return super.detectIsCard(message)
    }

    override fun isTransactionMessage(message: String): Boolean {
        if (message.contains("رمز") || message.contains("OTP", ignoreCase = true)) return false
        val keywords = listOf("شراء", "سحب", "حوالة", "خصم", "سداد", "إيداع", "SAR")
        return keywords.any { message.contains(it) }
    }
}
