module Utils
  class Validator
    EMAIL_REGEX = /\A[^\s@]+@[^\s@]+\.[^\s@]+\z/

    def self.valid_email?(email)
      !!(email =~ EMAIL_REGEX)
    end

    def self.valid_name?(name)
      name && name.length >= 2 && name.length <= 100
    end
  end
end
