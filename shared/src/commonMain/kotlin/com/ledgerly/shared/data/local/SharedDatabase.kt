package com.ledgerly.shared.data.local

import androidx.room.Database
import androidx.room.ConstructedBy
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.ledgerly.shared.data.local.dao.SharedCategoryDao
import com.ledgerly.shared.data.local.dao.SharedCategoryBudgetLimitDao
import com.ledgerly.shared.data.local.dao.SharedAccountBalanceDao
import com.ledgerly.shared.data.local.dao.SharedBudgetDao
import com.ledgerly.shared.data.local.dao.SharedCardDao
import com.ledgerly.shared.data.local.dao.SharedExchangeRateDao
import com.ledgerly.shared.data.local.dao.SharedMerchantMappingDao
import com.ledgerly.shared.data.local.dao.SharedRuleApplicationDao
import com.ledgerly.shared.data.local.dao.SharedRuleDao
import com.ledgerly.shared.data.local.dao.SharedSubscriptionDao
import com.ledgerly.shared.data.local.dao.SharedTransactionDao
import com.ledgerly.shared.data.local.dao.SharedTransactionSplitDao
import com.ledgerly.shared.data.local.dao.SharedUnrecognizedSmsDao
import com.ledgerly.shared.data.local.entity.SharedAccountBalanceEntity
import com.ledgerly.shared.data.local.entity.SharedBudgetCategoryEntity
import com.ledgerly.shared.data.local.entity.SharedBudgetEntity
import com.ledgerly.shared.data.local.entity.SharedCardEntity
import com.ledgerly.shared.data.local.entity.SharedCategoryEntity
import com.ledgerly.shared.data.local.entity.SharedCategoryBudgetLimitEntity
import com.ledgerly.shared.data.local.entity.SharedExchangeRateEntity
import com.ledgerly.shared.data.local.entity.SharedMerchantMappingEntity
import com.ledgerly.shared.data.local.entity.SharedRuleApplicationEntity
import com.ledgerly.shared.data.local.entity.SharedRuleEntity
import com.ledgerly.shared.data.local.entity.SharedSubscriptionEntity
import com.ledgerly.shared.data.local.entity.SharedTransactionEntity
import com.ledgerly.shared.data.local.entity.SharedTransactionSplitEntity
import com.ledgerly.shared.data.local.entity.SharedUnrecognizedSmsEntity

@Database(
    entities = [
        SharedTransactionEntity::class,
        SharedCategoryEntity::class,
        SharedSubscriptionEntity::class,
        SharedAccountBalanceEntity::class,
        SharedCardEntity::class,
        SharedTransactionSplitEntity::class,
        SharedMerchantMappingEntity::class,
        SharedRuleEntity::class,
        SharedRuleApplicationEntity::class,
        SharedExchangeRateEntity::class,
        SharedBudgetEntity::class,
        SharedBudgetCategoryEntity::class,
        SharedCategoryBudgetLimitEntity::class,
        SharedUnrecognizedSmsEntity::class
    ],
    version = 2,
    exportSchema = false
)
@ConstructedBy(SharedDatabaseConstructor::class)
abstract class SharedDatabase : RoomDatabase() {
    abstract fun transactionDao(): SharedTransactionDao
    abstract fun categoryDao(): SharedCategoryDao
    abstract fun subscriptionDao(): SharedSubscriptionDao
    abstract fun accountBalanceDao(): SharedAccountBalanceDao
    abstract fun cardDao(): SharedCardDao
    abstract fun transactionSplitDao(): SharedTransactionSplitDao
    abstract fun merchantMappingDao(): SharedMerchantMappingDao
    abstract fun ruleDao(): SharedRuleDao
    abstract fun ruleApplicationDao(): SharedRuleApplicationDao
    abstract fun exchangeRateDao(): SharedExchangeRateDao
    abstract fun budgetDao(): SharedBudgetDao
    abstract fun categoryBudgetLimitDao(): SharedCategoryBudgetLimitDao
    abstract fun unrecognizedSmsDao(): SharedUnrecognizedSmsDao

    companion object {
        const val DATABASE_NAME: String = "ledgerly_shared.db"
    }
}

@Suppress("KotlinNoActualForExpect")
expect object SharedDatabaseConstructor : RoomDatabaseConstructor<SharedDatabase> {
    override fun initialize(): SharedDatabase
}
