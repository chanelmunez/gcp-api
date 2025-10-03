import re

class Validator:
    @staticmethod
    def is_valid_email(email: str) -> bool:
        email_regex = r'^[^\s@]+@[^\s@]+\.[^\s@]+$'
        return bool(re.match(email_regex, email))

    @staticmethod
    def is_valid_name(name: str) -> bool:
        return name and 2 <= len(name) <= 100
