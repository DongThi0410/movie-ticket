package com.example.app02.ui.Screens.admin.MovieManage


import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.app02.network.models.Cate
import com.example.app02.network.models.Movie
import com.example.app02.network.models.Showtime
import com.example.app02.ui.Screens.user.LoadingIndicator
import com.example.app02.ui.components.BoldTextComponent
import com.example.app02.ui.components.Btn
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.CustomTabRow
import com.example.app02.ui.components.CustomizableSearchBar
import com.example.app02.ui.components.LightTextComponent
import com.example.app02.ui.components.LightTextComponentCenter
import com.example.app02.ui.components.NormalTextComponent
import com.example.app02.ui.components.RowComponent
import com.example.app02.ui.components.customShadow
import com.example.app02.ui.components.getHourAndMinute
import com.example.app02.ui.theme.bcl
import com.example.app02.ui.theme.boxshad
import com.example.app02.ui.theme.boxshadow
import com.example.app02.ui.theme.textFocus
import com.example.app02.viewmodel.CateViewModel
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.MovieViewModel
import com.example.app02.viewmodel.ShowtimeViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    loginViewModel: LoginViewModel,
    navController: NavController,
    scheduleViewModel: ShowtimeViewModel = viewModel(),
    cateViewModel: CateViewModel = viewModel(),
    movieViewModel: MovieViewModel = viewModel()
) {
    var fromDate by remember { mutableStateOf(LocalDate.now()) }
    var toDate by remember { mutableStateOf(LocalDate.now().plusDays(2)) }
    var dailyStart by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var dailyEnd by remember { mutableStateOf(LocalTime.of(22, 0)) }
    var showDatePicker by remember { mutableStateOf(false) }
    val movies by movieViewModel.movies.collectAsState()
    val cates = cateViewModel.cate.value
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val tabTitles = listOf("Lập lịch", "Theo dõi ghế")
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    var note by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        movieViewModel.fetchMovies()
        cateViewModel.fetchCate()
    }
    CommonScaffold(isLogged, navController, loginViewModel, "admin") { p ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(p)
        ) {
            CustomTabRow(
                tabTitles = tabTitles,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { index -> selectedTabIndex = index }
            )

            when (selectedTabIndex) {
                0 -> {
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
                        scheduleViewModel.schedule(fromDate, toDate, dailyStart, dailyEnd) { success ->
                            if (success) {

                                Toast.makeText(context, "Lập lịch thành công", Toast.LENGTH_SHORT).show()
                                note = "Lên lịch thành công, vui lòng kiểm tra lại!"
                            }else{
                                Toast.makeText(context, "Lập lịch that bai", Toast.LENGTH_SHORT).show()
                                note = "Lên lịch that bai, vui lòng kiểm tra lại!"
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

                1 -> MovieListSec(cates, movies, navController, movieViewModel)
            }
//


        }
    }

}
@Composable
fun MovieListSec(
    cates: List<Cate>,
    movies: List<Movie>,
    navController: NavController,
    movieViewModel: MovieViewModel,
) {
    var query by remember { mutableStateOf("") }
    val result by movieViewModel.movies.collectAsState()
    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            movieViewModel.search(query)
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        cates.forEach { cate ->
            Card(
                modifier = Modifier
                    .weight(1f) // chia đều
                    .height(50.dp)
                    .padding(8.dp)
                    .clickable { movieViewModel.fetchMoviesByCate(cate.id) }
                    .customShadow(
                        color = boxshad,
                        alpha = 0.2f,
                        borderRadius = 16.dp,
                        shadowRadius = 10.dp,
                        offsetX = 4.dp,
                        offsetY = 4.dp
                    ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
                )

            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material.Text(
                        text = cate.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray
                    )
                }
            }
        }
    }


    CustomizableSearchBar(
        query = query,
        onQueryChange = {
            query = it
            movieViewModel.search(it)
        },
        onSearch = { /* no-op */ },
        searchResults = result,
        onResultClick = {id ->
            navController.navigate("movie/${id}")
            println("Clicked movie ID: $id") }
    )
    LazyColumn {
        items(movies) { movie ->
            Box( //2
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bcl)
                    .customShadow(
                        color = boxshadow,
                        alpha = 0.2f,
                        borderRadius = 16.dp,
                        shadowRadius = 10.dp,
                        offsetX = 2.dp,
                        offsetY = 2.dp
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .padding(8.dp)
                    .clickable {
                        navController.navigate("showtime-detail/${movie.id}")
                    }
            )
            {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
//                    .clickable { movieViewModel.fetchMoviesByCate(cate.id) }
                    ,
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = boxshadow
                    )

                ) {
                    Row {
                        Box(modifier = Modifier.weight(1f)) {
                            Image(
                                painter = rememberAsyncImagePainter(model = movie.poster),
                                contentDescription = "Movie Poster",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .size(400.dp)
                                    .padding(8.dp)
                            )

                        }
                        Box(modifier = Modifier.weight(3f)) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                horizontalAlignment = Alignment.Start,
                            ) {
                                androidx.compose.material.Text(
                                    movie.title,
                                    style = TextStyle(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.LightGray
                                    )
                                )
                                Spacer(modifier = Modifier.padding(4.dp))
                                LightTextComponent(movie.title)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.padding(8.dp))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Btn("OK",  onClick = {
                onDateRangeSelected(
                    Pair(
                        dateRangePickerState.selectedStartDateMillis,
                        dateRangePickerState.selectedEndDateMillis
                    )
                )
                onDismiss()
            })
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Chọn phạm vi ngày "
                )
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialExample(
    initialTime: LocalTime = LocalTime.now(),
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true,
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            modifier = Modifier.padding(8.dp),
        ) {
            Column(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                TimePicker(state = timePickerState)

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onDismiss) {
                        Text("Hủy")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Btn("Xác nhận", onClick = {
                        val selectedTime = LocalTime.of(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        onConfirm(selectedTime)
                    })
                }
            }
        }
    }
}
