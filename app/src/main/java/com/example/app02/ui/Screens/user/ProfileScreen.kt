package com.example.app02.ui.Screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.app02.data.DataStore
import com.example.app02.network.api.TicketDTO
import com.example.app02.ui.Screens.admin.MovieManage.NewMovieScreen
import com.example.app02.ui.components.BoldTextComponent
import com.example.app02.ui.components.CommonHeader
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.CustomTabRow
import com.example.app02.ui.components.LightTextComponent
import com.example.app02.ui.components.RowComponent
import com.example.app02.ui.components.customShadow
import com.example.app02.ui.theme.bcl
import com.example.app02.ui.theme.boxshadow
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.TicketViewModel
import com.example.app02.viewmodel.UserViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    dataStore: DataStore,
    loginViewModel: LoginViewModel,
    ticketViewModel: TicketViewModel,
    role: String
) {
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val user by userViewModel.user.collectAsState()
    val tickets by ticketViewModel.tickets.collectAsState()

    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val tabTitles = listOf("Thông tin ", "Lịch sử vé")

    LaunchedEffect(Unit) {
        val id = dataStore.getUserIdFromToken()
        id?.let {
            userViewModel.fetchUserInfo(it)
            ticketViewModel.getTicketsByUser(it) {}
        }

    }

    CommonScaffold(isLogged, navController, loginViewModel, "users") { p ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(p)
        ) {
            user?.let {
                CustomTabRow(
                    tabTitles = tabTitles,
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { index -> selectedTabIndex = index }
                )
                CommonHeader("", navController)
                when (selectedTabIndex) {
                    0 -> {
                        LazyColumn(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                Spacer(modifier = Modifier.padding(16.dp))
                                BoldTextComponent("Thông tin tài khoản")
                                Spacer(modifier = Modifier.padding(16.dp))
                            }
                            item { RowComponent("Tên tài khoản", it.name) }
                            item { RowComponent("Email", it.email) }
                            item { RowComponent("Số điện thoại", it.phone) }
                        }
                    }

                    1 -> {
                        if (tickets == null) {
                            LoadingIndicator()
                        } else {
                            LazyColumn(
                                modifier = Modifier,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                items(tickets) { ticket ->
                                    TicketCard(ticket)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun TicketCard(ticket: TicketDTO) {
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

    )
    {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = boxshadow
            )

        ) {
            Row {
                Box(modifier = Modifier.weight(1f)) {
                    Image(
                        painter = rememberAsyncImagePainter(model = ticket.qr),
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
                            ticket.movieName,
                            style = TextStyle(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.LightGray
                            )
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        LightTextComponent("Thanh toán lúc: ${ticket.payTime}")
                    }
                }
            }
        }
    }

}