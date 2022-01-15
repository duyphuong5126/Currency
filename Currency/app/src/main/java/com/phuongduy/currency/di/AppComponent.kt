package com.phuongduy.currency.di

import android.app.Application
import com.phuongduy.currency.CurrencyApp
import com.phuongduy.currency.di.module.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        CoroutineModule::class,
        MainModule::class]
)
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(weatherApp: CurrencyApp)

    fun application(): Application
}