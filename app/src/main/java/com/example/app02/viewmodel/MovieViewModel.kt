package com.example.app02.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app02.network.api.MovieSearch
import com.example.app02.network.api.MovieUpdateDTO
import com.example.app02.network.api.RetrofitInstance
import com.example.app02.network.models.Movie
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import retrofit2.http.Query

class MovieViewModel : ViewModel() {
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies = _movies.asStateFlow()


    private val _selectedMovie = MutableStateFlow<Movie?>(null)
    val selectedMovie = _selectedMovie.asStateFlow()

    private val _filterMovies = MutableStateFlow<List<Movie>>(emptyList())
    val filterMovies = _filterMovies.asStateFlow()

    private val _delMovie = MutableStateFlow<String>("")
    val delMovie = _delMovie.asStateFlow()

    var newMovieState = mutableStateOf(NewMovieState())

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

//    init {
//        viewModelScope.launch {
//            _query
//                .debounce(300)
//                .filter { it.isNotBlank() }
//                .distinctUntilChanged()
//                .collect { key ->
//                    _resultSearch.value = search(key)
//                }
//        }
//    }

    fun fetchMovies() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.getMovies()
                _movies.value = response
            } catch (e: Exception) {
                println("Lỗi khi lấy danh sách phim: ${e.message}")
            }
        }
    }

    fun fetchMoviesByCate(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.getMovieByCate(id)
                _movies.value = response ?: emptyList()
            } catch (e: Exception) {
                println("Lỗi khi lấy phim theo danh mục: ${e.message}")
            }
        }
    }

    fun fetchMovieDetails(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.getMovieById(id)
                _selectedMovie.value = response
            } catch (e: Exception) {
                println("Lỗi khi lấy chi tiết phim: ${e.message}")
            }
        }
    }

    fun deleteMov(id: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.deleteMovie(id)
                if (response.isSuccessful) {
                    val msg = response.body()?.string() ?: "Xóa phim thành công"
                    _delMovie.value = msg
                    onResult(true)
                } else {
                    val msg = response.errorBody()?.string() ?: "Xóa phim không thành công"
                    _delMovie.value = msg
                    onResult(false)
                }
            } catch (e: Exception) {
                _delMovie.value = "Xóa phim không thành công "
                onResult(false)
                throw Exception(e.message)
            }
        }
    }

    fun update(id: Int, updateMovie: MovieUpdateDTO, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.updateMovie(id, updateMovie)
                if (response.isSuccessful) {
                    val msg = response.body()?.string() ?: "Cập nhật phim thành công"
                    _delMovie.value = msg
                    onResult(true)
                } else {
                    val msg = response.errorBody()?.string() ?: "Cập nhật phim không thành công"
                    _delMovie.value = msg
                    onResult(false)
                }
            } catch (e: Exception) {
                _delMovie.value = "Cập nhật phim không thành công "
                onResult(false)
                throw Exception(e.message)
            }
        }
    }

    fun newMovie(movie: Movie, onResult: (Boolean) -> Unit) {
        newMovieState.value = NewMovieState(isLoading = true)
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.newMovie(movie)
                Log.d("MovieDebug", "Movie gửi lên: ${Gson().toJson(movie)}")
                if (response.isSuccessful) {
                    val msg = response.body()?.string() ?: "Thêm phim mới thành công"
                    newMovieState.value = NewMovieState(isLoading = false, msg)
                } else {
                    val msg = response.errorBody()?.string() ?: "Thêm phim mới thất "
                    newMovieState.value = NewMovieState(
                        isLoading = false, errorMessage = msg
                    )
                }
            } catch (e: Exception) {
                newMovieState.value = NewMovieState(isLoading = false, errorMessage = e.message)
            }
        }
    }

     fun search(query: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.searchMovie(query)
                _movies.value = response ?: emptyList()
            } catch (e: Exception) {
                println("Lỗi khi tìm kiếm phim ${e}")
            }
        }
    }
}

data class NewMovieState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val errorMessage: String? = null
)