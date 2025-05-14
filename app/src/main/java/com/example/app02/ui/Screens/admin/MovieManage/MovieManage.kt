package com.example.app02.ui.Screens.admin.MovieManage

import android.app.DatePickerDialog
import android.widget.CalendarView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VideoLibrary

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.app02.R
import com.example.app02.nav.Screen
import com.example.app02.network.api.MovieSearch
import com.example.app02.network.models.Cate
import com.example.app02.network.models.Genre
import com.example.app02.network.models.Movie
import com.example.app02.ui.Screens.user.LoadingIndicator
import com.example.app02.ui.Screens.user.MovieShowtimes
import com.example.app02.ui.components.BoldTextComponent
import com.example.app02.ui.components.Btn
import com.example.app02.ui.components.CommonHeader
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.CustomTabRow
import com.example.app02.ui.components.CustomizableSearchBar
import com.example.app02.ui.components.LightTextComponent
import com.example.app02.ui.components.MyTextField
import com.example.app02.ui.components.customShadow
import com.example.app02.ui.theme.bcl
import com.example.app02.ui.theme.boxshad
import com.example.app02.ui.theme.boxshadow
import com.example.app02.viewmodel.CateViewModel
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.MovieViewModel
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Calendar

@Composable
fun MovieManageSceen(
    loginViewModel: LoginViewModel,
    navController: NavController,
    movieViewModel: MovieViewModel = viewModel(),
    cateViewModel: CateViewModel = viewModel(),

    ) {
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val movies by movieViewModel.movies.collectAsState()
    val cates = cateViewModel.cate.value
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val tabTitles = listOf("Danh sách phim", "Thêm phim")
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
                    if (movies == null) {
                        LoadingIndicator()
                    } else {
                        MovieList(cates, movies, navController, movieViewModel)
                    }
                }

                1 -> NewMovieScreen(cates, navController, movieViewModel)
            }
//
        }
    }
}

@Composable
fun MovieList(
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
                    containerColor = Color.White.copy(alpha = 0.1f) // màu nền trắng mờ
                )

            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cate.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
//    val dummyMovieList = listOf(
//        MovieSearch(
//            id = 1,
//            title = "Địa đạo",
//            poster = "https://media.baovanhoa.vn/zoom/1000/uploaded/nguyenthithutrang/2024_04_29/dia_dao_FHSP.jpg",
//            cast = "Đông Thi"
//        ),
//        MovieSearch(
//            id = 2,
//            title = "Bố già",
//            cast = "Đông Thi",
//            poster = "https://link-to-another-image.jpg",
//        )
//    )


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
                        navController.navigate("movie/${movie.id}")
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
                                Text(
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

@Composable
fun NewMovieScreen(
    cates: List<Cate>,
    navController: NavController,
    movieViewModel: MovieViewModel
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var posterUrl by remember { mutableStateOf("") }
    var cast by remember { mutableStateOf("") }
    var director by remember { mutableStateOf("") }
    var trailer by remember { mutableStateOf("") }

    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }
    var selectedCate by remember { mutableStateOf<Cate>(Cate(1, "Đang chiếu")) }
    var selectedGenre by remember { mutableStateOf<Genre>(Genre(1, "Hành động")) }
    val context = LocalContext.current
    val genres = listOf(
        Genre(1, "Hành động"),
        Genre(2, "Kinh dị "),
        Genre(3, "Hài huóc"),
        Genre(3, "Tình cảm"),
        Genre(3, "Gia đình")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            MyTextField(
                "Tên phim",
                leadingIconVector = Icons.Default.VideoLibrary,
                value = title,
                onValueChange = { title = it }
            )
        }

        item {
            MyTextField(
                "Mô tả",
                leadingIconVector = Icons.Default.Description,
                value = description,
                onValueChange = { description = it }
            )
        }

        item {
            OutlinedTextField(
                value = duration ?: "",
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        duration = input
                    }
                },
                label = { Text("Thời lượng (phút)") },
                leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedLeadingIconColor = Color.White,
                    unfocusedLeadingIconColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedContainerColor = bcl,
                    unfocusedContainerColor = bcl,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    unfocusedPlaceholderColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )


            )

        }

        item {
            MyTextField(
                "Poster URL",
                leadingIconVector = Icons.Default.Image,
                value = posterUrl,
                onValueChange = { posterUrl = it }
            )
        }

        item {
            MyTextField(
                "Diễn viên",
                leadingIconVector = Icons.Default.Person,
                value = cast,
                onValueChange = { cast = it }
            )
        }

        item {
            MyTextField(
                "Đạo diễn",
                leadingIconVector = Icons.Default.PersonOutline,
                value = director,
                onValueChange = { director = it }
            )
        }
        item {
            MyTextField(
                "Trailer",
                leadingIconVector = Icons.Default.PersonOutline,
                value = trailer,
                onValueChange = { trailer = it }
            )
        }
        item {
            Text(
                text = "Ngày bắt đầu",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // ✅ tránh lỗi scroll vô hạn
                    .padding(bottom = 16.dp)
            ) {
                AndroidView(
                    factory = { CalendarView(it) },
                    modifier = Modifier.fillMaxSize(),
                    update = { calendarView ->
                        // Cập nhật ngày đã chọn nếu có
                        startDate?.let {
                            val millis =
                                it.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            calendarView.date = millis
                        }
                        // Lắng nghe khi người dùng chọn ngày
                        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                            startDate = LocalDate.of(year, month + 1, dayOfMonth)
                            start = startDate.toString()
                        }
                    }
                )
            }
        }

        item {
            Text(
                text = "Ngày kết thúc",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // ✅ chiều cao cố định
                    .padding(bottom = 16.dp)
            ) {
                AndroidView(
                    factory = { CalendarView(it) },
                    modifier = Modifier.fillMaxSize(),
                    update = { calendarView ->
                        endDate?.let {
                            val millis =
                                it.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            calendarView.date = millis
                        }
                        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                            endDate = LocalDate.of(year, month + 1, dayOfMonth)
                            end = endDate.toString()
                        }
                    }
                )
            }
        }

        item {
            LabeledDropdown(
                label = "Danh mục",
                items = cates,
                selectedItem = selectedCate,
                onItemSelected = { selectedCate = it },
                itemToText = { it.name },
                icon = Icons.Default.Category
            )
        }

        item {
            LabeledDropdown(
                label = "Thể loại",
                items = genres,
                selectedItem = selectedGenre,
                onItemSelected = { selectedGenre = it },
                itemToText = { it.name },
                icon = Icons.Default.Movie
            )
        }
        item {
            Btn("Thêm", onClick = {
                val movie = Movie(
                    title = title,
                    des = description,
                    genre = selectedGenre,
                    duration = duration.toInt(),
                    poster = posterUrl,
                    cast = cast,
                    rating = 0.0f,
                    director = director,
                    startDate = start,
                    endDate = end,
                    category = selectedCate,
                    trailer = trailer
                )
                movieViewModel.newMovie(movie) { success ->
                    if (success)
                        Toast.makeText(context, "Thêm phim thành công", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(context, "Thêm phim thất bại", Toast.LENGTH_SHORT).show()

                }
            })

        }
    }


}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> LabeledDropdown(
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemToText: (T) -> String,
    icon: ImageVector = Icons.Default.ArrowDropDown
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedItem?.let { itemToText(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(icon, contentDescription = null) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemToText(item)) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
@Preview
fun previewMovieList() {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .customShadow(
                    color = boxshadow,
                    alpha = 0.2f,
                    borderRadius = 16.dp,
                    shadowRadius = 10.dp,
                    offsetX = 4.dp,
                    offsetY = 4.dp
                )
                .clip(RoundedCornerShape(5.dp))
                .padding(8.dp) // Thêm padding để tạo khoảng cách giữa bóng đổ và nội dung
        )
        {
            Card(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxSize()
//                    .clickable { movieViewModel.fetchMoviesByCate(cate.id) }
                ,
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = boxshadow
                )

            ) {
                Row {
                    Box(modifier = Modifier.weight(1f)) {

                        Image(
                            painter = painterResource(id = R.drawable.batman),
                            contentDescription = "Movie Poster",
                            contentScale = ContentScale.Fit, // hoặc ContentScale.FillWidth nếu muốn nó lấp đầy theo chiều ngang
                            modifier = Modifier
                                .fillMaxSize() // Hoặc .fillMaxSize() nếu muốn chiếm toàn bộ không gian cha
                                .size(400.dp) // Có thể điều chỉnh chiều cao theo nhu cầu
                        )

                    }
                    Box(modifier = Modifier.weight(3f)) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            BoldTextComponent("title")
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))

    }
}
