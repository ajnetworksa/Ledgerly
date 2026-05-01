package com.ledgerly.parser.core.bank

import com.ledgerly.parser.core.TransactionType
import java.math.BigDecimal

/**
 * Base class for Saudi Arabian bank parsers.
 * Handles SAR currency and common cleaning logic.
 */
abstract class BaseSaudiBankParser : BankParser() {

    override fun getCurrency() = "SAR"

    override fun extractAmount(message: String): BigDecimal? {
        // Standard SAR patterns: 
        // "Amount: 1,234.56 SAR"
        // "Amount:1234.56SAR"
        // "SAR 1,234.56"
        // "1234.56 SAR"
        
        val patterns = listOf(
            Regex("""Amount\s*:?\s*([0-9,]+(?:\.\d{1,2})?)\s*SAR""", RegexOption.IGNORE_CASE),
            Regex("""SAR\s*([0-9,]+(?:\.\d{1,2})?)""", RegexOption.IGNORE_CASE),
            Regex("""([0-9,]+(?:\.\d{1,2})?)\s*SAR""", RegexOption.IGNORE_CASE)
        )

        for (pattern in patterns) {
            pattern.find(message)?.let { match ->
                val amountStr = match.groupValues[1].replace(",", "")
                return try {
                    BigDecimal(amountStr)
                } catch (e: NumberFormatException) {
                    null
                }
            }
        }

        return super.extractAmount(message)
    }

    override fun cleanMerchantName(merchant: String): String {
        return super.cleanMerchantName(merchant)
            .replace(Regex("""\s*mada Pay\s*""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""\s*\(Atheer\)\s*""", RegexOption.IGNORE_CASE), "")
            .trim()
    }
}
