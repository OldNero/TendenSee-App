package com.tendensee.ui.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tendensee.ui.components.ButtonVariant
import com.tendensee.ui.components.ShadcnButton
import com.tendensee.ui.components.ShadcnInput
import com.tendensee.ui.theme.Purple400
import com.tendensee.ui.theme.SeaBlue
import com.tendensee.ui.theme.SeaGreen
import com.tendensee.viewmodel.HabitViewModel
import com.vanniktech.emoji.Emoji
import com.vanniktech.emoji.compose.EmojiPicker
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    navController: NavController,
    viewModel: HabitViewModel
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Regular Habit", "One-time Task")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Habit") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(text = { Text(title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }
            when (selectedTabIndex) {
                0 -> RegularHabitTab(navController, viewModel)
                1 -> OneTimeTaskTab(navController, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegularHabitTab(navController: NavController, viewModel: HabitViewModel) {
    var habitName by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf<Emoji?>(null) }
    var showEmojiPicker by remember { mutableStateOf(false) }
    val colors = listOf(Purple400, SeaBlue, SeaGreen, Color.Red, Color.Yellow, Color.Blue)
    var selectedColor by remember { mutableStateOf(colors.first()) }
    var repeatOption by remember { mutableStateOf("Daily") }
    val repeatOptions = listOf("Daily", "Weekly", "Monthly")
    var allDay by remember { mutableStateOf(false) }
    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
    var selectedDays by remember { mutableStateOf(emptySet<String>()) }
    var weeklyRepeat by remember { mutableStateOf("1") }
    val timeOfDay = listOf("Morning", "Afternoon", "Evening")
    var selectedTimeOfDay by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf<LocalTime?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ShadcnInput(
                value = habitName,
                onValueChange = { habitName = it },
                label = "Habit Name",
                placeholder = "e.g., Read 30 mins"
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                ShadcnButton(
                    onClick = { showEmojiPicker = true },
                    text = selectedEmoji?.unicode ?: "Select Icon"
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.forEach { color ->
                        ColorBox(color = color, isSelected = selectedColor == color) {
                            selectedColor = color
                        }
                    }
                }
            }
        }

        item {
            Text("Repeat", style = MaterialTheme.typography.titleSmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeatOptions.forEach { option ->
                    ShadcnButton(
                        onClick = { repeatOption = option },
                        text = option,
                        variant = if (repeatOption == option) ButtonVariant.Primary else ButtonVariant.Outline,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        when (repeatOption) {
            "Daily" -> {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = allDay, onCheckedChange = { allDay = it })
                        Text("All day")
                    }
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        daysOfWeek.forEach { day ->
                            val isSelected = selectedDays.contains(day)
                            ShadcnButton(
                                onClick = {
                                    selectedDays = if (isSelected) {
                                        selectedDays - day
                                    } else {
                                        selectedDays + day
                                    }
                                },
                                text = day,
                                variant = if (isSelected) ButtonVariant.Primary else ButtonVariant.Outline
                            )
                        }
                    }
                }
            }
            "Weekly" -> {
                item {
                    ShadcnInput(
                        value = weeklyRepeat,
                        onValueChange = { if (it.all { char -> char.isDigit() }) weeklyRepeat = it },
                        label = "Days per week repeat",
                        placeholder = "e.g., 3"
                    )
                }
            }
            "Monthly" -> {
                item {
                    Text("Calendar selection will be here") // Placeholder
                }
            }
        }

        item {
            Text("Do it at", style = MaterialTheme.typography.titleSmall)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                timeOfDay.forEach { time ->
                    ShadcnButton(
                        onClick = { selectedTimeOfDay = time },
                        text = time,
                        variant = if (selectedTimeOfDay == time) ButtonVariant.Primary else ButtonVariant.Outline,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ShadcnButton(onClick = { showDatePicker = true }, text = endDate?.toString() ?: "End Date")
                ShadcnButton(onClick = { /* TODO */ }, text = "After X days")
            }
        }

        item {
            ShadcnButton(onClick = { showTimePicker = true }, text = reminderTime?.toString() ?: "Reminder")
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            ShadcnButton(
                onClick = { /* TODO */ },
                text = "Create Habit",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showEmojiPicker) {
        EmojiPicker(
            onEmojiClick = { emoji ->
                selectedEmoji = emoji
                showEmojiPicker = false
            },
            onDismiss = { showEmojiPicker = false }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        endDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        DatePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    reminderTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@Composable
fun ColorBox(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() }
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            )
    )
}

@Composable
fun OneTimeTaskTab(navController: NavController, viewModel: HabitViewModel) {
    Text("One-time Task content goes here", modifier = Modifier.fillMaxSize())
}
