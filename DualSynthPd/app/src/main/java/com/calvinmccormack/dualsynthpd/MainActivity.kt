package com.calvinmccormack.dualsynthpd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import android.util.Log
import androidx.compose.ui.unit.sp
import android.content.Context

import com.calvinmccormack.dualsynthpd.ui.theme.DualSynthPdTheme

import java.io.File

import org.puredata.core.PdBase
import org.puredata.android.io.PdAudio

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Copy the .pd patches from assets to internal storage
        val patchFile = File(filesDir, "all_input_remixer.pd")
        assets.open("pd-patches/all_input_remixer.pd").use { input ->
            patchFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        val pitchShifterFile = File(filesDir, "pitchshifter~.pd")
        assets.open("pd-patches/pitchshifter~.pd").use { input ->
            pitchShifterFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        // Extract the audio file into a relative path
        val soundDir = File(filesDir, "sound")
        soundDir.mkdirs()

        val assetFiles = assets.list("pd-patches/sound") ?: emptyArray()

        for (filename in assetFiles) {
            val destFile = File(soundDir, filename)
            assets.open("pd-patches/sound/$filename").use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        // Initialize audio
        PdAudio.initAudio(44100, 0, 2, 8, true)

        // Open patch
        PdBase.openPatch(patchFile)

        // Open Pd Logger
        PdBase.setReceiver(PdLogReceiver())

        // Start audio
        PdAudio.startAudio(this)

        enableEdgeToEdge()
        setContent {
            DualSynthPdTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PdAudio.stopAudio()
        PdBase.release()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val label = when (keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> "Cross (X)"
            KeyEvent.KEYCODE_BUTTON_B -> "Circle (O)"
            KeyEvent.KEYCODE_BUTTON_X -> "Square"
            KeyEvent.KEYCODE_BUTTON_Y -> "Triangle"
            KeyEvent.KEYCODE_BUTTON_L1 -> "L1"
            KeyEvent.KEYCODE_BUTTON_R1 -> "R1"
            KeyEvent.KEYCODE_BUTTON_SELECT -> "Select"
            KeyEvent.KEYCODE_BUTTON_START -> "Start"
            KeyEvent.KEYCODE_BUTTON_THUMBL -> "L3"
            KeyEvent.KEYCODE_BUTTON_THUMBR -> "R3"
            else -> null
        }

        if (label != null) {
            Log.d("DualSynthPd", "$label pressed")
            InteractionRouter.updateInput(label, true)
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        val label = when (keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> "Cross (X)"
            KeyEvent.KEYCODE_BUTTON_B -> "Circle (O)"
            KeyEvent.KEYCODE_BUTTON_X -> "Square"
            KeyEvent.KEYCODE_BUTTON_Y -> "Triangle"
            KeyEvent.KEYCODE_BUTTON_L1 -> "L1"
            KeyEvent.KEYCODE_BUTTON_R1 -> "R1"
            KeyEvent.KEYCODE_BUTTON_SELECT -> "Select"
            KeyEvent.KEYCODE_BUTTON_START -> "Start"
            KeyEvent.KEYCODE_BUTTON_THUMBL -> "L3"
            KeyEvent.KEYCODE_BUTTON_THUMBR -> "R3"
            else -> null
        } // same switch as onKeyDown
        if (label != null) {
            InteractionRouter.updateInput(label, false)
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if ((event.source and InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK &&
            event.action == MotionEvent.ACTION_MOVE
        ) {

            // 🎚 Analog floats
            fun normalize(value: Float): Float = ((value + 1f) / 2f).coerceIn(0f, 1f)

            InteractionRouter.updateInput(
                "Left Stick →",
                normalize(event.getAxisValue(MotionEvent.AXIS_X))
            )
            InteractionRouter.updateInput(
                "Left Stick ↑",
                normalize(event.getAxisValue(MotionEvent.AXIS_Y))
            )
            InteractionRouter.updateInput(
                "Right Stick →",
                normalize(event.getAxisValue(MotionEvent.AXIS_Z))
            )
            InteractionRouter.updateInput(
                "Right Stick ↑",
                normalize(event.getAxisValue(MotionEvent.AXIS_RZ))
            )
            InteractionRouter.updateInput(
                "L2 Trigger",
                event.getAxisValue(MotionEvent.AXIS_LTRIGGER).coerceIn(0f, 1f)
            )
            InteractionRouter.updateInput(
                "R2 Trigger",
                event.getAxisValue(MotionEvent.AXIS_RTRIGGER).coerceIn(0f, 1f)
            )

            // 🎯 DPad hat switches
            val hatX = event.getAxisValue(MotionEvent.AXIS_HAT_X)
            val hatY = event.getAxisValue(MotionEvent.AXIS_HAT_Y)

            InteractionRouter.updateInput("DPadLeft", hatX < -0.5f)
            InteractionRouter.updateInput("DPadRight", hatX > 0.5f)
            InteractionRouter.updateInput("DPadUp", hatY < -0.5f)
            InteractionRouter.updateInput("DPadDown", hatY > 0.5f)

            return true
        }

        return super.onGenericMotionEvent(event)
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Live Monitor", "Mapping", "Presets")

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        when (selectedTab) {
            0 -> LiveMonitorUI(modifier = Modifier.padding(8.dp))
            1 -> InputMappingUI(modifier = Modifier.padding(8.dp))
            2 -> PresetManagerUI(context = androidx.compose.ui.platform.LocalContext.current, modifier = Modifier.padding(8.dp))
        }
    }
}

object MappingConfig {
    val buttonMappings = mutableStateMapOf<String, String>()
    val floatMappings = mutableStateMapOf<String, String>()

    init {
        buttonMappings.putAll(mapOf(
            "Cross (X)" to "startPlayback",
            "Circle (O)" to "stopPlayback",
            "Square" to "decreaseVoxVolume",
            "Triangle" to "increaseVoxVolume",
            "L1" to "decreasePitch",
            "R1" to "increasePitch",
            "L3" to "swapFxLStick",
            "R3" to "swapFxRStick",
            "DPadUp" to "triggerSample1",
            "DPadRight" to "triggerSample2",
            "DPadDown" to "triggerSample3",
            "DPadLeft" to "triggerSample4",
            "Select" to "decreaseVolume",
            "Start" to "increaseVolume"
        ))

        floatMappings.putAll(mapOf(
            "Left Stick ↑" to "Reverb",
            "Left Stick →" to "Delay",
            "Left Stick ↓" to "Bitcrush",
            "Left Stick ←" to "Distortion",
            "Right Stick ↑" to "GranShiftUp",
            "Right Stick →" to "Flange",
            "Right Stick ↓" to "GranShiftDown",
            "Right Stick ←" to "Chorus",
            "L2 Trigger" to "stutter1",
            "R2 Trigger" to "stutter2"
        ))
    }
}

@Composable
fun InputMappingUI(modifier: Modifier = Modifier) {
    val buttonInputs = MappingConfig.buttonMappings.keys.toList()
    val floatInputs = MappingConfig.floatMappings.keys.toList()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Map Buttons to Actions", fontSize = 12.sp)
        }

        items(buttonInputs) { label ->
            val displayLabel = label
            var expanded by remember { mutableStateOf(false) }
            var selectedOption by remember { mutableStateOf(MappingConfig.buttonMappings[label] ?: "") }

            Column {
                Text(text = displayLabel, fontSize = 10.sp)
                Box {
                    Button(
                        onClick = { expanded = true },
                        modifier = Modifier
                            .height(32.dp)
                            .padding(2.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(selectedOption, fontSize = 10.sp)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        MappingConfig.buttonMappings.values.toSet().forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedOption = option
                                    MappingConfig.buttonMappings[label] = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text("Map Joysticks/Triggers to Effects", fontSize = 12.sp)
        }

        items(floatInputs) { label ->
            val displayLabel = label
            var expanded by remember { mutableStateOf(false) }
            var selectedOption by remember { mutableStateOf(MappingConfig.floatMappings[label] ?: "") }

            Column {
                Text(text = displayLabel, fontSize = 10.sp)
                Box {
                    Button(
                        onClick = { expanded = true },
                        modifier = Modifier
                            .height(32.dp)
                            .padding(2.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(selectedOption, fontSize = 10.sp)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        MappingConfig.floatMappings.values.toSet().forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedOption = option
                                    MappingConfig.floatMappings[label] = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PresetManagerUI(context: Context, modifier: Modifier = Modifier) {
    var newName by remember { mutableStateOf("") }
    var presetList by remember { mutableStateOf(PresetManager.listPresets(context)) }
    var selectedPreset by remember { mutableStateOf(presetList.firstOrNull() ?: "") }

    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Save Current Mapping", fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextField(value = newName, onValueChange = { newName = it }, label = { Text("Preset Name") })
            Button(onClick = {
                val preset = InputPreset(newName, MappingConfig.buttonMappings.toMap(), MappingConfig.floatMappings.toMap())
                PresetManager.savePreset(context, preset)
                presetList = PresetManager.listPresets(context)
                newName = ""
            }) {
                Text("Save")
            }
        }


        HorizontalDivider(thickness = 1.dp, color = Color.Gray)

        Text("Load Preset", fontSize = 14.sp)
        var expanded by remember { mutableStateOf(false) }
        Box {
            Button(onClick = { expanded = true }) {
                Text(selectedPreset.ifEmpty { "Select Preset" })
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                presetList.forEach { name ->
                    DropdownMenuItem(text = { Text(name) }, onClick = {
                        val preset = PresetManager.loadPreset(context, name)
                        preset?.let {
                            MappingConfig.buttonMappings.clear()
                            MappingConfig.buttonMappings.putAll(it.buttonMappings)
                            MappingConfig.floatMappings.clear()
                            MappingConfig.floatMappings.putAll(it.floatMappings)
                            selectedPreset = name
                        }
                        expanded = false
                    })
                }
            }
        }
    }
}


@Composable
fun LiveMonitorUI(modifier: Modifier = Modifier) {
    // List of button labels and their corresponding pressed drawable resource IDs only
    val buttonImages = listOf(
        "Cross (X)" to R.drawable.dualsense_buttonsouth,
        "Circle (O)" to R.drawable.dualsense_buttoneast,
        "Square" to R.drawable.dualsense_buttonwest,
        "Triangle" to R.drawable.dualsense_buttonnorth,
        "DPadUp" to R.drawable.dualsense_dpad_up,
        "DPadDown" to R.drawable.dualsense_dpad_down,
        "DPadLeft" to R.drawable.dualsense_dpad_left,
        "DPadRight" to R.drawable.dualsense_dpad_right,
        "L1" to R.drawable.dualsense_l1,
        "R1" to R.drawable.dualsense_r1,
        "L3" to R.drawable.dualsense_l3,
        "R3" to R.drawable.dualsense_r3,
        "Start" to R.drawable.dualsense_start,
        "Select" to R.drawable.dualsense_select
    )

    val pressedButtonsState = remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(Unit) {
        while (true) {
            pressedButtonsState.value = InteractionRouter.getPressedButtons()
            kotlinx.coroutines.delay(10L)
        }
    }

    // Periodically update pressed button state
    LaunchedEffect(Unit) {
        while (true) {
            pressedButtonsState.value = InteractionRouter.getPressedButtons()
            kotlinx.coroutines.delay(10L)
        }
    }

    val pressedButtons = pressedButtonsState.value

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFAAAAAA))
            .offset(y = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(R.drawable.dualsense_base),
                contentDescription = "Controller Base",
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(1.5f)
            )

            buttonImages.forEach { (label, drawable) ->
                if (pressedButtons.contains(label)) {
                    Image(
                        painter = painterResource(drawable),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(1.5f)
                    )
                }
            }

        }
    }
}