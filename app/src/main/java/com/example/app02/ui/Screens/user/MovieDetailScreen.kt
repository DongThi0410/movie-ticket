package com.example.app02.ui.Screens.user

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.app02.network.models.Movie
import com.example.app02.network.models.Showtime
import com.example.app02.ui.components.*
import com.example.app02.ui.player.YouTubePlayer
import com.example.app02.ui.theme.bcl
import com.example.app02.ui.theme.boxshadow
//import com.example.app02.ui.player.YouTubePlayer
import com.example.app02.ui.theme.headerColor
import com.example.app02.ui.theme.textFocus
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.MovieViewModel
import com.example.app02.viewmodel.RatingViewModel
import com.example.app02.viewmodel.ShowtimeViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class) // Suppress experimental API warning
@Composable
fun MovieDetailScreen(
    movieId: Int,
    navController: NavController,
    loginViewModel: LoginViewModel,
    role: String,
    movieViewModel: MovieViewModel = viewModel(),
    showtimeViewModel: ShowtimeViewModel = viewModel()
) {
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val movie by movieViewModel.selectedMovie.collectAsState()
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    var selecledShowtime by rememberSaveable { mutableStateOf<Showtime?>(null) }

    val tabTitles = listOf("Chi tiết", "Suất chiếu")
    LaunchedEffect(movieId) {
        movieViewModel.fetchMovieDetails(movieId)
    }

    CommonScaffold(isLogged, navController, loginViewModel, role) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

        ) {
            movie?.let {

                CommonHeader(it.title ?: "Đang tải...", navController)
            }
            CustomTabRow(
                tabTitles = tabTitles,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { index -> selectedTabIndex = index }
            )

            when (selectedTabIndex) {
                0 -> {
                    if (movie == null) {
                        LoadingIndicator()
                    } else {
                        MovieDetailContent(movie)
//                        Btn(
//                            value = "Chọn suất",
//                            onClick = { selectedTabIndex = 1 }
//                        )
                    }
                }

                1 -> MovieShowtimes(movieId, navController = navController)
            }

        }
    }
}

@Composable
fun MovieShowtimes(
    movieId: Int,
    showtimeViewModel: ShowtimeViewModel = viewModel(),
    navController: NavController
) {

    val today = LocalDate.now()
    var selectedDate by rememberSaveable { mutableStateOf(today) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val showtimes by showtimeViewModel.showtimes.collectAsState(initial = emptyList())

    // Dùng snapshotFlow để theo dõi selectedDate thay vì LaunchedEffect
    LaunchedEffect(movieId) {
        snapshotFlow { selectedDate }
            .collectLatest { date ->
                showtimeViewModel.fetchShowtimes(movieId.toInt(), date.format(dateFormatter))
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
                Text("Đăng nhập để tiến hành đặt vé!", color = Color.White)
            }
        } else {
            LazyColumn {
                items(showtimes) { showtime ->
                    ShowtimeItem(showtime, navController)
                }
            }
        }
    }
}


@Composable
fun MovieDetailContent(movie: Movie?, ratingViewModel: RatingViewModel = viewModel()) {
    val rates by ratingViewModel.result.collectAsState()
    var text by remember { mutableStateOf("") }
    var selectedScore by remember { mutableStateOf(5) }
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(rates) {
        movie?.id?.let {

            ratingViewModel.getRating(it)
        }
    }

    movie?.let {
        val videoId = "dQw4w9WgXcQ"

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            YouTubePlayer(
                youtubeVideoId = "NLOp_6uPccQ",
                lifecycleOwner = LocalLifecycleOwner.current
            )
            Text(
                text = movie.des,
                modifier = Modifier
                    .fillMaxWidth(),
                style = TextStyle(
                    fontSize = 16.sp, fontStyle = FontStyle.Normal
                ),
                color = Color.White,
                textAlign = TextAlign.Justify // Căn đều chữ theo chiều ngang
            )
            Spacer(modifier = Modifier.height(8.dp))
            RowComponent("Thể loại", "${movie.genre.name}")
            RowComponent("Thời lượng", "${movie.duration} phút")
            RowComponent("Đạo diễn", "${movie.director}")
            RowComponent("Diễn viên chính", "${movie.cast}")

            Spacer(modifier = Modifier.padding(15.dp))

            Row {
                Box(modifier = Modifier.fillMaxWidth(0.8f)) {
                    MyTextField(
                        "Viết đánh giá tại đây...",
                        leadingIconVector = Icons.Default.Inbox,
                        value = text,
                        onValueChange = { text = it }
                    )
                }
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { expanded = true }) {
                        Row {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.Yellow
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.LightGray
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        (1..10).forEach { score ->
                            DropdownMenuItem(
                                text = { Text(score.toString()) },
                                onClick = {
                                    selectedScore = score
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer (modifier = Modifier.padding(10.dp))

            Btn("Đăng", onClick = {
                movie?.id?.let {

                    ratingViewModel.rating(it, selectedScore, text) { success ->
                        if (success) {
                            ratingViewModel.getRating(it)
                            Toast.makeText(context, "Đã đăng đánh giá", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Đánh giá chưa được đăng do lỗi hệ thống",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    }
                }
            })
            Spacer (modifier = Modifier.padding(15.dp))
            rates.forEach { rate ->
                Box(
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
                        .padding(10.dp)

                )
                {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .height(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = boxshadow
                        )

                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.5f)
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    rate.username,
                                    style = TextStyle(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.LightGray
                                    ), modifier = Modifier.fillMaxWidth(0.5f)
                                )
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Star Icon",
                                    tint = Color.Yellow, modifier = Modifier.fillMaxWidth(0.2f)
                                )
                                Text(
                                    rate.score.toString(),
                                    style = TextStyle(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Red
                                    ), modifier = Modifier.fillMaxWidth()
                                )


                            }
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterEnd,
                            ) {

                                LightTextComponent(
                                    rate.ratedAt.toString(),
                                    Color.White,
                                    alignment = TextAlign.End
                                )
                            }

                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalAlignment = Alignment.Start,
                        ) {
                            LightTextComponent(rate.comment, Color.White)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.padding(8.dp))

        }
    } ?: Text(text = "Không tìm thấy phim", modifier = Modifier.padding(16.dp))
}

@Composable
fun ShowtimeItem(showtime: Showtime, navController: NavController) {
    val startTime = getHourAndMinute(showtime.startTime).joinToString(":")
    val endTime = getHourAndMinute(showtime.endTime).joinToString(":")

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
        Btn("Chọn ghế", onClick = { navController.navigate("showSeat/${showtime.id}") })

    }
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        color = Color.Gray,
        thickness = 1.dp
    )
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreviewOfDetailScreen() {
//    val navController = rememberNavController() // Tạo NavController giả để tránh lỗi
//    MovieDetailScreen(movieId = 1, navController = navController)
//}
//
