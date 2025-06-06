package com.example.packup_podejscie_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.packup_podejscie_3.ui.theme.PackUp_podejscie_3Theme
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject

// --- Importy wszystkich destynacji ---
import com.example.packup_podejscie_3.ui.navigation.WelcomeScreenDestination
import com.example.packup_podejscie_3.ui.navigation.LoginScreenDestination
import com.example.packup_podejscie_3.ui.navigation.RegisterScreenDestination
import com.example.packup_podejscie_3.ui.navigation.AddEditEventScreenDestination
import com.example.packup_podejscie_3.ui.navigation.NewAnnouncementScreenDestination
import com.example.packup_podejscie_3.ui.navigation.NewExpenseScreenDestination
import com.example.packup_podejscie_3.ui.navigation.NewPollScreenDestination
import com.example.packup_podejscie_3.ui.navigation.EventListScreenDestination
import com.example.packup_podejscie_3.ui.navigation.EventDetailsScreenDestination
import com.example.packup_podejscie_3.ui.navigation.AnkietyListScreenDestination
import com.example.packup_podejscie_3.ui.navigation.AnnouncementBoardScreenDestination
import com.example.packup_podejscie_3.ui.navigation.MoneyEventScreenDestination
import com.example.packup_podejscie_3.ui.navigation.MainAppDestination
import com.example.packup_podejscie_3.ui.navigation.AnkietyDetailsDestination
import com.example.packup_podejscie_3.ui.navigation.ListaZadanListScreenDestination
import com.example.packup_podejscie_3.ui.navigation.NewListaZadanScreenDestination

// --- Importy wszystkich ekranów (Composable functions) ---
import com.example.packup_podejscie_3.ui.screens.auth.WelcomeScreen
import com.example.packup_podejscie_3.ui.screens.auth.LoginScreen
import com.example.packup_podejscie_3.ui.screens.auth.RegisterScreen
import com.example.packup_podejscie_3.ui.screens.addscreens.AddEditEventScreen
import com.example.packup_podejscie_3.ui.screens.addscreens.NewAnnouncementScreen
import com.example.packup_podejscie_3.ui.screens.addscreens.AddEditExpenseScreen
import com.example.packup_podejscie_3.ui.screens.addscreens.NewPollScreen
import com.example.packup_podejscie_3.ui.screens.eventlist.EventListScreen
import com.example.packup_podejscie_3.ui.screens.eventdetail.EventDetailsScreen
import com.example.packup_podejscie_3.ui.screens.eventdetail.AnkietyListScreen
import com.example.packup_podejscie_3.ui.screens.eventdetail.AnnouncementBoardScreen
import com.example.packup_podejscie_3.ui.screens.eventdetail.MoneyEventScreen
import com.example.packup_podejscie_3.ui.screens.eventdetail.AnkietyDetailsScreen
import com.example.packup_podejscie_3.ui.screens.eventdetail.ListaZadanListScreen
import com.example.packup_podejscie_3.ui.screens.addscreens.NewListaZadanScreen


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var supabaseClient: SupabaseClient

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PackUp_podejscie_3Theme {
                val navController = rememberNavController()
                val currentBackStack by navController.currentBackStackEntryAsState()
                // val currentDestination = currentBackStack?.destination // Przydatne do debugowania

                Scaffold { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = WelcomeScreenDestination.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // --- Ekrany Uwierzytelniania ---
                        composable(WelcomeScreenDestination.route) {
                            WelcomeScreen(
                                onLoginClick = { navController.navigate(LoginScreenDestination.route) },
                                onRegisterClick = { navController.navigate(RegisterScreenDestination.route) }
                            )
                        }
                        composable(LoginScreenDestination.route) {
                            LoginScreen(
                                navController = navController,
                                onLoginSuccess = {
                                    navController.navigate(MainAppDestination.route) {
                                        popUpTo(LoginScreenDestination.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(RegisterScreenDestination.route) {
                            RegisterScreen(
                                navController = navController,
                                onRegisterSuccess = {
                                    navController.navigate(MainAppDestination.route) {
                                        popUpTo(RegisterScreenDestination.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- Główna aplikacja po zalogowaniu ---
                        composable(MainAppDestination.route) {
                            EventListScreen(navController = navController)
                        }

                        // --- Ekrany Wydarzeń ---
                        composable(EventListScreenDestination.route) {
                            EventListScreen(navController = navController)
                        }
                        composable(
                            route = EventDetailsScreenDestination.routeWithArgs,
                            arguments = EventDetailsScreenDestination.arguments
                        ) {
                            // EventDetailsScreen pobiera eventId bezpośrednio w ViewModelu
                            EventDetailsScreen(navController = navController)
                        }
                        composable(
                            route = AddEditEventScreenDestination.routeWithArgs,
                            arguments = AddEditEventScreenDestination.arguments
                        ) {
                            AddEditEventScreen(navController = navController)
                        }

                        // --- Ekrany Wydatków ---
                        composable(
                            route = MoneyEventScreenDestination.routeWithArgs,
                            arguments = MoneyEventScreenDestination.arguments
                        ) {
                            MoneyEventScreen(navController = navController)
                        }
                        composable(
                            route = NewExpenseScreenDestination.routeWithArgs,
                            arguments = NewExpenseScreenDestination.arguments
                        ) {
                            AddEditExpenseScreen(navController = navController)
                        }

                        // --- Ekrany Ogłoszeń ---
                        composable(
                            route = AnnouncementBoardScreenDestination.routeWithArgs,
                            arguments = AnnouncementBoardScreenDestination.arguments
                        ) {
                            AnnouncementBoardScreen(navController = navController)
                        }
                        composable(
                            route = NewAnnouncementScreenDestination.routeWithArgs,
                            arguments = NewAnnouncementScreenDestination.arguments
                        ) {
                            NewAnnouncementScreen(navController = navController)
                        }

                        // --- Ekrany Ankiet ---
                        composable(
                            route = AnkietyListScreenDestination.routeWithArgs,
                            arguments = AnkietyListScreenDestination.arguments
                        ) { navBackStackEntry ->
                            // Jawnie pobieramy eventId z argumentów trasy i przekazujemy go do AnkietyListScreen
                            val eventId = navBackStackEntry.arguments?.getString(AnkietyListScreenDestination.eventId)
                                ?: throw IllegalStateException("Event ID must be present for AnkietyListScreen")
                            AnkietyListScreen(eventId = eventId, navController = navController)
                        }
                        composable(
                            route = NewPollScreenDestination.routeWithArgs,
                            arguments = NewPollScreenDestination.arguments
                        ) {
                            NewPollScreen(navController = navController)
                        }
                        composable(
                            route = AnkietyDetailsDestination.routeWithArgs,
                            arguments = AnkietyDetailsDestination.arguments
                        ) {
                            AnkietyDetailsScreen(navController = navController)
                        }

                        // --- Ekrany Listy Zadań ---
                        composable(
                            route = ListaZadanListScreenDestination.routeWithArgs,
                            arguments = ListaZadanListScreenDestination.arguments
                        ) {
                            ListaZadanListScreen(navController = navController)
                        }
                        composable(
                            route = NewListaZadanScreenDestination.routeWithArgs,
                            arguments = NewListaZadanScreenDestination.arguments
                        ) {
                            NewListaZadanScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}