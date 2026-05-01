package com.ledgerly.shared.data.statement

expect object SharedPdfTextExtractor {
    fun extractText(filePath: String): String
}
