package com.example.mpaz_pro_2.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mpaz_pro_2.ui.screens.MainScreen
import com.example.mpaz_pro_2.ui.screens.StudentScreen
import com.example.mpaz_pro_2.ui.screens.TeacherScreen

object Routes {
    const val MAIN = "main"
    const val TEACHER = "teacher/{userName}"
    const val STUDENT = "student/{userName}"

    fun teacherWithArg(userName: String) = "teacher/$userName"
    fun studentWithArg(userName: String) = "student/$userName"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.MAIN
    ) {
        composable(Routes.MAIN) {
            MainScreen(
                onNavigateToTeacher = { name ->
                    navController.navigate(Routes.teacherWithArg(name))
                },
                onNavigateToStudent = { name ->
                    navController.navigate(Routes.studentWithArg(name))
                }
            )
        }
        composable(
            route = Routes.TEACHER,
            arguments = listOf(navArgument("userName") { type = NavType.StringType })
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Docente"
            TeacherScreen(
                userName = userName,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.STUDENT,
            arguments = listOf(navArgument("userName") { type = NavType.StringType })
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Estudiante"
            StudentScreen(
                userName = userName,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}