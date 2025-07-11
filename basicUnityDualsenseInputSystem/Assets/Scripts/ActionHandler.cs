using UnityEngine;

public static class ActionHandler
{
    public static void Trigger(string actionName, float value)
    {
        #if UNITY_EDITOR
        Debug.Log($"Action triggered: {actionName} with value {value}");
        #endif
    }
}