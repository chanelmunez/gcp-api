module Config
  APP_CONFIG = {
    service_name: 'advanced-user-service-ruby',
    version: '1.0.0',
    environment: ENV['ENV'] || 'development',
    cors: {
      enabled: true,
      origins: ['*']
    }
  }.freeze
end
