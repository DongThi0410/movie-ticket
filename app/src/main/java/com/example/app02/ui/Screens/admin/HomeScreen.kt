package com.example.app02.ui.Screens.admin

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView

import androidx.navigation.NavController
import com.example.app02.network.api.RevenueDTO
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.LightTextComponent
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.RevenueViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

import java.text.NumberFormat
import java.util.*

@Composable
fun HomeScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    revenueViewModel: RevenueViewModel
) {
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val revenues by revenueViewModel.revenues.collectAsState()
    LaunchedEffect (Unit){
        revenueViewModel.fetchRevenues()
    }
    CommonScaffold(isLogged, navController, loginViewModel, "admin") { padding ->
        Column(modifier = Modifier.padding(padding).background(androidx.compose.ui.graphics.Color.White)) {
            RevenueDaily(revenues)
            Spacer(modifier = Modifier.padding(8.dp))
            RevenuePieChart(revenues)
        }
        revenues.map { revenue ->

            RevenueItem(revenue.date.toString(), revenue.total )
        }
    }
}
@Composable
fun RevenueItem(date: String, total: Double) {
    val formatter = remember { NumberFormat.getInstance(Locale("vi", "VN")) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LightTextComponent(date)
        LightTextComponent("${formatter.format(total)} đ")
    }
}

@Composable
fun RevenueDaily(revenues: List<RevenueDTO>) {
    if (revenues.isEmpty()) {
        LightTextComponent("Chưa có dữ liệu.")
        return
    }

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                axisRight.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)
                axisLeft.setDrawGridLines(false)
                legend.isEnabled = false
            }
        },
        update = { chart ->
            val entries = revenues.mapIndexed { index, revenue ->
                Entry(index.toFloat(), revenue.total.toFloat())
            }

            val dataSet = LineDataSet(entries, "Doanh thu").apply {
                color = android.graphics.Color.BLUE

                valueTextColor = android.graphics.Color.WHITE
                lineWidth = 2f
                setDrawCircles(true)
                setDrawValues(false)
                setDrawFilled(true)
                fillColor = android.graphics.Color.CYAN
            }

            chart.data = LineData(dataSet)

            chart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt().coerceIn(revenues.indices)
                    return revenues.getOrNull(index)?.date ?: ""
                }
            }

            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // ✅ GIỚI HẠN CHIỀU CAO Ở ĐÂY
    )

}
@Composable
fun RevenuePieChart(revenues: List<RevenueDTO>) {
    AndroidView(
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                setEntryLabelTextSize(12f)
                setUsePercentValues(true)
                legend.isEnabled = true
            }
        },
        update = { chart ->
            if (revenues.isEmpty()) {
                chart.clear()
                return@AndroidView
            }

            val entries = revenues.map {
                PieEntry(it.total.toFloat(), it.date)
            }

            val dataSet = PieDataSet(entries, "Doanh thu theo ngày").apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList()
                valueTextColor = android.graphics.Color.WHITE
                valueTextSize = 14f
            }

            val data = PieData(dataSet).apply {
                setValueFormatter(PercentFormatter(chart))
            }

            chart.data = data
            chart.invalidate()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

@Composable
@Preview
fun RevenueChartPreview() {
    val fakeRevenues = listOf(
        RevenueDTO("05-01", 500.0),
        RevenueDTO("05-02", 1000.0),
        RevenueDTO("05-03", 1500.0),
        RevenueDTO("05-04", 800.0),
        RevenueDTO("05-05", 1300.0)
    )

    RevenueDaily(fakeRevenues)
}
