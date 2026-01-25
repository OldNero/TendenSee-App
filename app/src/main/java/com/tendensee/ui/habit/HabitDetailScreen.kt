package com.tendensee.ui.habit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tendensee.data.Habit
import com.tendensee.ui.components.ExtendedHeatmap
import com.tendensee.ui.components.ShadcnCard
import com.tendensee.ui.components.StatCard
import com.tendensee.utils.StatsCalculator
import com.tendensee.viewmodel.HabitViewModel
import kotlinx.coroutines.launch

// Add the following dependency to your build.gradle file:
// implementation "androidx.compose.material3:material3:1.0.0"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    navController: NavController,
    viewModel: HabitViewModel,
    habitId: Int
) {
    var habit by remember { mutableStateOf<Habit?>(null) }
    val records by viewModel.getAllRecordsForHabit(habitId).collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(habitId) {
        habit = viewModel.getHabitById(habitId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(habit?.title ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        if (habit == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Habit Insights",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val currentStreak = StatsCalculator.calculateCurrentStreak(records)
                    val bestStreak = StatsCalculator.calculateBestStreak(records)
                    
                    StatCard(
                        label = "Current Streak",
                        value = "$currentStreak",
                        subValue = "days",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Best Streak",
                        value = "$bestStreak",
                        subValue = "days",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ShadcnCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Last 90 Days",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        ExtendedHeatmap(records = records)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Completion Rates",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val rate7d = StatsCalculator.calculateCompletionRate(records, 7)
                    val rate30d = StatsCalculator.calculateCompletionRate(records, 30)

                    StatCard(
                        label = "7 Days",
                        value = "${rate7d.toInt()}%",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "30 Days",
                        value = "${rate30d.toInt()}%",
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (habit?.description?.isNotBlank() == true) {
                    Text(
                        text = "About this habit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = habit?.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}