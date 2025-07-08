// MappingConfigLoader.cs
using System.IO;
using UnityEngine;

public static class MappingConfigLoader {
    private static string GetPath(string presetName) =>
        Path.Combine(Application.persistentDataPath, $"{presetName}.json");

    public static UserMappingConfig Load(string presetName) {
        string path = GetPath(presetName);
        if (!File.Exists(path)) {
            Debug.LogWarning($"Preset not found: {path}");
            return null;
        }

        string json = File.ReadAllText(path);
        return JsonUtility.FromJson<UserMappingConfig>(json);
    }

    public static void Save(string presetName, UserMappingConfig config) {
        string path = GetPath(presetName);
        string json = JsonUtility.ToJson(config, true);
        File.WriteAllText(path, json);

        #if UNITY_EDITOR
        Debug.Log($"Preset saved to: {path}");
        #endif
    }
}