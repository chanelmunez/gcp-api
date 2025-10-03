package functions.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public enum Level {
        INFO, ERROR, WARN
    }

    private void log(Level level, String message, String meta) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        String logEntry = String.format(
            "{\"timestamp\":\"%s\",\"level\":\"%s\",\"message\":\"%s\"%s}",
            timestamp, level, message, meta.isEmpty() ? "" : ",\"meta\":" + meta
        );
        System.out.println(logEntry);
    }

    public void info(String message) {
        log(Level.INFO, message, "");
    }

    public void info(String message, String meta) {
        log(Level.INFO, message, meta);
    }

    public void error(String message) {
        log(Level.ERROR, message, "");
    }

    public void error(String message, String meta) {
        log(Level.ERROR, message, meta);
    }

    public void warn(String message) {
        log(Level.WARN, message, "");
    }

    public void warn(String message, String meta) {
        log(Level.WARN, message, meta);
    }
}
