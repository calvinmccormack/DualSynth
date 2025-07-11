// InputMappingCatalog.cs
using System.Collections.Generic;

public enum InputType {
    Button,
    Axis
}

public class InputDescriptor {
    public string inputName;
    public InputType inputType;

    public InputDescriptor(string name, InputType type) {
        inputName = name;
        inputType = type;
    }
}

public static class InputMappingCatalog {
    public static List<InputDescriptor> AllInputs = new List<InputDescriptor> {
        new InputDescriptor("leftStickX", InputType.Axis),
        new InputDescriptor("leftStickY", InputType.Axis),
        new InputDescriptor("rightStickX", InputType.Axis),
        new InputDescriptor("rightStickY", InputType.Axis),
        new InputDescriptor("leftTrigger", InputType.Axis),
        new InputDescriptor("rightTrigger", InputType.Axis),
        new InputDescriptor("dpadUp", InputType.Button),
        new InputDescriptor("dpadDown", InputType.Button),
        new InputDescriptor("dpadLeft", InputType.Button),
        new InputDescriptor("dpadRight", InputType.Button),
        new InputDescriptor("buttonSouth", InputType.Button),
        new InputDescriptor("buttonNorth", InputType.Button),
        new InputDescriptor("buttonEast", InputType.Button),
        new InputDescriptor("buttonWest", InputType.Button),
        new InputDescriptor("leftShoulder", InputType.Button),
        new InputDescriptor("rightShoulder", InputType.Button),
        new InputDescriptor("leftStickPress", InputType.Button),
        new InputDescriptor("rightStickPress", InputType.Button),
        new InputDescriptor("startButton", InputType.Button),
        new InputDescriptor("selectButton", InputType.Button),
    };

    public static UserMappingConfig CreateDefaultMapping() {
        var config = new UserMappingConfig();
        foreach (var input in AllInputs) {
            config.mappings.Add(new UserMappingEntry {
                inputName = input.inputName,
                actionName = input.inputName // placeholder — action name matches input name
            });
        }
        return config;
    }

    public static List<string> GetInputsByType(InputType type) {
        return AllInputs
            .FindAll(desc => desc.inputType == type)
            .ConvertAll(desc => desc.inputName);
    }
}