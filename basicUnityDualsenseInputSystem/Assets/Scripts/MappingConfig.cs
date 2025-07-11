// MappingConfig.cs
using System.Collections.Generic;
using UnityEngine;

[CreateAssetMenu(menuName = "Input/Mapping Config")]
public class MappingConfig : ScriptableObject {
    public Dictionary<string, string> inputToActionMap = new Dictionary<string, string>();

    public void LoadFromUserConfig(UserMappingConfig userConfig) {
        inputToActionMap.Clear();
        foreach (var entry in userConfig.mappings) {
            inputToActionMap[entry.inputName] = entry.actionName;
        }
    }

    public string GetActionForInput(string inputName) {
        inputToActionMap.TryGetValue(inputName, out var action);
        return action;
    }
}