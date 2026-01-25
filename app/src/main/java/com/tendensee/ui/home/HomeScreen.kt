package com.tendensee.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tendensee.ui.components.ButtonVariant
import com.tendensee.ui.components.ShadcnButton
import com.tendensee.ui.components.ShadcnInput
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tendensee.data.Habit
import com.tendensee.ui.components.ShadcnCard
import com.tendensee.ui.navigation.Screen
import com.tendensee.viewmodel.HabitViewModel
import java.time.LocalDate
import java.time.ZoneId
import com.tendensee.data.HabitRecord
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HabitViewModel
) {
    val habits by viewModel.allHabits.collectAsState()
    val today = LocalDate.now()
    
    val startOfLookback = today.minusDays(30)
    val recordsFlow = remember(today) { 
        viewModel.getRecentRecords(30)
    }
    val allRecentRecords by recordsFlow.collectAsState(initial = emptyList())
    
    val recordsByHabit = allRecentRecords.groupBy { it.habitId }
    val completedTodayIds = allRecentRecords.filter { 
        LocalDate.ofInstant(java.time.Instant.ofEpochMilli(it.timestamp), ZoneId.systemDefault()) == today 
    }.map { it.habitId }.toSet()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { navController.navigate(Screen.Stats.route) }) {
                    Icon(Icons.Outlined.Analytics, contentDescription = "Stats")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddHabit.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "My Habits",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(habits) { habit ->
                    HabitItem(
                        habit = habit,
                        isDone = completedTodayIds.contains(habit.id),
                        onToggle = { isDone, note ->
                            viewModel.toggleHabitCompletion(habit.id, today, isDone, note = note)
                        },
                        onItemClick = {
                            navController.navigate(Screen.HabitDetail.createRoute(habit.id))
                        },
                        recentRecords = recordsByHabit[habit.id] ?: emptyList()
                    )
                }
            }
        }
    }
}

@Composable
fun HabitItem(
    habit: Habit,
    isDone: Boolean,
    onToggle: (Boolean, String?) -> Unit,
    onItemClick: () -> Unit,
    recentRecords: List<HabitRecord> = emptyList()
) {
    val strength = com.tendensee.utils.HabitStrengthCalculator.calculateStrength(recentRecords)
    var showNoteDialog by remember { mutableStateOf(false) }
    var tempNote by remember { mutableStateOf("") }
    
    if (showNoteDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showNoteDialog = false },
            title = { Text("Add Note for ${habit.title}") },
            text = {
                ShadcnInput(
                    value = tempNote,
                    onValueChange = { tempNote = it },
                    label = "How did it go?",
                    placeholder = "e.g. Travel day, sick..."
                )
            },
            confirmButton = {
                ShadcnButton(
                    onClick = {
                        onToggle(true, tempNote)
                        showNoteDialog = false
                    },
                    text = "Save & Done"
                )
            },
            dismissButton = {
                ShadcnButton(
                    onClick = { showNoteDialog = false },
                    text = "Cancel",
                    variant = ButtonVariant.Outline
                )
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    ShadcnCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showNoteDialog = true } // Click to add note
                ) {
                    Text(
                        text = habit.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                        color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Strength: $strength%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // One-tap check-in button (Circular)
                androidx.compose.material3.IconButton(
                    onClick = { onToggle(!isDone, null) },
                    colors = androidx.compose.material3.IconButtonDefaults.iconButtonColors(
                        containerColor = if (isDone) MaterialTheme.colorScheme.primary else Color.Transparent,
                        contentColor = if (isDone) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .border(
                            width = if (isDone) 0.dp else 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Check-in",
                        modifier = Modifier.padding(2.dp) // Reduced size padding for 20.dp equivalent icon size
                    )
                }
            }
            
            // Show latest note if present
            recentRecords.firstOrNull { 
                LocalDate.ofInstant(java.time.Instant.ofEpochMilli(it.timestamp), ZoneId.systemDefault()) == LocalDate.now() 
            }?.note?.let { note ->
                if (note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Note: $note",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            // Heatmap Row (Last 7 days)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val today = LocalDate.now()
                val recordDates = recentRecords.map {
                    LocalDate.ofInstant(java.time.Instant.ofEpochMilli(it.timestamp), ZoneId.systemDefault())
                }.toSet()

                for (i in 6 downTo 0) {
                    val date = today.minusDays(i.toLong())
                    val isDayDone = recordDates.contains(date)
                    
                    Column( // Changed from Box to Column to fix missing Box import issue based on previous build errors
                        modifier = Modifier
                            .width(12.dp) // Using width instead of size for simplicity if size causes issues
                            .height(12.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (isDayDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                    ) {}
                }
            }
        }
    }
}