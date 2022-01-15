package com.phuongduy.currency.di.module

import com.phuongduy.currency.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [ViewModelModule::class])
interface MainModule {
    @ContributesAndroidInjector
    fun contributesMainActivity(): MainActivity
}