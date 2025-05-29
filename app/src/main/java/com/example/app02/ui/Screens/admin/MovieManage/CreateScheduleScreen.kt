package com.example.app02.ui.Screens.admin.MovieManage

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app02.network.models.Movie
import com.example.app02.ui.components.BoldTextComponent
import com.example.app02.ui.components.Btn
import com.example.app02.ui.components.CommonHeader
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.LightTextComponentCenter
import com.example.app02.ui.components.RowComponent
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.MovieViewModel
import com.example.app02.viewmodel.ShowtimeViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Composable
fun CreateScheduleScreen(
    movieId: Int,
    loginViewModel: LoginViewModel, navController: NavController,
    scheduleViewModel: ShowtimeViewModel = viewModel(),
    movieViewModel: MovieViewModel = viewModel()
) {

    val movie by movieViewModel.selectedMovie.collectAsState()
    var fromDate by remember { mutableStateOf(LocalDate.now()) }
    var toDate by remember { mutableStateOf(LocalDate.now().plusDays(2)) }
    var dailyStart by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var dailyEnd by remember { mutableStateOf(LocalTime.of(22, 0)) }
    var showDatePicker by remember { mutableStateOf(false) }
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var note by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        movieViewModel.fetchMovieDetails(movieId)
    }
    CommonScaffold(isLogged, navController, loginViewModel, "admin") { p ->
        Column(modifier = Modifier.padding(p)) {
            BoldTextComponent("Tạo lịch chiếu")
            RowComponent("Từ ngày: ", fromDate.toString())
            RowComponent("Đến ngày: ", toDate.toString())
            Btn("Chọn ngày", onClick = {
                showDatePicker = true
            })
            RowComponent(
                "Giờ bắt đầu mỗi ngày: ",
                dailyStart.format(DateTimeFormatter.ofPattern("HH:mm"))
            )
            Btn("Chọn giờ", onClick = { showStartTimePicker = true })

            RowComponent(
                "Giờ kết thúc mỗi ngày: ",
                dailyEnd.format(DateTimeFormatter.ofPattern("HH:mm"))
            )
            Btn("Chọn giờ", onClick = { showEndTimePicker = true })

            Btn("Lập lịch", onClick = {

                movie?.id?.let {
                    scheduleViewModel.scheduleByMovie(it,fromDate, toDate, dailyStart, dailyEnd) { success ->
                        if (success) {

                            Toast.makeText(context, "Lập lịch thành công", Toast.LENGTH_SHORT).show()
                            note = "Lên lịch thành công, vui lòng kiểm tra lại!"
                        } else {
                            Toast.makeText(context, "Lập lịch that bai", Toast.LENGTH_SHORT).show()
                            note = "Lên lịch that bai, vui lòng kiểm tra lại!"
                        }
                    }
                }
            })

            LightTextComponentCenter(note, Color.Green)

            if (showDatePicker) {
                DateRangePickerModal(
                    onDateRangeSelected = { pair ->
                        pair.first?.let { millisStart ->
                            fromDate = Instant.ofEpochMilli(millisStart)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        pair.second?.let { millisEnd ->
                            toDate = Instant.ofEpochMilli(millisEnd)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                    },
                    onDismiss = { showDatePicker = false }
                )
            }

            if (showStartTimePicker) {
                DialExample(initialTime = dailyStart,
                    onConfirm = {
                        dailyStart = it
                        showStartTimePicker = false
                    },
                    onDismiss = { showStartTimePicker = false })
            }
            if (showEndTimePicker) {
                DialExample(initialTime = dailyEnd,
                    onConfirm = {
                        dailyEnd = it
                        showEndTimePicker = false
                    },
                    onDismiss = { showEndTimePicker = false })
            }
        }
    }
}