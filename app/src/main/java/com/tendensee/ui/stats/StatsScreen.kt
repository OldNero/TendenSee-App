package com.tendensee.ui.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.tendensee.viewmodel.HabitViewModel
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun StatsScreen(viewModel: HabitViewModel) {
    val records by viewModel.getRecentRecords(7).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Your Progress", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)

        val entries = records
            .groupBy { LocalDate.ofInstant(java.time.Instant.ofEpochMilli(it.timestamp), ZoneId.systemDefault()) }
            .mapValues { (_, records) -> records.count { it.isCompleted } }
            .map { (date, count) -> BarEntry(date.dayOfWeek.value.toFloat(), count.toFloat()) }

        val dataSet = BarDataSet(entries, "Habits Completed")
        val barData = BarData(dataSet)

        AndroidView(
            factory = { context ->
                BarChart(context).apply {
                    data = barData
                    description.isEnabled = false
                    xAxis.valueFormatter = DayAxisValueFormatter()
                    invalidate()
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
