package com.ledgerly.parser.core.bank

import com.ledgerly.parser.core.TransactionType
import java.math.BigDecimal

/**
 * Parser for Alinma Bank (Saudi Arabia) SMS messages.
 */
class AlinmaBankParser : BaseSaudiBankParser() {

    override fun getBankName() = "Alinma Bank"

    override fun canHandle(sender: String): Boolean {
        val normalizedSender = sender.uppercase()
        return normalizedSender.contains("ALINMA") ||
                normalizedSender.contains("الإنماء")
    }

    override fun extractTransactionType(message: String): TransactionType? {
        if (message.contains("شراء") || message.contains("Purchase", ignoreCase = true)) return TransactionType.EXPENSE
        if (message.contains("إيداع") || message.contains("Deposit", ignoreCase = true)) return TransactionType.INCOME
        return super.extractTransactionType(message)
    }

    override fun extractMerchant(message: String, sender: String): String? {
        val fromPattern = Regex("""من:\s*([^\n]+?)(?:\n|في:|$)""", RegexOption.IGNORE_CASE)
        fromPattern.find(message)?.let { match ->
            val merchant = cleanMerchantName(match.groupValues[1].trim())
            if (isValidMerchantName(merchant)) return merchant
        }

        val atPattern = Regex("""لدى:\s*([^\n]+?)(?:\n|في:|$)""", RegexOption.IGNORE_CASE)
        atPattern.find(message)?.let { match ->
            val merchant = cleanMerchantName(match.groupValues[1].trim())
            if (isValidMerchantName(merchant)) return merchant
        }

        if (message.contains("POS") || message.contains("نقاط البيع")) return "POS Transaction"

        return super.extractMerchant(message, sender)
    }

    override fun extractAccountLast4(message: String): String? {
        val accountPattern = Regex("""حساب:\s*\*+(\d{4})""")
        accountPattern.find(message)?.let { return it.groupValues[1] }

        val cardPattern = Regex("""البطاقة:\s*\*+(\d{4})""")
        cardPattern.find(message)?.let { return it.groupValues[1] }

        val madaPattern = Regex("""بطاقة مدى:\s*(\d{4})\*""")
        madaPattern.find(message)?.let { return it.groupValues[1] }

        return super.extractAccountLast4(message)
    }

    override fun extractBalance(message: String): BigDecimal? {
        val balanceSARPattern = Regex("""الرصيد:\s*([0-9,]+(?:\.[0-9]{2})?)\s*SAR""", RegexOption.IGNORE_CASE)
        balanceSARPattern.find(message)?.let { match ->
            return try { BigDecimal(match.groupValues[1].replace(",", "")) } catch (e: Exception) { null }
        }

        val balanceRiyalPattern = Regex("""الرصيد:\s*([0-9,]+(?:\.[0-9]{2})?)\s*ريال""")
        balanceRiyalPattern.find(message)?.let { match ->
            return try { BigDecimal(match.groupValues[1].replace(",", "")) } catch (e: Exception) { null }
        }

        return super.extractBalance(message)
    }

    override fun isTransactionMessage(message: String): Boolean {
        if (message.contains("OTP", ignoreCase = true) || message.contains("رمز") || message.contains("كلمة المرور")) return false
        val transactionKeywords = listOf("شراء", "بمبلغ", "مبلغ", "الرصيد", "Purchase", "POS")
        return transactionKeywords.any { message.contains(it) }
    }

    override fun detectIsCard(message: String): Boolean {
        return message.contains("البطاقة") || message.contains("بطاقة") || 
               message.contains("بطاقة مدى") || message.contains("POS") || 
               message.contains("نقاط البيع")
    }
}
