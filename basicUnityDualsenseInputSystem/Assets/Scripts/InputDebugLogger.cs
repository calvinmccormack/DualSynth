using System.Collections.Generic;
using UnityEngine;
using TMPro; // or use UnityEngine.UI if using legacy Text

public class InputDebugLogger : MonoBehaviour {
    public TextMeshProUGUI debugText; // Assign in Inspector

    private Dictionary<string, float> liveInputs = new Dictionary<string, float>();
    
    void Start() {
    foreach (var input in InputMappingCatalog.AllInputs) {
        liveInputs[input.inputName] = 0f;
        }   
    }

    public void UpdateValue(string inputName, float value)
    {
        liveInputs[inputName] = value;
    }


    private string lastOutput = "";
    void Update() {
        string output = "";

        foreach (var entry in liveInputs) {
            string label = FormatInputName(entry.Key);
            output += $"{label}: {entry.Value:0.##}\n";
        }

        if (output != lastOutput) {
            debugText.text = output;
            lastOutput = output;
        }

        debugText.text = output;
    }

    private string FormatInputName(string raw) {
        // Optional: prettify names like "leftStickY" → "Left Stick Y"
        return System.Text.RegularExpressions.Regex.Replace(raw, "([a-z])([A-Z])", "$1 $2").Replace("button", "Button").Replace("Stick", " Stick");
    }
}