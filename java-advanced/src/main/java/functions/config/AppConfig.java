package functions.config;

public class AppConfig {
    public static final String SERVICE_NAME = "advanced-user-service-java";
    public static final String VERSION = "1.0.0";
    public static final String ENVIRONMENT = System.getenv().getOrDefault("ENV", "development");

    public static class CORS {
        public static final boolean ENABLED = true;
        public static final String ORIGINS = "*";
    }
}
