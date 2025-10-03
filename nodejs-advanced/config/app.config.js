module.exports = {
  serviceName: 'advanced-user-service',
  version: '1.0.0',
  environment: process.env.NODE_ENV || 'development',
  cors: {
    enabled: true,
    origins: ['*']
  }
};
