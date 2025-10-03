require 'json'
require 'time'

module Models
  class User
    attr_reader :id, :name, :email, :created_at

    def initialize(id, name, email)
      @id = id
      @name = name
      @email = email
      @created_at = Time.now
    end

    def to_h
      {
        id: @id,
        name: @name,
        email: @email,
        created_at: @created_at.iso8601
      }
    end

    def to_json(*args)
      to_h.to_json(*args)
    end
  end
end
