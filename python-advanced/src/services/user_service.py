from typing import Optional, List
from src.models import User
from src.utils import logger, Validator

class UserService:
    def __init__(self):
        self.users = {}
        self.next_id = 1

    def create_user(self, name: str, email: str) -> User:
        if not Validator.is_valid_name(name):
            logger.error("Invalid name provided", name=name)
            raise ValueError("Invalid name: must be between 2 and 100 characters")

        if not Validator.is_valid_email(email):
            logger.error("Invalid email provided", email=email)
            raise ValueError("Invalid email format")

        user = User(id=self.next_id, name=name, email=email)
        self.users[self.next_id] = user
        self.next_id += 1

        logger.info("User created successfully", user_id=user.id)
        return user

    def get_user(self, user_id: int) -> Optional[User]:
        return self.users.get(user_id)

    def get_all_users(self) -> List[User]:
        return list(self.users.values())
