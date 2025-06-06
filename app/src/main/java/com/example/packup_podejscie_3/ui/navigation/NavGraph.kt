package com.example.packup_podejscie_3.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation // Import for nested navigation graphs
import androidx.navigation.navArgument // Needed for arguments

import com.example.packup_podejscie_3.ui.screens.addscreens.AddEditEventScreen
import com.example.packup_podejscie_3.ui.screens.addscreens.NewAnnouncementScreen
import com.example.packup_podejscie_3.ui.screens.addscreens.NewListaZadanScreen
import com.example.packup_podejscie_3.ui.screens.addscreens.NewPollScreen
import com.example.packup_podejscie_3.ui.screens.auth.LoginScreen
import com.example.packup_podejscie_3.ui.screens.auth.RegisterScreen
import com.example.packup_podejscie_3.ui.screens.auth.WelcomeScreen
import com.example.packup_podejscie_3.ui.screens.eventdetail.AnnouncementBoardScreen
import com.example.packup_podejscie_3.ui.screens.eventdetail.EventDetailsScreen
import com.example.packup_podejscie_3.ui.screens.eventlist.EventListScreen
import com.example.packup_podejscie_3.ui.screens.eventdetail.MoneyEventScreen
import com.example.packup_podejscie_3.ui.screens.eventdetail.AnkietyDetailsScreen
import com.example.packup_podejscie_3.ui.screens.eventdetail.AnkietyListScreen
import com.example.packup_podejscie_3.ui.screens.eventdetail.ListaZadanListScreen
import com.example.packup_podejscie_3.ui.screens.eventdetail.OdpowiedziListScreen // <--- ADDED IMPORT
import com.example.packup_podejscie_3.ui.screens.addscreens.AddEditExpenseScreen // Corrected name


@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = WelcomeScreenDestination.route) {

        // Authentication Flow
        composable(WelcomeScreenDestination.route) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(LoginScreenDestination.route) },
                onRegisterClick = { navController.navigate(RegisterScreenDestination.route) }
            )
        }

        composable(LoginScreenDestination.route) {
            // CORRECTED: Pass navController as it's required by LoginScreen's signature
            LoginScreen(
                navController = navController, // <-- Added this line
                onLoginSuccess = {
                    // After successful login, navigate to the main app destination and clear back stack
                    navController.navigate(EventListScreenDestination.route) {
                        popUpTo(WelcomeScreenDestination.route) { inclusive = true } // Clear auth stack
                    }
                }
            )
        }

        composable(RegisterScreenDestination.route) {
            // CORRECTED: Pass navController as it's required by RegisterScreen's signature
            RegisterScreen(
                navController = navController, // <-- Added this line
                onRegisterSuccess = {
                    // After successful registration, navigate to the main app destination and clear back stack
                    navController.navigate(EventListScreenDestination.route) {
                        popUpTo(WelcomeScreenDestination.route) { inclusive = true } // Clear auth stack
                    }
                }
            )
        }

        // Event List and Creation
        composable(EventListScreenDestination.route) {
            EventListScreen(navController = navController)
        }

        composable(
            route = AddEditEventScreenDestination.routeWithArgs,
            arguments = AddEditEventScreenDestination.arguments
        ) {
            // eventId is retrieved by the ViewModel using SavedStateHandle
            AddEditEventScreen(navController = navController)
        }

        // Event Details and Sub-Screens (Announcements, Polls, Money, Tasks)
        composable(
            route = EventDetailsScreenDestination.routeWithArgs,
            arguments = EventDetailsScreenDestination.arguments
        ) {
            // eventId is retrieved by the ViewModel using SavedStateHandle
            EventDetailsScreen(navController = navController)
        }

        // Announcement Board
        composable(
            route = AnnouncementBoardScreenDestination.routeWithArgs,
            arguments = AnnouncementBoardScreenDestination.arguments
        ) {
            // eventId is retrieved by the ViewModel using SavedStateHandle
            AnnouncementBoardScreen(navController = navController)
        }

        // New Announcement Screen
        composable(
            route = NewAnnouncementScreenDestination.routeWithArgs,
            arguments = NewAnnouncementScreenDestination.arguments
        ) {
            // eventId is retrieved by the ViewModel using SavedStateHandle
            NewAnnouncementScreen(navController = navController)
        }

        // Polls List Screen
        composable(
            route = AnkietyListScreenDestination.routeWithArgs,
            arguments = AnkietyListScreenDestination.arguments
        ) { backStackEntry -> // <--- Added backStackEntry
            val eventId = backStackEntry.arguments?.getString(AnkietyListScreenDestination.eventId)
            if (eventId != null) {
                // eventId is explicitly passed because the Composable's signature requires it
                AnkietyListScreen(navController = navController, eventId = eventId)
            } else {
                // Handle case where eventId is missing, e.g., navigate back or show error
                navController.popBackStack()
            }
        }

        // New Poll Screen
        composable(
            route = NewPollScreenDestination.routeWithArgs,
            arguments = NewPollScreenDestination.arguments
        ) {
            // eventId is retrieved by the ViewModel using SavedStateHandle
            NewPollScreen(navController = navController)
        }

        // Ankiety Details Screen
        composable(
            route = AnkietyDetailsDestination.routeWithArgs,
            arguments = AnkietyDetailsDestination.arguments
        ) {
            // ankietyId is retrieved by the ViewModel using SavedStateHandle
            AnkietyDetailsScreen(navController = navController)
        }

        // Odpowiedzi List Screen (for Ankiety Options)
        composable(
            route = OdpowiedziListScreenDestination.routeWithArgs,
            arguments = OdpowiedziListScreenDestination.arguments
        ) {
            // ankietyId (or pollId) is retrieved by the ViewModel using SavedStateHandle
            OdpowiedziListScreen(navController = navController)
        }

        // Money Event Screen
        composable(
            route = MoneyEventScreenDestination.routeWithArgs,
            arguments = MoneyEventScreenDestination.arguments
        ) {
            // eventId is retrieved by the ViewModel using SavedStateHandle
            MoneyEventScreen(navController = navController)
        }

        // New Expense Screen (corrected name: AddEditExpenseScreen)
        composable(
            route = NewExpenseScreenDestination.routeWithArgs,
            arguments = NewExpenseScreenDestination.arguments
        ) {
            // eventId and expenseId are retrieved by the ViewModel using SavedStateHandle
            AddEditExpenseScreen(navController = navController)
        }

        // Task List Screen
        composable(
            route = ListaZadanListScreenDestination.routeWithArgs,
            arguments = ListaZadanListScreenDestination.arguments
        ) {
            // eventId is retrieved by the ViewModel using SavedStateHandle
            ListaZadanListScreen(navController = navController)
        }

        // New Task Screen
        composable(
            route = NewListaZadanScreenDestination.routeWithArgs,
            arguments = NewListaZadanScreenDestination.arguments
        ) {
            // eventId is retrieved by the ViewModel using SavedStateHandle
            NewListaZadanScreen(navController = navController)
        }

        // Add other destinations as needed...
        composable(ListenEventScreenDestination.route) {
            // ListenEventScreen() // Add your composable here
        }
        composable(PostsEventScreenDestination.route) {
            // PostsEventScreen() // Add your composable here
        }
    }
}