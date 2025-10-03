package config

import "os"

type Config struct {
	ServiceName string
	Version     string
	Environment string
	CORS        CORSConfig
}

type CORSConfig struct {
	Enabled bool
	Origins []string
}

var AppConfig = Config{
	ServiceName: "advanced-user-service-go",
	Version:     "1.0.0",
	Environment: getEnv("ENV", "development"),
	CORS: CORSConfig{
		Enabled: true,
		Origins: []string{"*"},
	},
}

func getEnv(key, defaultValue string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return defaultValue
}
