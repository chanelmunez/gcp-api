const logLevels = {
  INFO: 'INFO',
  ERROR: 'ERROR',
  WARN: 'WARN'
};

function log(level, message, meta = {}) {
  const timestamp = new Date().toISOString();
  console.log(JSON.stringify({
    timestamp,
    level,
    message,
    ...meta
  }));
}

module.exports = {
  info: (message, meta) => log(logLevels.INFO, message, meta),
  error: (message, meta) => log(logLevels.ERROR, message, meta),
  warn: (message, meta) => log(logLevels.WARN, message, meta)
};
