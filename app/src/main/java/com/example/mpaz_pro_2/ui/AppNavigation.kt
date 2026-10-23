package com.example.mpaz_pro_2.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mpaz_pro_2.data.local.AppDatabase
import com.example.mpaz_pro_2.data.local.AppRepository
import com.example.mpaz_pro_2.ui.screens.AuthScreen
import com.example.mpaz_pro_2.ui.screens.StudentScreen
import com.example.mpaz_pro_2.ui.screens.TeacherScreen
import com.example.mpaz_pro_2.ui.screens.UtpScreen
import com.example.mpaz_pro_2.ui.viewmodel.AuthViewModel
import com.example.mpaz_pro_2.ui.viewmodel.AuthViewModelFactory
import com.example.mpaz_pro_2.ui.viewmodel.MainViewModel
import com.example.mpaz_pro_2.ui.viewmodel.MainViewModelFactory

@Composable
fun AppNavigation() {
    val context = LocalContext.current

    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember {
        AppRepository(
            userDao = database.userDao(),
            curricularDao = database.curricularDao(),
            questionDao = database.questionDao(),
            evaluationConfigDao = database.evaluationConfigDao(),
            attemptDao = database.attemptDao(),
            reportDao = database.reportDao()
        )
    }

    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(repository))
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(repository))

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            AuthScreen(
                viewModel = authViewModel,
                onAuthSuccess = { user ->
                    val route = when (user.role) {
                        "UTP" -> "utp"
                        "DOCENTE" -> "teacher"
                        else -> "student"
                    }
                    navController.navigate("$route/${Uri.encode(user.username)}") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        composable("utp/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            UtpScreen(
                username = username,
                viewModel = mainViewModel,
                onNavigateBack = {
                    authViewModel.reset()
                    navController.navigate("auth") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        composable("teacher/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            TeacherScreen(
                username = username,
                viewModel = mainViewModel,
                onNavigateBack = {
                    authViewModel.reset()
                    navController.navigate("auth") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        composable("student/{username}") { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: ""
            StudentScreen(
                username = username,
                viewModel = mainViewModel,
                onNavigateBack = {
                    authViewModel.reset()
                    navController.navigate("auth") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
    }
}