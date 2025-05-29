package com.example.app02.ui.Screens.admin.MovieManage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app02.network.models.Showtime
import com.example.app02.ui.components.Btn
import androidx.compose.material3.Icon
import com.example.app02.ui.components.CommonHeader
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.NormalTextComponent
import com.example.app02.ui.components.getHourAndMinute
import com.example.app02.ui.theme.textFocus
import com.example.app02.viewmodel.BookingViewModel
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.MovieViewModel
import com.example.app02.viewmodel.ShowtimeViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ShowtimeDetailScreen(
    movieId: Int,
    navController: NavController,
    loginViewModel: LoginViewModel,
    showtimeViewModel: ShowtimeViewModel = viewModel(),
    movieViewModel: MovieViewModel = viewModel()
) {
    val movie by movieViewModel.selectedMovie.collectAsState()
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val today = LocalDate.now()
    var selectedDate by rememberSaveable { mutableStateOf(today) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val showtimes by showtimeViewModel.showtimes.collectAsState(initial = emptyList())
    LaunchedEffect(movieId) {
        movieViewModel.fetchMovieDetails(movieId)
        snapshotFlow { selectedDate }
            .collectLatest { date ->

                showtimeViewModel.fetchShowtimes(movieId.toInt(), date.format(dateFormatter))
            }
    }
    CommonScaffold(isLogged, navController, loginViewModel, "admin") { p ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(p)
        ) {
            movie?.let {
                CommonHeader(
                    it.title, navController, rightIcon = {
                        IconButton(onClick = {
                            navController.navigate("create_showtime/${it.id}")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "add",
                                tint = Color.LightGray
                            )
                        }

                    }
                )
            }
            Text("Chọn ngày:", style = MaterialTheme.typography.bodyMedium, color = textFocus)

            LazyRow {
                items(4) { i ->
                    val date = today.plusDays(i.toLong())
                    Button(
                        onClick = { selectedDate = date },
                        modifier = Modifier.padding(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedDate == date) Color(0xFF2F2C2C) else Color.Gray
                        )
                    ) {
                        Text(date.format(dateFormatter), color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (showtimes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có suất chiếu nào", color = Color.White)
                }
            } else {
                LazyColumn {
                    items(showtimes) { showtime ->
                        ShowtimeItemAdmin(showtime)
                    }
                }
            }
        }
    }
}

@Composable
fun ShowtimeItemAdmin(showtime: Showtime) {
    val countViewModel: BookingViewModel = viewModel()
    val startTime = getHourAndMinute(showtime.startTime).joinToString(":")
    val endTime = getHourAndMinute(showtime.endTime).joinToString(":")
    var showDialog by remember { mutableStateOf(false) }
    val counted by countViewModel.counted.collectAsState()

    LaunchedEffect(Unit) {
        countViewModel.countSeat(showtime.id)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        NormalTextComponent(startTime)
        Spacer(modifier = Modifier.width(8.dp))
        Divider(
            modifier = Modifier
                .height(24.dp)
                .width(2.dp),
            color = Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        NormalTextComponent(endTime)
        Btn("Trạng thái ", onClick = {
            showDialog = true
            countViewModel.countSeat(showtime.id)

        })

    }
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        color = Color.Gray,
        thickness = 1.dp
    )
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Đóng")
                }
            },
            title = {
                Text("Trạng thái ghế đã đặt")
            },
            text = {

                Text("Số ghế đã được đặt: $counted")
            }
        )
    }

}

