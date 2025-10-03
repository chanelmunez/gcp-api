using System;
using System.Collections.Generic;
using System.Text.Json;

namespace AdvancedUserService.Utils
{
    public enum LogLevel
    {
        INFO,
        ERROR,
        WARN
    }

    public class Logger
    {
        public void Log(LogLevel level, string message, Dictionary<string, object>? meta = null)
        {
            var entry = new Dictionary<string, object>
            {
                ["timestamp"] = DateTime.UtcNow.ToString("o"),
                ["level"] = level.ToString(),
                ["message"] = message
            };

            if (meta != null)
            {
                foreach (var kvp in meta)
                {
                    entry[kvp.Key] = kvp.Value;
                }
            }

            Console.WriteLine(JsonSerializer.Serialize(entry));
        }

        public void Info(string message, Dictionary<string, object>? meta = null)
        {
            Log(LogLevel.INFO, message, meta);
        }

        public void Error(string message, Dictionary<string, object>? meta = null)
        {
            Log(LogLevel.ERROR, message, meta);
        }

        public void Warn(string message, Dictionary<string, object>? meta = null)
        {
            Log(LogLevel.WARN, message, meta);
        }
    }
}
