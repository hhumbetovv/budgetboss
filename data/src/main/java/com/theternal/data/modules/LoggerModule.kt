package com.theternal.data.modules

import com.theternal.data.monitoring.FirebaseLogger
import com.theternal.domain.interfaces.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object LoggerModule {

    @Provides
    @IntoSet
    fun provideFirebaseLogger(): Logger {
        return FirebaseLogger()
    }

}