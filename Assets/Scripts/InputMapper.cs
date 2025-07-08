using UnityEngine;
using UnityEngine.InputSystem;
using System.IO;



public class InputMapper : MonoBehaviour
{
    public MappingConfig mappingConfig;
    public InputDebugLogger debugLogger; 

    void Start()
    {
        string path = Path.Combine(Application.persistentDataPath, "default.json");

        var userConfig = MappingConfigLoader.Load("default");
        if (userConfig != null)
        {
            mappingConfig.LoadFromUserConfig(userConfig);
            
            #if UNITY_EDITOR
            Debug.Log("Loaded user mapping from preset.");
            #endif
        }
        else
        {
            #if UNITY_EDITOR
            Debug.LogWarning("No user config found. Loading fallback defaults.");
            #endif
            
            var fallback = InputMappingCatalog.CreateDefaultMapping();
            var axisInputs = InputMappingCatalog.GetInputsByType(InputType.Axis);
            mappingConfig.LoadFromUserConfig(fallback);
            MappingConfigLoader.Save("default", fallback);

            #if UNITY_EDITOR
            Debug.Log("Saved default preset to: " + path);
            #endif
        }

        #if UNITY_EDITOR
        Debug.Log("Preset path: " + path);
        #endif
    }

    void Update()
    {
        Gamepad gamepad = Gamepad.current;
        if (gamepad == null) return;

        // Sticks (Axis)
        CheckAndTrigger("leftStickX", gamepad.leftStick.x.ReadValue());
        CheckAndTrigger("leftStickY", gamepad.leftStick.y.ReadValue());
        CheckAndTrigger("rightStickX", gamepad.rightStick.x.ReadValue());
        CheckAndTrigger("rightStickY", gamepad.rightStick.y.ReadValue());

        // Triggers (Axis)
        CheckAndTrigger("leftTrigger", gamepad.leftTrigger.ReadValue());
        CheckAndTrigger("rightTrigger", gamepad.rightTrigger.ReadValue());

        // Face Buttons
        CheckAndTrigger("buttonSouth", gamepad.buttonSouth.isPressed ? 1f : 0f);
        CheckAndTrigger("buttonNorth", gamepad.buttonNorth.isPressed ? 1f : 0f);
        CheckAndTrigger("buttonEast", gamepad.buttonEast.isPressed ? 1f : 0f);
        CheckAndTrigger("buttonWest", gamepad.buttonWest.isPressed ? 1f : 0f);

        // Shoulder Buttons
        CheckAndTrigger("leftShoulder", gamepad.leftShoulder.isPressed ? 1f : 0f);
        CheckAndTrigger("rightShoulder", gamepad.rightShoulder.isPressed ? 1f : 0f);

        // Stick Press (L3/R3)
        CheckAndTrigger("leftStickPress", gamepad.leftStickButton.isPressed ? 1f : 0f);
        CheckAndTrigger("rightStickPress", gamepad.rightStickButton.isPressed ? 1f : 0f);

        // D-pad (each direction as button)
        CheckAndTrigger("dpadUp", gamepad.dpad.up.isPressed ? 1f : 0f);
        CheckAndTrigger("dpadDown", gamepad.dpad.down.isPressed ? 1f : 0f);
        CheckAndTrigger("dpadLeft", gamepad.dpad.left.isPressed ? 1f : 0f);
        CheckAndTrigger("dpadRight", gamepad.dpad.right.isPressed ? 1f : 0f);

        // Start / Select
        CheckAndTrigger("startButton", gamepad.startButton.isPressed ? 1f : 0f);
        CheckAndTrigger("selectButton", gamepad.selectButton.isPressed ? 1f : 0f);
        
    }

    void CheckAndTrigger(string inputName, float value)
    {
        string action = mappingConfig.GetActionForInput(inputName);
        if (string.IsNullOrEmpty(action))
            action = inputName; 

        ActionHandler.Trigger(action, value);

        if (debugLogger != null)
            debugLogger.UpdateValue(inputName, value);
    }
}