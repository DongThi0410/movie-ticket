package com.example.app02.nav

import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.app02.data.DataStore
import com.example.app02.ui.Screens.user.HomeScreen
import com.example.app02.ui.Screens.LoginScreen
import com.example.app02.ui.Screens.ResetPasswordScreen
import com.example.app02.ui.Screens.user.MovieDetailScreen
import com.example.app02.ui.Screens.user.PaymentScreen
import com.example.app02.ui.Screens.user.ShowSeatScreen
import com.example.app02.ui.Screens.SignupScreen
import com.example.app02.ui.Screens.admin.MovieManage.CreateScheduleScreen
import com.example.app02.ui.Screens.admin.MovieManage.MovieManageScreen
import com.example.app02.ui.Screens.admin.MovieManage.MovieDetailScreen
import com.example.app02.ui.Screens.admin.MovieManage.ScheduleScreen
import com.example.app02.ui.Screens.admin.MovieManage.ShowtimeDetailScreen
import com.example.app02.ui.Screens.user.ProfileScreen
import com.example.app02.ui.Screens.user.TicketScreen
import com.example.app02.ui.components.ConditionsScreen
import com.example.app02.ui.components.Ticket
import com.example.app02.viewmodel.BookingViewModel
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.MovieViewModel
import com.example.app02.viewmodel.RevenueViewModel
import com.example.app02.viewmodel.TicketViewModel
import com.example.app02.viewmodel.UserViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    userViewModel: UserViewModel,
    dataStore: DataStore,
    bookingViewModel: BookingViewModel,
    ticketViewModel: TicketViewModel,
    revenueViewModel: RevenueViewModel,
    movieViewModel: MovieViewModel,

    ) {
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    var role by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        val savedRole = dataStore.getRole()
        if (savedRole == null) {
            dataStore.extractAndSave()
        }
        role = dataStore.getRole()?.removePrefix("ROLE_")?.lowercase()
    }

    role?.let {
        val startDestination = when (it) {
            "admin" -> "admin_home"
            "user" -> "user_home"
            else -> Screen.Login.route
        }
        NavHost(navController = navController, startDestination = startDestination) {
            composable(Screen.TicketLayout.route) {
//                Ticket()
            }
            composable(Screen.SignUp.route) { SignupScreen(navController) }
            composable(Screen.Reset.route) { ResetPasswordScreen(navController) }
            composable(Screen.Login.route) { LoginScreen(navController, loginViewModel) }
            composable(Screen.Conditions.route) { ConditionsScreen(navController) }
            composable("admin_home") {
                com.example.app02.ui.Screens.admin.HomeScreen(
                    navController,
                    loginViewModel,
                    revenueViewModel
                )
            }
            composable(Screen.Showtime.route) {
                ScheduleScreen(loginViewModel, navController)
            }


            composable("user_home") {

                HomeScreen(
                    navController, loginViewModel
                )
            }

            composable(Screen.ShowSeat.route) { backStackEntry ->
                val showtimeId =
                    backStackEntry.arguments?.getString("showtimeId")?.toIntOrNull()
                showtimeId?.let {
                    ShowSeatScreen(
                        showtimeId = it,
                        navController = navController,
                        role.toString(),
                        bookingViewModel,
                        loginViewModel
                    )
                }
            }

            composable("payment/{bookingId}/{qrUrl}/{showtimeId}") { entry ->
                val context = LocalContext.current
                val args = entry.arguments

                if (args == null) {
                    Toast.makeText(context, "Thiếu dữ liệu điều hướng", Toast.LENGTH_SHORT)
                        .show()
                    navController.popBackStack()
                    return@composable
                }

                val bookingId = args.getString("bookingId")?.toIntOrNull()
                val showtimeId = args.getString("showtimeId")?.toIntOrNull()
                val qrUrl = args.getString("qrUrl")?.let(Uri::decode)

                if (bookingId != null && qrUrl != null && showtimeId != null) {
                    PaymentScreen(
                        navController,
                        bookingId,
                        qrUrl,
                        showtimeId,

                        bookingViewModel,
                        loginViewModel
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Không thể mở trang thanh toán",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.popBackStack()
                }
            }



            composable(Screen.Bill.route) {
                ProfileScreen(
                    navController,
                    userViewModel,
                    dataStore,
                    loginViewModel, ticketViewModel, role.toString()
                )
            }
            composable(Screen.MovieDetail.route) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                if (movieId != null) {
                    MovieDetailScreen(movieId, navController, loginViewModel, role.toString())
                }
            }
            composable("movie/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                if (id != null) {
                    MovieDetailScreen(id, navController, movieViewModel, loginViewModel)
                }
            }
            composable("showtime-detail/{movieId}") { entry ->
                val context = LocalContext.current
                val args = entry.arguments
                if (args == null) {
                    Toast.makeText(context, "Thiếu dữ liệu điều hướng", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                    return@composable
                }
                val id = args.getString("movieId")?.toIntOrNull()
                if (id != null) {
                    ShowtimeDetailScreen(id, navController, loginViewModel)
                } else {
                    Toast.makeText(context, "Không tìm thấy ID vé", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            }
            composable(Screen.MovieManage.route) {
                MovieManageScreen(
                    loginViewModel,
                    navController
                )
            }
            composable("create_showtime/{movieId}") { entry ->
                val context = LocalContext.current
                val args = entry.arguments
                if (args == null) {
                    Toast.makeText(context, "Thiếu dữ liệu điều hướng", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                    return@composable
                }
                val id = args.getString("movieId")?.toIntOrNull()
                if (id != null) {
                    CreateScheduleScreen(id, loginViewModel, navController)
                } else {
                    Toast.makeText(context, "Không tìm thấy ID vé", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            }
            composable(
                "ticket/{id}",
            ) { entry ->
                val context = LocalContext.current
                val args = entry.arguments
                if (args == null) {
                    Toast.makeText(context, "Thiếu dữ liệu điều hướng", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                    return@composable
                }

                val id = args.getString("id")?.toIntOrNull()

                if (id != null) {
                    TicketScreen(navController, id, ticketViewModel, loginViewModel)
                } else {
                    Toast.makeText(context, "Không tìm thấy ID vé", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            }
        }

    }
}
