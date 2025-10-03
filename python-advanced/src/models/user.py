from datetime import datetime
from dataclasses import dataclass, asdict

@dataclass
class User:
    id: int
    name: str
    email: str
    created_at: datetime = None

    def __post_init__(self):
        if self.created_at is None:
            self.created_at = datetime.now()

    def to_dict(self):
        data = asdict(self)
        data['created_at'] = self.created_at.isoformat()
        return data
