import json
from datetime import datetime
from enum import Enum

class LogLevel(Enum):
    INFO = "INFO"
    ERROR = "ERROR"
    WARN = "WARN"

class Logger:
    def _log(self, level: LogLevel, message: str, **meta):
        log_entry = {
            "timestamp": datetime.now().isoformat(),
            "level": level.value,
            "message": message,
            **meta
        }
        print(json.dumps(log_entry))

    def info(self, message: str, **meta):
        self._log(LogLevel.INFO, message, **meta)

    def error(self, message: str, **meta):
        self._log(LogLevel.ERROR, message, **meta)

    def warn(self, message: str, **meta):
        self._log(LogLevel.WARN, message, **meta)

logger = Logger()
