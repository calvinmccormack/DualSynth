package com.calvinmccormack.dualsynthpd.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown

@Composable
fun InputMappingUI() {
    val inputMappings = listOf(
        "buttonSouth" to "Button",
        "buttonEast" to "Button",
        "leftStickX" to "Joystick",
        "rightStickY" to "Joystick"
    )

    val buttonTargets = listOf("triggerSample1", "tapTempo", "transportStart", "pitchUp")
    val floatTargets = listOf("filterCutoff", "reverbWet", "delayTime", "flangeDepth")

    var sendToPd by remember { mutableStateOf(true) }
    var sendToOsc by remember { mutableStateOf(false) }
    var oscIp by remember { mutableStateOf("192.168.1.7") }
    var oscPort by remember { mutableStateOf("8000") }

    Column(Modifier.padding(16.dp)) {
        Text("Global Settings", style = MaterialTheme.typography.titleLarge)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SwitchWithLabel("Send to Pd", sendToPd) { sendToPd = it }
            SwitchWithLabel("Send to OSC", sendToOsc) { sendToOsc = it }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = oscIp,
            onValueChange = { oscIp = it },
            label = { Text("OSC IP Address") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = oscPort,
            onValueChange = { oscPort = it },
            label = { Text("OSC Port") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))
        Text("Input Mappings", style = MaterialTheme.typography.titleLarge)

        inputMappings.forEach { (inputName, type) ->
            var selectedTarget by remember { mutableStateOf("") }
            val options = if (type == "Button") buttonTargets else floatTargets

            Column(Modifier.padding(vertical = 8.dp)) {
                Text("$inputName ($type)")
                DropdownSelector(options, selectedTarget) { selectedTarget = it }
            }
        }
    }
}

@Composable
fun SwitchWithLabel(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Switch(checked = checked, onCheckedChange = onCheckedChange)
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}

@Composable
fun DropdownSelector(options: List<String>, selected: String, onSelectedChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Assign to") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { label ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onSelectedChange(label)
                        expanded = false
                    }
                )
            }
        }
    }
}