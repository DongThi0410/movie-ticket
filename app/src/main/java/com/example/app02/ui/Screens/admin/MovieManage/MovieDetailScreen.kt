package com.example.app02.ui.Screens.admin.MovieManage

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.app02.network.api.MovieUpdateDTO
import com.example.app02.network.models.Movie
import com.example.app02.ui.components.Btn
import com.example.app02.ui.components.CommonHeader
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.MyTextField
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.MovieViewModel

@Composable
fun MovieDetailScreen(
    id: Int,
    navController: NavController,
    movieViewModel: MovieViewModel,
    loginViewModel: LoginViewModel
) {
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val delMovie by movieViewModel.delMovie.collectAsState()
    val movie by movieViewModel.selectedMovie.collectAsState()
    LaunchedEffect(Unit) {
        movieViewModel.fetchMovieDetails(id)

    }

    CommonScaffold(isLogged, navController, loginViewModel, "admin") { p ->
        movie?.let {
            Column(modifier = Modifier.padding(p)) {
                CommonHeader(it.title, navController)
                MovieDetailContent(id, it, movieViewModel)
            }
        }
    }
}

@Composable
fun MovieDetailContent(id: Int, movie: Movie?, movieViewModel: MovieViewModel) {

    val context = LocalContext.current
    movie?.let {
        var title by remember { mutableStateOf(movie.title) }
        var des by remember { mutableStateOf(movie.des) }
        var duration by remember { mutableStateOf(movie.duration) }
        var director by remember { mutableStateOf(movie.director) }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            MyTextField(
                "Mô tả",
                leadingIconVector = Icons.Default.Edit,
                des,
                onValueChange = { des = it })
            MyTextField(
                "Thoi luong",
                leadingIconVector = Icons.Default.Edit,
                duration.toString(),
                onValueChange = { duration = it.toInt() })
            MyTextField(
                "Dao dien",
                leadingIconVector = Icons.Default.Edit,
                director,
                onValueChange = { director = it })
            Btn("Cập nhật", onClick = {
                val movieUpdate = MovieUpdateDTO(des, duration = duration, director = director)
                movieViewModel.update(id, movieUpdate) { success ->
                    movieViewModel.update(id, movieUpdate) { success ->
                        if (success) {
                            Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            })

            IconButton(
                modifier = Modifier.fillMaxWidth(0.45f)
                    .height(45.dp).background(
                    Color.Red,
                    shape = RoundedCornerShape(50.dp)
                ),
                onClick = {
                    movieViewModel.deleteMov(id) { success ->
                        if (success) {
                            Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show()
                        }

                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = "delete",
                    tint = Color.White
                )

            }

        }
    } ?: Text(text = "Không tìm thấy phim", modifier = Modifier.padding(16.dp))
}
