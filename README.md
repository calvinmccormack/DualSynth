# DualSynth

An accessible Android music-making app using the PS5 DualSense controller.

## Features

- Real-time synthesis and effects using [libpd](https://github.com/libpd/libpd)
- Customizable input-action mappings for buttons and analog controls
- Preset save/load system for mapping configurations
- Designed for low-cost Android phones and accessibility
- Bluetooth controller support for all interaction (no on-screen controls)

## Structure

- `MainActivity.kt`: Core app activity and input event handling
- `InteractionRouter.kt`: Routes controller input to Pd and OSC
- `MappingConfig.kt`: Stores editable controller-to-action mappings
- `LiveMonitorUI.kt`: Visual display of button press states
- `PresetManager.kt`: Save/load mapping presets from internal storage as JSON files
- `assets/pd-patches/`: Contains Pure Data patch and sound files

## Getting Started

1. Clone this repo into Android Studio (Electric Eel or later)
2. Connect a PS5 DualSense controller via Bluetooth
3. Build and run the app on a physical Android device
4. You can drop in and connect any vanilla pd patches into the pd routing templates to create custom configurations

## License

MIT