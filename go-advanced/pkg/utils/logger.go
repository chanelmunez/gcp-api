package utils

import (
	"encoding/json"
	"fmt"
	"time"
)

type LogLevel string

const (
	INFO  LogLevel = "INFO"
	ERROR LogLevel = "ERROR"
	WARN  LogLevel = "WARN"
)

type Logger struct{}

type logEntry struct {
	Timestamp string                 `json:"timestamp"`
	Level     LogLevel               `json:"level"`
	Message   string                 `json:"message"`
	Meta      map[string]interface{} `json:"meta,omitempty"`
}

func (l *Logger) log(level LogLevel, message string, meta map[string]interface{}) {
	entry := logEntry{
		Timestamp: time.Now().Format(time.RFC3339),
		Level:     level,
		Message:   message,
		Meta:      meta,
	}
	jsonData, _ := json.Marshal(entry)
	fmt.Println(string(jsonData))
}

func (l *Logger) Info(message string, meta map[string]interface{}) {
	l.log(INFO, message, meta)
}

func (l *Logger) Error(message string, meta map[string]interface{}) {
	l.log(ERROR, message, meta)
}

func (l *Logger) Warn(message string, meta map[string]interface{}) {
	l.log(WARN, message, meta)
}

var Log = &Logger{}
