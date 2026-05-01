package com.ledgerly.tracker.data.statement

import com.ledgerly.parser.core.ParsedTransaction

interface PdfStatementParser {
    fun canHandle(text: String): Boolean
    fun parse(text: String): List<ParsedTransaction>
}
