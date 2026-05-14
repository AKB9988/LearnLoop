from pydantic import BaseModel, Field
from typing import List, Optional


class UserProfileModel(BaseModel):
    name: str
    email: str
    subjects_known: List[str] = []
    subjects_learning: List[str] = []
    balance: int = 500          # Starting KC balance
    total_earned: int = 0
    total_spent: int = 0
    sessions_completed: int = 0
    streak_days: int = 0


class SessionCompleteModel(BaseModel):
    session_id: str = Field(..., example="session_xyz")


class LeaderboardEntry(BaseModel):
    user_id: str
    display_name: str
    avatar_url: str = ""
    knowledge_credits: int
    skill_level: str = "Learner"
