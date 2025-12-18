package com.example.sa.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sa.ui.navigation.SaDestinations
import com.example.sa.ui.screens.repositories.RepositoriesScreen
import com.example.sa.ui.screens.repositorydetail.RepositoryDetailScreen
import com.example.sa.ui.theme.SATheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = SaDestinations.RepositoriesRoute
                    ) {
                        composable(SaDestinations.RepositoriesRoute) {
                            RepositoriesScreen(
                                onRepositoryClick = { repositoryName ->
                                    navController.navigate(SaDestinations.repositoryDetailRoute(repositoryName))
                                }
                            )
                        }
                        
                        composable(
                            route = SaDestinations.RepositoryDetailRoute,
                            arguments = listOf(
                                navArgument(SaDestinations.RepositoryNameArg) {
                                    type = NavType.StringType
                                }
                            )
                        ) {
                            RepositoryDetailScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
} 
