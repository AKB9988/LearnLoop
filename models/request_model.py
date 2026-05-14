from pydantic import BaseModel, Field
from typing import Optional, Literal


class LearningRequestModel(BaseModel):
    title: str = Field(..., example="Need help with recursion in C++")
    description: str = Field(..., example="Cannot understand recursive tree calls.")
    subject: str = Field(..., example="Programming")
    topic: str = Field(..., example="Recursion")
    session_type: Literal["video", "whiteboard", "chat"] = "video"
    urgency: Literal["low", "medium", "high", "urgent"] = "medium"
    duration: int = Field(..., example=30, description="Session duration in minutes")
    credits_offered: int = Field(..., example=50, description="KC offered for this session")


class MatchAcceptModel(BaseModel):
    request_id: str = Field(..., example="req_abc123")
