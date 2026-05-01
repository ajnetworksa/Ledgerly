package com.ledgerly.tracker.di

import com.ledgerly.tracker.data.repository.RuleRepositoryImpl
import com.ledgerly.tracker.domain.repository.RuleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RuleModule {

    @Binds
    @Singleton
    abstract fun bindRuleRepository(
        ruleRepositoryImpl: RuleRepositoryImpl
    ): RuleRepository
}