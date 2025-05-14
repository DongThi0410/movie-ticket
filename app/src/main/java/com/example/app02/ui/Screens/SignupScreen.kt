package com.example.app02.ui.Screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app02.ui.components.NormalTextComponent
import com.example.app02.ui.theme.bcl
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.app02.R
import com.example.app02.ui.components.Btn
import com.example.app02.ui.components.ClickableLoginTextComponent
import com.example.app02.ui.components.MyTextField
import com.example.app02.ui.components.MyTextFieldPassword
import com.example.app02.ui.components.checkBox
import com.example.app02.ui.components.lineDivide
import com.example.app02.nav.Screen
import com.example.app02.ui.components.BoldTextComponent
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.SignupViewModel
import org.json.JSONObject


@Composable
fun SignupScreen(navController: NavController, signupViewModel: SignupViewModel = viewModel()) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val state by signupViewModel.signupState


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(color = bcl)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(color = bcl)
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            val imageModifier = Modifier
                .size(150.dp)
                .border(
                    BorderStroke(2.dp, Color.Transparent),
                    shape = RoundedCornerShape(15.dp) // Thêm bo tròn viền
                )
//                .offset(y = (-50).dp)
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                contentScale = ContentScale.Inside,
                modifier = imageModifier
            )
            BoldTextComponent("Đăng ký")
            Spacer(modifier = Modifier.height(20.dp))

            MyTextField("Ho va ten", leadingIconVector = Icons.Default.Person, value = name.value,
                onValueChange = { name.value = it })
            Spacer(modifier = Modifier.height(10.dp))
            MyTextField("Email", leadingIconVector = Icons.Default.Email, value = email.value,
                onValueChange = { email.value = it })
            MyTextField("Số điện thoại",
                leadingIconVector = Icons.Default.Email,
                value = phone.value,
                onValueChange = { phone.value = it })
            Spacer(modifier = Modifier.height(10.dp))
            MyTextFieldPassword("Mật khẩu",
                leadingIconVector = Icons.Default.Lock,
                value = password.value,
                onValueChange = { password.value = it })

            checkBox(value = "Chấp nhận với các điều khoản của chúng tôi.", onTextSelected = {
                navController.navigate(Screen.Conditions.route) // Đảm bảo rằng Screen.SignUpScreen có thuộc tính route
            })
            Spacer(modifier = Modifier.height(30.dp))

            Btn(
                value = "Đăng ký",
                onClick = {
                    if (email.value.isBlank() || password.value.isBlank()) {
                        errorMessage = "Vui lòng nhập đầy đủ thông tin!"
                    } else {
                        errorMessage = ""
                        signupViewModel.signup(name.value, email.value, phone.value, password.value)
                    }
                }
                       , isLoading = state.isLoading

            )
            if (state.isLoading) {
                CircularProgressIndicator()
            }
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            val errorText = state.errorMessage?.let {
                try {
                    JSONObject(it).getString("message") // Lấy nội dung từ key "message"
                } catch (e: Exception) {
                    it // Nếu có lỗi, giữ nguyên nội dung
                }
            }

            state.message?.let { msg ->
                Text(
                    text = msg,
                    color = Color.Green,
                    modifier = Modifier.padding(8.dp)
                )
            }

            state.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }

            if (!state.isLoading) {
                errorText?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
            lineDivide()
            ClickableLoginTextComponent("Bạn đã có tài khoản?", "Đăng nhập", onTextSelected = {
                navController.navigate(Screen.Login.route) // Đảm bảo rằng Screen.SignUpScreen có thuộc tính route

            })
        }


    }

}
@Preview(showBackground = true)
@Composable
fun DefaultPreviewOfSignUpScreen() {
    val navController = rememberNavController() // Tạo NavController giả để tránh lỗi
    SignupScreen(navController = navController)
}

