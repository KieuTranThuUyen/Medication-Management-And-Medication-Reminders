package com.example.medinotify

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import com.example.medinotify.ui.components.BottomNavBar
import com.example.medinotify.ui.screens.addmedicine.AddMedicineScreen
import com.example.medinotify.ui.screens.addmedicine.StartScreen
import com.example.medinotify.ui.screens.home.HomeScreen
import com.example.medinotify.ui.screens.medicinelist.MedicineListScreen
import com.example.medinotify.ui.screens.calendar.CalendarScreen
import com.example.medinotify.ui.screens.profile.ProfileScreen

@Composable
fun Navigation(navController: NavHostController) {

    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute !in listOf("add")) BottomNavBar(navController)
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {

            // ---------------- HOME ----------------
            composable("home") {
                HomeScreen()
            }

            // ---------------- START ----------------
            composable("start") {
                StartScreen(
                    onStart = { navController.navigate("add") }
                )
            }

            // ---------------- ADD ----------------
            composable("add") {
                AddMedicineScreen(
                    navController = navController
                )
            }

            // ---------------- LIST ----------------
            composable("medicine_list") {
                MedicineListScreen(
                    navController = navController
                )
            }

            // ---------------- CALENDAR ----------------
            composable("calendar") {
                CalendarScreen()
            }

            // ---------------- PROFILE ----------------
            composable("profile") {
                ProfileScreen()
            }
        }
    }
}
