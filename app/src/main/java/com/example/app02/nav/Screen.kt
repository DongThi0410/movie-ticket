package com.example.app02.nav

sealed class Screen(val route: String) {
    object Home : Screen("user_home")
    object Bill : Screen("bill")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Reset : Screen("reset")
    object MovieDetail : Screen("movieDetail/{id}")
    object Conditions : Screen("conditions")
    object ShowSeat : Screen("showSeat/{showtimeId}")
    object Payment: Screen("payment/{bookingId}/{qrUrl}")
    object Ticket: Screen("ticket")
    object AdminHome: Screen("admin_home")
    object MovieManage: Screen("movie_manage")
    object MovieDailAdmin: Screen("movie/{id}")
    object TicketLayout: Screen("ticket_layout")
}
