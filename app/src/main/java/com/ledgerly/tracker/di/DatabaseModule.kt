package com.ledgerly.tracker.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ledgerly.tracker.data.database.LedgerlyDatabase
import com.ledgerly.tracker.data.database.dao.AccountBalanceDao
import com.ledgerly.tracker.data.database.dao.ProfileDao
import com.ledgerly.tracker.data.database.dao.BankNotificationDao
import com.ledgerly.tracker.data.database.dao.BudgetDao
import com.ledgerly.tracker.data.database.dao.BudgetSnapshotDao
import com.ledgerly.tracker.data.database.dao.CardDao
import com.ledgerly.tracker.data.database.dao.CategoryDao
import com.ledgerly.tracker.data.database.dao.ChatDao
import com.ledgerly.tracker.data.database.dao.ExchangeRateDao
import com.ledgerly.tracker.data.database.dao.LoanDao
import com.ledgerly.tracker.data.database.dao.TransactionGroupDao
import com.ledgerly.tracker.data.database.dao.MerchantMappingDao
import com.ledgerly.tracker.data.database.dao.RuleApplicationDao
import com.ledgerly.tracker.data.database.dao.RuleDao
import com.ledgerly.tracker.data.database.dao.SubscriptionDao
import com.ledgerly.tracker.data.database.dao.TransactionDao
import com.ledgerly.tracker.data.database.dao.TransactionSplitDao
import com.ledgerly.tracker.data.database.dao.UnrecognizedSmsDao
import com.ledgerly.tracker.data.database.dao.SavingsGoalDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

/**
 * Hilt module that provides database-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provides the singleton instance of LedgerlyDatabase.
     * 
     * @param context Application context
     * @return Configured Room database instance
     */
    @Provides
    @Singleton
    fun provideLedgerlyDatabase(
        @ApplicationContext context: Context
    ): LedgerlyDatabase {
        val database = Room.databaseBuilder(
            context,
            LedgerlyDatabase::class.java,
            LedgerlyDatabase.DATABASE_NAME
        )
            // Add manual migrations here when needed
            .addMigrations(
                LedgerlyDatabase.MIGRATION_12_14,
                LedgerlyDatabase.MIGRATION_13_14,
                LedgerlyDatabase.MIGRATION_14_15,
                LedgerlyDatabase.MIGRATION_20_21,
                LedgerlyDatabase.MIGRATION_21_22,
                LedgerlyDatabase.MIGRATION_22_23,
                LedgerlyDatabase.MIGRATION_38_39,
                LedgerlyDatabase.MIGRATION_44_45,
                LedgerlyDatabase.MIGRATION_45_46
            )

            // Enable auto-migrations
            // Room will automatically detect schema changes between versions

            // Add callback to seed default data on first creation
            .addCallback(DatabaseCallback())

            .build()

        // Set the singleton instance so BroadcastReceivers can access it
        LedgerlyDatabase.setInstance(database)

        return database
    }
    
    /**
     * Provides the TransactionDao from the database.
     * 
     * @param database The LedgerlyDatabase instance
     * @return TransactionDao for accessing transaction data
     */
    @Provides
    @Singleton
    fun provideTransactionDao(database: LedgerlyDatabase): TransactionDao {
        return database.transactionDao()
    }
    
    /**
     * Provides the SubscriptionDao from the database.
     * 
     * @param database The LedgerlyDatabase instance
     * @return SubscriptionDao for accessing subscription data
     */
    @Provides
    @Singleton
    fun provideSubscriptionDao(database: LedgerlyDatabase): SubscriptionDao {
        return database.subscriptionDao()
    }
    
    /**
     * Provides the ChatDao from the database.
     * 
     * @param database The LedgerlyDatabase instance
     * @return ChatDao for accessing chat message data
     */
    @Provides
    @Singleton
    fun provideChatDao(database: LedgerlyDatabase): ChatDao {
        return database.chatDao()
    }
    
    /**
     * Provides the MerchantMappingDao from the database.
     * 
     * @param database The LedgerlyDatabase instance
     * @return MerchantMappingDao for accessing merchant mapping data
     */
    @Provides
    @Singleton
    fun provideMerchantMappingDao(database: LedgerlyDatabase): MerchantMappingDao {
        return database.merchantMappingDao()
    }
    
    /**
     * Provides the CategoryDao from the database.
     * 
     * @param database The LedgerlyDatabase instance
     * @return CategoryDao for accessing category data
     */
    @Provides
    @Singleton
    fun provideCategoryDao(database: LedgerlyDatabase): CategoryDao {
        return database.categoryDao()
    }
    
    /**
     * Provides the AccountBalanceDao from the database.
     * 
     * @param database The LedgerlyDatabase instance
     * @return AccountBalanceDao for accessing account balance data
     */
    @Provides
    @Singleton
    fun provideAccountBalanceDao(database: LedgerlyDatabase): AccountBalanceDao {
        return database.accountBalanceDao()
    }
    
    /**
     * Provides the UnrecognizedSmsDao from the database.
     * 
     * @param database The LedgerlyDatabase instance
     * @return UnrecognizedSmsDao for accessing unrecognized SMS data
     */
    @Provides
    @Singleton
    fun provideUnrecognizedSmsDao(database: LedgerlyDatabase): UnrecognizedSmsDao {
        return database.unrecognizedSmsDao()
    }
    
    /**
     * Provides the CardDao from the database.
     *
     * @param database The LedgerlyDatabase instance
     * @return CardDao for accessing card data
     */
    @Provides
    @Singleton
    fun provideCardDao(database: LedgerlyDatabase): CardDao {
        return database.cardDao()
    }

    /**
     * Provides the RuleDao from the database.
     *
     * @param database The LedgerlyDatabase instance
     * @return RuleDao for accessing rule data
     */
    @Provides
    @Singleton
    fun provideRuleDao(database: LedgerlyDatabase): RuleDao {
        return database.ruleDao()
    }

    /**
     * Provides the RuleApplicationDao from the database.
     *
     * @param database The LedgerlyDatabase instance
     * @return RuleApplicationDao for accessing rule application data
     */
    @Provides
    @Singleton
    fun provideRuleApplicationDao(database: LedgerlyDatabase): RuleApplicationDao {
        return database.ruleApplicationDao()
    }

    /**
     * Provides the ExchangeRateDao from the database.
     *
     * @param database The LedgerlyDatabase instance
     * @return ExchangeRateDao for accessing exchange rate data
     */
    @Provides
    @Singleton
    fun provideExchangeRateDao(database: LedgerlyDatabase): ExchangeRateDao {
        return database.exchangeRateDao()
    }

    /**
     * Provides the BudgetDao from the database.
     *
     * @param database The LedgerlyDatabase instance
     * @return BudgetDao for accessing budget data
     */
    @Provides
    @Singleton
    fun provideBudgetDao(database: LedgerlyDatabase): BudgetDao {
        return database.budgetDao()
    }

    /**
     * Provides the TransactionSplitDao from the database.
     *
     * @param database The LedgerlyDatabase instance
     * @return TransactionSplitDao for accessing transaction split data
     */
    @Provides
    @Singleton
    fun provideTransactionSplitDao(database: LedgerlyDatabase): TransactionSplitDao {
        return database.transactionSplitDao()
    }

    @Provides
    @Singleton
    fun provideBankNotificationDao(database: LedgerlyDatabase): BankNotificationDao {
        return database.bankNotificationDao()
    }

    @Provides
    @Singleton
    fun provideLoanDao(database: LedgerlyDatabase): LoanDao {
        return database.loanDao()
    }

    @Provides
    @Singleton
    fun provideTransactionGroupDao(database: LedgerlyDatabase): TransactionGroupDao {
        return database.transactionGroupDao()
    }

    @Provides
    @Singleton
    fun provideBudgetSnapshotDao(database: LedgerlyDatabase): BudgetSnapshotDao {
        return database.budgetSnapshotDao()
    }

    @Provides
    @Singleton
    fun provideProfileDao(database: LedgerlyDatabase): ProfileDao {
        return database.profileDao()
    }

    @Provides
    @Singleton
    fun provideSavingsGoalDao(database: LedgerlyDatabase): SavingsGoalDao {
        return database.savingsGoalDao()
    }
}

/**
 * Database callback to seed initial data when database is first created
 */
class DatabaseCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        
        // Seed default categories for new installations
        CoroutineScope(Dispatchers.IO).launch {
            seedCategories(db)
            seedProfiles(db)
        }
    }
    
    private fun seedCategories(db: SupportSQLiteDatabase) {
        val categories = listOf(
            Triple("Food & Dining", "#FC8019", false),
            Triple("Groceries", "#5AC85A", false),
            Triple("Transportation", "#000000", false),
            Triple("Shopping", "#FF9900", false),
            Triple("Bills & Utilities", "#4CAF50", false),
            Triple("Entertainment", "#E50914", false),
            Triple("Healthcare", "#10847E", false),
            Triple("Investments", "#00D09C", false),
            Triple("Banking", "#004C8F", false),
            Triple("Personal Care", "#6A4C93", false),
            Triple("Education", "#673AB7", false),
            Triple("Mobile", "#2A3890", false),
            Triple("Fitness", "#FF3278", false),
            Triple("Insurance", "#0066CC", false),
            Triple("Travel", "#00BCD4", false),
            Triple("Salary", "#4CAF50", true),
            Triple("Income", "#4CAF50", true),
            Triple("Others", "#757575", false)
        )
        
        categories.forEachIndexed { index, (name, color, isIncome) ->
            db.execSQL("""
                INSERT OR IGNORE INTO categories (name, color, is_system, is_income, display_order, created_at, updated_at)
                VALUES (?, ?, 1, ?, ?, datetime('now'), datetime('now'))
            """.trimIndent(), arrayOf<Any>(name, color, if (isIncome) 1 else 0, index + 1))
        }
    }

    private fun seedProfiles(db: SupportSQLiteDatabase) {
        db.execSQL("INSERT OR IGNORE INTO profiles (id, name, color_hex, sort_order) VALUES (1, 'Personal', '#4CAF50', 0)")
        db.execSQL("INSERT OR IGNORE INTO profiles (id, name, color_hex, sort_order) VALUES (2, 'Business', '#2196F3', 1)")
    }
}
