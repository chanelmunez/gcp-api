import os

config = {
    "service_name": "advanced-user-service-python",
    "version": "1.0.0",
    "environment": os.getenv("ENV", "development"),
    "cors": {
        "enabled": True,
        "origins": ["*"]
    }
}
