package com.example.app02.ui.Screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.app02.ui.components.Btn
import com.example.app02.ui.components.CommonHeader
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.NormalTextComponent
import com.example.app02.ui.theme.reserved
import com.example.app02.viewmodel.BookingViewModel
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.SeatViewModel
import kotlinx.coroutines.delay

@Composable
fun PaymentScreen(
    navController: NavController,
    bookingId: Int,
    qrUrl: String,
    showtimeId: Int,
    bookingViewModel: BookingViewModel,
    loginViewModel: LoginViewModel
) {
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val seatViewModel: SeatViewModel = viewModel()
    val seats = bookingViewModel.selectedSeats
    val state by bookingViewModel.state.collectAsState()
    val countdown = remember { mutableStateOf(10 * 60) }
    val showDiaLog = remember { mutableStateOf(false) }
    DisposableEffect(Unit) {
        onDispose {

                    bookingViewModel.cancelBook(bookingId){}
        }
    }
    LaunchedEffect(Unit) {
        while (countdown.value > 0) {
            delay(1000)
            countdown.value -= 1
        }
        bookingViewModel.cancelBook(bookingId) { success ->
            if (success) {
                seatViewModel.fetchSeats(showtimeId)
            }
        }
        showDiaLog.value = true


    }

    CommonScaffold(isLogged, navController, loginViewModel, "user") { p ->
        CommonHeader( "Thanh toán",  navController)

        Column(modifier = Modifier.padding(p), horizontalAlignment = Alignment.CenterHorizontally) {
            val min = countdown.value / 60
            val sec = countdown.value % 60
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(30.dp)
                    .background(Color.Transparent, shape = RoundedCornerShape(8.dp))
                    .padding(4.dp)
                    .border(1.dp, reserved, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        3.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "time",
                        tint = Color.White
                    )
                        NormalTextComponent("$min:$sec")
                }
            }
            Btn("Đã thanh toán", onClick = {
                bookingViewModel.getBookingState(bookingId){success ->
                    navController.navigate("ticket/${bookingId}")

                }

            })
            Image(
                painter = rememberAsyncImagePainter(model = qrUrl),
                contentDescription = "QR thanh toan",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(12.dp))
            )
            Text("Ma ve $bookingId")
            Text("DS ghe:")
            seats.forEach { seat ->
                Text("${seat.row}${seat.col}")
            }
        }

    }
    if (showDiaLog.value) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Đã hết thời gian thanh toán") },
            confirmButton = {
                Button(onClick = { navController.popBackStack() }) {
                    Text("Trở về để thao tác chọn lại ghế nhía!")
                }
            }
        )
    }

}