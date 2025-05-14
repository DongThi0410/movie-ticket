package com.example.app02.ui.Screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.app02.R
import com.example.app02.ui.components.CommonHeader
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.LightTextComponent
import com.example.app02.ui.components.LightTextComponentCenter
import com.example.app02.ui.components.RowComponent2
import com.example.app02.ui.components.Ticket
import com.example.app02.ui.theme.bcl
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.TicketViewModel
import java.time.format.DateTimeFormatter

@Composable
fun TicketScreen(
    navController: NavController,
    bookingId: Int,
    ticketViewModel: TicketViewModel,
    loginViewModel: LoginViewModel,
) {
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val ticketState = ticketViewModel.ticket.collectAsState(initial = null)
    val isLoading by ticketViewModel.loading.collectAsState()
    val ticket = ticketState.value

    // Gọi API lấy ticket khi composable được tạo
    LaunchedEffect(bookingId) {
        ticketViewModel.getTicket(bookingId)
    }

    CommonScaffold(isLogged, navController, loginViewModel, "user") { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                isLoading -> {
                    CommonHeader(title = ticket?.movieName ?: "Thông tin vé", navController)
                    CircularProgressIndicator()
                }

                ticket == null -> {
                    Text("Không tìm thấy thông tin vé", color = Color.Red)
                }

                else -> {
                    val seatText = ticket.seat.joinToString { "${it.row}${it.col}, " }
                    CommonHeader("Thanh toán thành công", navController)
                    LazyColumn(
                        modifier = Modifier
                            .padding(15.dp)
                            .background(bcl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            LightTextComponentCenter("Không chia sẻ cho bất kỳ ai!")
                        }
                        item {
                            LightTextComponentCenter("Cung cấp mã QR này cho nhân viên tại quầy để xác nhận! ")
                        }
                        item {
                            Column(
                                modifier = Modifier
                                    .padding(15.dp)
                                    .background(bcl),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 30.dp, topEnd = 30.dp
                                            )
                                        )
                                        .size(400.dp)
                                        .background(com.example.app02.ui.theme.ticket)
                                ) {
                                    ticket.qr?.let {
                                        Image(
                                            painter = rememberAsyncImagePainter(model = it),
                                            contentDescription = "logo",
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier
                                                .fillMaxSize(0.7f)
                                                .align(Alignment.Center)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(com.example.app02.ui.theme.ticket),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.fillMaxWidth(0.1f)) {
                                        Image(
                                            painter = painterResource(id = R.drawable.noun_half_circle_left),
                                            contentDescription = "left half circle",
                                            contentScale = ContentScale.Fit,

                                            )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .weight(0.8f)
                                            .padding(horizontal = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        repeat(10) {
                                            Divider(
                                                modifier = Modifier
                                                    .height(5.dp)
                                                    .width(20.dp), color = bcl
                                            )
                                        }
                                    }

                                    Box(modifier = Modifier.fillMaxWidth(0.1f)) {

                                        Image(
                                            painter = painterResource(id = R.drawable.right),
                                            contentDescription = "left half circle",
                                            contentScale = ContentScale.Fit,
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .clip(
                                            RoundedCornerShape(
                                                bottomStart = 30.dp, bottomEnd = 30.dp
                                            )
                                        )
                                        .background(com.example.app02.ui.theme.ticket)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,

                                    ) {
                                    RowComponent2(
                                        " ",
                                        ticket.payTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                                    )
                                    Spacer(modifier = Modifier.padding(5.dp))
                                    RowComponent2("Phim: ", ticket.movieName)
                                    RowComponent2("Phòng chiếu: ", ticket.audName)
                                    RowComponent2("Ghế: ", seatText)
                                    RowComponent2("Đã thanh toán: ", "${ticket.total.toInt()} VND")

                                }
                            }
                        }
                    }
                    ticket.qr?.let {
                        Image(
                            painter = rememberAsyncImagePainter(model = it),
                            contentDescription = "QR ticket",
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Phòng chiếu: ${ticket.audName}")
                    Text(text = "Mã vé: ${ticket.ticketId}")
                    Text(
                        text = "Thanh toán lúc: ${
                            ticket.payTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        }"
                    )
                    Text(text = "Tổng tiền: ${ticket.total} VNĐ")

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Chỗ ngồi:", style = MaterialTheme.typography.titleMedium)
                    Row {
                        ticket.seat.forEach {
                            Text(
                                text = "${it.row}${it.col}", modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
