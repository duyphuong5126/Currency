package com.phuongduy.currency.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phuongduy.currency.presentation.viewmodel.main.MainViewModelImpl
import com.phuongduy.currency.presentation.viewmodel.ViewModelFactory
import com.phuongduy.currency.presentation.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {
    @Binds
    fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModelImpl::class)
    fun postListViewModel(viewModelImpl: MainViewModelImpl): ViewModel
}