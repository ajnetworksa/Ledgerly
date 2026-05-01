package com.ledgerly.shared.data.local

expect class SharedDatabaseFactory() {
    fun createDatabase(): SharedDatabase
}
