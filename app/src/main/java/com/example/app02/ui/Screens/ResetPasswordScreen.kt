package com.example.app02.ui.Screens


import android.widget.Toast
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.app02.R
import com.example.app02.data.DataStore
import com.example.app02.nav.Screen
import com.example.app02.network.api.ResetPasswordRequest
import com.example.app02.ui.components.BoldTextComponent
import com.example.app02.ui.components.Btn
import com.example.app02.ui.components.ClickableLoginTextComponent
import com.example.app02.ui.components.MyTextField
import com.example.app02.ui.components.MyTextFieldPassword
import com.example.app02.ui.components.NormalTextComponent
import com.example.app02.ui.components.lineDivide
import com.example.app02.ui.theme.bcl
import com.example.app02.ui.theme.red
import com.example.app02.viewmodel.LoginState
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ResetPasswordScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val dataStore = remember { DataStore(context) }

    val imageModifier = Modifier
        .size(150.dp)
        .border(
            BorderStroke(2.dp, Color.Transparent),
            shape = RoundedCornerShape(15.dp) // Thêm bo tròn viền
        )
    LaunchedEffect(Unit) {
        loginViewModel.loginState.collectLatest { state ->
            when (state) {
                is LoginState.Success -> {
                    println("TOKEN: ${state.token}")
                }

                is LoginState.Error -> {
                    println("${state.message}")
                }

                else -> {}
            }

        }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(color = bcl)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = bcl)
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {

            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                BoldTextComponent("Quên mật khẩu")
            }
            Spacer(modifier = Modifier.height(20.dp))


            MyTextField("Email", leadingIconVector = Icons.Default.Email, value = email,
                onValueChange = { email = it })
            Spacer(modifier = Modifier.height(10.dp))
            MyTextField("OTP", leadingIconVector = Icons.Default.Numbers, value = otp,
                onValueChange = { otp = it })
            Spacer(modifier = Modifier.height(10.dp))
            MyTextFieldPassword("Mật khẩu mới",
                leadingIconVector = Icons.Default.Lock,
                value = password,
                onValueChange = { password = it })



            Spacer(modifier = Modifier.height(40.dp))
            Btn(
                value = "Gửi mã OTP",
                onClick = {
                    userViewModel.forgotPassword(email) { success ->
                        note = "Mã OTP đã được gửi về email"
                    }
                }
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                Btn(
                    value = "Cập nhật",
                    onClick = {
                        val dto = ResetPasswordRequest(email, otp, password)
                        userViewModel.resetPassword(dto) { success ->
                            if (success) {
                                note = "Đặt lại mật khẩu thành công"
                                Toast.makeText(context, "Đặt lại mật khẩu thành công", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.Login.route)
                            }
                        }
                    }
                )
            }


            Text(note, modifier = Modifier, style = TextStyle(Color.Green))

            Spacer(modifier = Modifier.height(16.dp))


        }


    }

}



