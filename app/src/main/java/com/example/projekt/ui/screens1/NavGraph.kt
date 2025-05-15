//package com.example.ioapp.ui.screens
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import com.example.ioapp.ui.screens.*
//import ui.screens.AnnouncementBoardScreen
//
//sealed class Screen(val route: String) {
//    object Announcements : Screen("announcements")
//    object NewAnnouncement : Screen("new_announcement")
//    object NewPoll : Screen("new_poll")
//}
//
//@Composable
//fun AppNavGraph(navController: NavHostController) {
//    NavHost(navController = navController, startDestination = Screen.Announcements.route) {
//        composable(Screen.Announcements.route) {
//            AnnouncementBoardScreen(
//                onCreateAnnouncement = { navController.navigate(Screen.NewAnnouncement.route) },
//                onCreatePoll = { navController.navigate(Screen.NewPoll.route) }
//            )
//        }
//        composable(Screen.NewAnnouncement.route) {
//            NewAnnouncementScreen(onSubmit = {
//                // TODO: save to DB
//                navController.popBackStack()
//            })
//        }
//        composable(Screen.NewPoll.route) {
//            NewPollScreen(onSubmit = { _, _ ->
//                // TODO: save to DB
//                navController.popBackStack()
//            })
//        }
//    }
//}