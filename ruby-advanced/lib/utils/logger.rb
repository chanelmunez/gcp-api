require 'json'
require 'time'

module Utils
  class Logger
    LEVELS = {
      info: 'INFO',
      error: 'ERROR',
      warn: 'WARN'
    }.freeze

    def log(level, message, meta = {})
      entry = {
        timestamp: Time.now.iso8601,
        level: LEVELS[level],
        message: message
      }.merge(meta)

      puts entry.to_json
    end

    def info(message, meta = {})
      log(:info, message, meta)
    end

    def error(message, meta = {})
      log(:error, message, meta)
    end

    def warn(message, meta = {})
      log(:warn, message, meta)
    end
  end
end
