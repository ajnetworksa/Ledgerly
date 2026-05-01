package com.ledgerly.shared.data

import com.ledgerly.shared.data.bootstrap.SharedDataInitializer
import com.ledgerly.shared.data.local.SharedDatabase
import com.ledgerly.shared.data.local.SharedDatabaseFactory
import com.ledgerly.shared.data.repository.RoomSharedAccountRepository
import com.ledgerly.shared.data.repository.RoomSharedBudgetRepository
import com.ledgerly.shared.data.repository.RoomSharedCategoryRepository
import com.ledgerly.shared.data.repository.RoomSharedExchangeRateRepository
import com.ledgerly.shared.data.repository.RoomSharedMerchantMappingRepository
import com.ledgerly.shared.data.repository.RoomSharedRuleRepository
import com.ledgerly.shared.data.repository.RoomSharedSplitRepository
import com.ledgerly.shared.data.repository.RoomSharedSubscriptionRepository
import com.ledgerly.shared.data.repository.RoomSharedTransactionRepository
import com.ledgerly.shared.data.repository.RoomSharedUnrecognizedSmsRepository
import com.ledgerly.shared.data.repository.SharedAccountRepository
import com.ledgerly.shared.data.repository.SharedBudgetRepository
import com.ledgerly.shared.data.repository.SharedCategoryRepository
import com.ledgerly.shared.data.repository.SharedExchangeRateRepository
import com.ledgerly.shared.data.repository.SharedMerchantMappingRepository
import com.ledgerly.shared.data.repository.SharedRuleRepository
import com.ledgerly.shared.data.repository.SharedSplitRepository
import com.ledgerly.shared.data.repository.SharedSubscriptionRepository
import com.ledgerly.shared.data.repository.SharedTransactionRepository
import com.ledgerly.shared.data.repository.SharedUnrecognizedSmsRepository

class SharedDataGraph private constructor(
    val database: SharedDatabase,
    val transactionRepository: SharedTransactionRepository,
    val categoryRepository: SharedCategoryRepository,
    val subscriptionRepository: SharedSubscriptionRepository,
    val accountRepository: SharedAccountRepository,
    val splitRepository: SharedSplitRepository,
    val merchantMappingRepository: SharedMerchantMappingRepository,
    val ruleRepository: SharedRuleRepository,
    val exchangeRateRepository: SharedExchangeRateRepository,
    val budgetRepository: SharedBudgetRepository,
    val unrecognizedSmsRepository: SharedUnrecognizedSmsRepository
) {
    private val initializer = SharedDataInitializer(categoryRepository)

    suspend fun initialize() {
        initializer.seedDefaultCategoriesIfNeeded()
    }

    companion object {
        private val _instance: SharedDataGraph by lazy { create() }

        fun getInstance(): SharedDataGraph = _instance

        fun create(factory: SharedDatabaseFactory = SharedDatabaseFactory()): SharedDataGraph {
            val database = factory.createDatabase()
            return SharedDataGraph(
                database = database,
                transactionRepository = RoomSharedTransactionRepository(database.transactionDao()),
                categoryRepository = RoomSharedCategoryRepository(database.categoryDao()),
                subscriptionRepository = RoomSharedSubscriptionRepository(database.subscriptionDao()),
                accountRepository = RoomSharedAccountRepository(database.accountBalanceDao(), database.cardDao(), database.transactionDao()),
                splitRepository = RoomSharedSplitRepository(database.transactionSplitDao()),
                merchantMappingRepository = RoomSharedMerchantMappingRepository(database.merchantMappingDao()),
                ruleRepository = RoomSharedRuleRepository(database.ruleDao(), database.ruleApplicationDao()),
                exchangeRateRepository = RoomSharedExchangeRateRepository(database.exchangeRateDao()),
                budgetRepository = RoomSharedBudgetRepository(database.budgetDao(), database.categoryBudgetLimitDao()),
                unrecognizedSmsRepository = RoomSharedUnrecognizedSmsRepository(database.unrecognizedSmsDao())
            )
        }
    }
}
