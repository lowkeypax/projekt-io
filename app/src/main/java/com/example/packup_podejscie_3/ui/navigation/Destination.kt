package com.example.packup_podejscie_3.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Interfejs bazowy dla wszystkich destynacji nawigacyjnych w aplikacji.
 */
interface Destination {
    val route: String
    val title: String
    val routeWithArgs: String // Ścieżka z placeholderami argumentów
    val arguments: List<NamedNavArgument>
}

/**
 * Destynacje związane z uwierzytelnianiem.
 */
object WelcomeScreenDestination : Destination {
    override val route = "WelcomeScreen"
    override val title = "Welcome"
    override val routeWithArgs = route
    override val arguments: List<NamedNavArgument> = emptyList()
}

object LoginScreenDestination : Destination {
    override val route = "LoginScreen"
    override val title = "Login"
    override val routeWithArgs = route
    override val arguments: List<NamedNavArgument> = emptyList()
}

object RegisterScreenDestination : Destination {
    override val route = "RegisterScreen"
    override val title = "Register"
    override val routeWithArgs = route
    override val arguments: List<NamedNavArgument> = emptyList()
}

/**
 * Destynacje dla ekranów dodawania/edycji.
 */
object AddEditEventScreenDestination : Destination {
    override val route = "AddEditEventScreen"
    override val title = "Add/Edit Event"
    const val eventId = "eventId" // Zmieniono na CamelCase
    override val routeWithArgs = "$route?{$eventId}" // Opcjonalny parametr zapytania
    override val arguments = listOf(
        navArgument(name = eventId) {
            type = NavType.StringType
            nullable = true
        }
    )
    fun createRouteWithParam(id: String? = null) = if (id != null) "$route?$eventId=$id" else route
}

object NewAnnouncementScreenDestination : Destination {
    override val route = "NewAnnouncementScreen"
    override val title = "New Announcement"
    const val eventId = "eventId"
    override val routeWithArgs = "$route/{$eventId}"
    override val arguments = listOf(
        navArgument(name = eventId) {
            type = NavType.StringType
            nullable = false
        }
    )
    fun createRouteWithParam(eventId: String) = "$route/$eventId"
}

object NewExpenseScreenDestination : Destination {
    override val route = "NewExpenseScreen"
    override val title = "New Expense"
    const val eventId = "eventId"
    const val expenseId = "expenseId" // <-- Dodano opcjonalny parametr dla edycji
    override val routeWithArgs = "$route/{$eventId}?$expenseId={$expenseId}" // Trasa z opcjonalnym parametrem
    override val arguments = listOf(
        navArgument(name = eventId) {
            type = NavType.StringType
            nullable = false // eventId jest wymagane!
        },
        navArgument(name = expenseId) { // Nowy argument
            type = NavType.StringType
            nullable = true // expenseId jest opcjonalne!
        }
    )
    fun createRouteWithParam(eventId: String, expenseId: String? = null) =
        if (expenseId != null) "$route/$eventId?$expenseId=$expenseId" else "$route/$eventId"
}

object NewPollScreenDestination : Destination {
    override val route = "NewPollScreen"
    override val title = "New Poll"
    const val eventId = "eventId"
    override val routeWithArgs = "$route/{$eventId}"
    override val arguments = listOf(
        navArgument(name = eventId) {
            type = NavType.StringType
            nullable = false
        }
    )
    fun createRouteWithParam(eventId: String) = "$route/$eventId"
}

/**
 * Destynacje dla głównych list i szczegółów wydarzeń/aktywności.
 */
object EventListScreenDestination : Destination {
    override val route = "EventListScreen"
    override val title = "Event List"
    override val routeWithArgs = route
    override val arguments: List<NamedNavArgument> = emptyList()
}

object EventDetailsScreenDestination : Destination {
    override val route = "EventDetailsScreen"
    override val title = "Event Details"
    const val eventId = "eventId"
    override val routeWithArgs = "$route/{$eventId}"
    override val arguments = listOf(
        navArgument(name = eventId) {
            type = NavType.StringType
            nullable = false
        }
    )
    fun createRouteWithParam(eventId: String) = "$route/$eventId"
}

object AnkietyListScreenDestination : Destination {
    override val route = "AnkietyListScreen"
    override val title = "Polls"
    const val eventId = "eventId"
    override val routeWithArgs = "$route/{$eventId}"
    override val arguments = listOf(
        navArgument(name = eventId) {
            type = NavType.StringType
            nullable = false
        }
    )
    fun createRouteWithParam(eventId: String) = "$route/$eventId"
}

object ListenEventScreenDestination : Destination {
    override val route = "ListenEventScreen"
    override val title = "Listen Event"
    override val routeWithArgs = route
    override val arguments: List<NamedNavArgument> = emptyList()
}

object AnnouncementBoardScreenDestination : Destination {
    override val route = "AnnouncementBoardScreen"
    override val title = "Announcement Board"
    const val eventId = "eventId"
    override val routeWithArgs = "$route/{$eventId}"
    override val arguments = listOf(
        navArgument(name = eventId) {
            type = NavType.StringType
            nullable = false
        }
    )
    fun createRouteWithParam(eventId: String) = "$route/$eventId"
}

object MoneyEventScreenDestination : Destination {
    override val route = "MoneyEventScreen"
    override val title = "Money Event"
    const val eventId = "eventId" // Dodajemy parametr eventId
    override val routeWithArgs = "$route/{$eventId}" // Teraz route wymaga eventId
    override val arguments = listOf(
        navArgument(name = eventId) {
            type = NavType.StringType
            nullable = false // eventId jest wymagane dla tego ekranu
        }
    )
    fun createRouteWithParam(eventId: String) = "$route/$eventId"
}


object PostsEventScreenDestination : Destination {
    override val route = "PostsEventScreen"
    override val title = "Posts Event"
    override val routeWithArgs = route
    override val arguments: List<NamedNavArgument> = emptyList()
}

/**
 * Główna destynacja, do której nawigujemy po zalogowaniu.
 * Może to być np. domyślny ekran dla zalogowanego użytkownika (np. EventListScreen).
 */
object MainAppDestination : Destination {
    override val route = "MainAppScreen"
    override val title = "Main App"
    override val routeWithArgs = route
    override val arguments: List<NamedNavArgument> = emptyList()
}

object AnkietyDetailsDestination : Destination {
    override val route = "AnkietyDetailsScreen"
    override val title = "Ankiety Details"
    const val ankietyId = "ankietyId"
    override val routeWithArgs = "$route/{$ankietyId}"
    override val arguments = listOf(
        navArgument(name = ankietyId) {
            type = NavType.StringType
            nullable = false
        }
    )
    fun createRouteWithParam(id: String) = "$route/$id"
}

// --- ADDED OdpowiedziListScreenDestination ---
object OdpowiedziListScreenDestination : Destination {
    override val route = "OdpowiedziListScreen"
    override val title = "Poll Answers"
    const val ankietyId = "ankietyId" // Assuming it needs ankietyId to show answers for a specific poll
    override val routeWithArgs = "$route/{$ankietyId}"
    override val arguments = listOf(
        navArgument(name = ankietyId) {
            type = NavType.StringType
            nullable = false
        }
    )
    fun createRouteWithParam(ankietyId: String) = "$route/$ankietyId"
}
// --- END ADDED ---

object ListaZadanListScreenDestination : Destination {
    override val route = "listaZadanListScreen"
    override val title = "Lista Zadań"
    const val eventId = "eventId"
    override val routeWithArgs = "$route/{$eventId}"
    override val arguments = listOf(
        navArgument(name = eventId) {
            type = NavType.StringType
            nullable = false
        }
    )
    fun createRouteWithParam(eventId: String) = "$route/$eventId"
}

object NewListaZadanScreenDestination : Destination {
    override val route = "newListaZadanScreen"
    override val title = "Nowe Zadanie"
    const val eventId = "eventId"
    override val routeWithArgs = "$route/{$eventId}"
    override val arguments = listOf(
        navArgument(name = eventId) {
            type = NavType.StringType
            nullable = false
        }
    )
    fun createRouteWithParam(eventId: String) = "$route/$eventId"
}