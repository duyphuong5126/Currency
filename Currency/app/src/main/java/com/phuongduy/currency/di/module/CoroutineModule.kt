package com.phuongduy.currency.di.module

import com.phuongduy.currency.di.qualifier.IODispatcher
import com.phuongduy.currency.di.qualifier.MainDispatcher
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
class CoroutineModule {
    @IODispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @MainDispatcher
    @Provides
    fun providesMainDispatcher(): CoroutineDispatcher {
        return Dispatchers.Main
    }
}