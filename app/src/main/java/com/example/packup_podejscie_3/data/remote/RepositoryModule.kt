package com.example.packup_podejscie_3.data.remote

import com.example.packup_podejscie_3.data.repository.* // Import your concrete repository implementations
import com.example.packup_podejscie_3.domain.repository.* // Import your repository interfaces
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient // You might need this if your Impl classes use it
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Install this module in the application-wide scope
abstract class RepositoryModule {

    // --- Binds for each missing Repository Interface ---

    @Binds
    @Singleton // Provide a single instance of this repository throughout the app
    abstract fun bindWydarzenieRepository(
        impl: WydarzenieRepositoryImpl // Hilt will figure out how to create WydarzenieRepositoryImpl
    ): WydarzenieRepository

    @Binds
    @Singleton
    abstract fun bindWydatkiRepository(
        impl: WydatkiRepositoryImpl
    ): WydatkiRepository

    @Binds
    @Singleton
    abstract fun bindAnkietyRepository(
        impl: AnkietyRepositoryImpl
    ): AnkietyRepository

    @Binds
    @Singleton
    abstract fun bindOgloszenieRepository(
        impl: OgloszenieRepositoryImpl
    ): OgloszenieRepository

    @Binds
    @Singleton
    abstract fun bindListaZadanRepository(
        impl: Lista_zadanRepositoryImpl // Assuming ListaZadanRepositoryImpl implements Lista_zadanRepository
    ): Lista_zadanRepository

    @Binds
    @Singleton
    abstract fun bindAuthenticationRepository(
        impl: AuthenticationRepositoryImpl
    ): AuthenticationRepository

    @Binds
    @Singleton
    abstract fun bindOdpowiedziRepository(
        impl: OdpowiedziRepositoryImpl
    ): OdpowiedziRepository

    // --- End of Binds ---
}