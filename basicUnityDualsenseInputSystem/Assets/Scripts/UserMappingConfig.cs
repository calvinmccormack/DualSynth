// UserMappingConfig.cs
using System;
using System.Collections.Generic;

[Serializable]
public class UserMappingEntry {
    public string inputName;     // e.g. "buttonSouth"
    public string actionName;    // e.g. "play, pause"
}

[Serializable]
public class UserMappingConfig {
    public List<UserMappingEntry> mappings = new List<UserMappingEntry>();
}