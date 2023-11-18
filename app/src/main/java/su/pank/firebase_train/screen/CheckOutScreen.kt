package su.pank.firebase_train.screen

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import su.pank.firebase_train.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckOutScreen(vm: UserViewModel) {
    var timePickerDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var datePickerDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val timePickerState = rememberTimePickerState()
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        vm.time = LocalTime(timePickerState.hour, timePickerState.minute)
    }
    LaunchedEffect(datePickerState.selectedDateMillis) {
        if (datePickerState.selectedDateMillis != null)
            vm.date =
                LocalDate.fromEpochDays((datePickerState.selectedDateMillis!! / (60 * 60 * 1000 * 24)).toInt())
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = vm.date.toString(),
            onValueChange = {},
            label = { Text(text = "Дата") },
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                datePickerDialog = true
                            }
                        }
                    }
                }, readOnly = true
        )
        OutlinedTextField(
            value = vm.time.toString(),
            onValueChange = {},
            label = { Text(text = "Время") },
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                timePickerDialog = true
                            }
                        }
                    }
                }, readOnly = true
        )
        OutlinedTextField(value = vm.location, onValueChange = {
            vm.location = it
        }, label = { Text(text = "Место") })

    }
    if (timePickerDialog) {
        Dialog(onDismissRequest = { timePickerDialog = false }) {
            Column {
                TimePicker(state = timePickerState)
                Button(onClick = { timePickerDialog = false }, modifier = Modifier.alignBy {
                    it.measuredWidth
                }) {
                    Text(text = "OK")
                }
            }
        }
    }
    if (datePickerDialog) {
        Dialog(onDismissRequest = { datePickerDialog = false }) {
            Surface {
                Column {
                    DatePicker(state = datePickerState)
                    Button(onClick = { datePickerDialog = false }, modifier = Modifier.alignBy {
                        it.measuredWidth
                    }) {
                        Text(text = "OK")
                    }
                }
            }
        }
    }
}