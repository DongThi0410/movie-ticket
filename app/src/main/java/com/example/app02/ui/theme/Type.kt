package com.example.app02.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.app02.R


val pt_root_ui_regular = FontFamily(Font(R.font.pt_root_ui_regular))
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = pt_root_ui_regular,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.2.sp
    ),
    titleLarge = TextStyle(
        fontFamily = pt_root_ui_regular,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 35.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = pt_root_ui_regular,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )
)