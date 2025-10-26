package hu.ait.finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.traceEventStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hu.ait.finalproject.navigation.AddBookScreenRoute
import hu.ait.finalproject.navigation.FeedScreenRoute
import hu.ait.finalproject.navigation.LoginScreenRoute
import hu.ait.finalproject.ui.screen.AddBookScreen
import hu.ait.finalproject.ui.screen.FeedScreen
import hu.ait.finalproject.ui.screen.LoginScreen
import hu.ait.finalproject.ui.theme.FinalProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinalProjectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = LoginScreenRoute
    ) {
        composable<LoginScreenRoute> {
            LoginScreen(onLoginSuccess = { navController.navigate(FeedScreenRoute) })
        }
        composable<FeedScreenRoute> {
            FeedScreen(onNewBookClick = {
                navController.navigate(AddBookScreenRoute)
            })
        }
        composable<AddBookScreenRoute> {
            AddBookScreen(onPostSuccess = {
                navController.navigate(FeedScreenRoute)
            })
        }

    }
}