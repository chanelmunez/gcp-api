using System;

namespace AdvancedUserService.Config
{
    public static class AppConfig
    {
        public const string ServiceName = "advanced-user-service-dotnet";
        public const string Version = "1.0.0";

        public static string Environment =>
            System.Environment.GetEnvironmentVariable("ENV") ?? "development";

        public static class Cors
        {
            public const bool Enabled = true;
            public const string Origins = "*";
        }
    }
}
