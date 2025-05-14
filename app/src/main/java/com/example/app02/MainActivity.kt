package com.example.app02

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.app02.data.DataStore
import com.example.app02.nav.AppNavGraph
import com.example.app02.network.api.RetrofitInstance
import com.example.app02.ui.Screens.LoginScreen
import com.example.app02.ui.Screens.SignupScreen
import com.example.app02.ui.theme.App02Theme
import com.example.app02.ui.theme.bcl
import com.example.app02.viewmodel.BookingViewModel
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.MovieViewModel
import com.example.app02.viewmodel.RevenueViewModel
import com.example.app02.viewmodel.TicketViewModel
import com.example.app02.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Khởi tạo RetrofitInstance
            RetrofitInstance.initialize(this)

            // Set content view với Compose
            setContent {
                App02Theme {
                    val navController = rememberNavController()
                    val loginViewModel: LoginViewModel = viewModel()
                    val userViewModel: UserViewModel = viewModel()
                    val bookingViewModel: BookingViewModel = viewModel()
                    val ticketViewModel: TicketViewModel = viewModel()
                    val revenueViewModel: RevenueViewModel = viewModel()
                    val movieViewModel: MovieViewModel = viewModel()
                    val context = LocalContext.current
                    val dataStore = remember { DataStore(context) }
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        AppNavGraph(navController, loginViewModel, userViewModel, dataStore, bookingViewModel, ticketViewModel, revenueViewModel, movieViewModel)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error initializing app", e)
            throw e // Throw lại nếu cần
        }
    }
}
