from fastapi import APIRouter, Depends
from auth import verify_token
from models.user_model import UserProfileModel, SessionCompleteModel, LeaderboardEntry
from services.firestore_service import get_user, create_user, get_session, update_session
from services.wallet_service import get_wallet_data, award_credits

router = APIRouter(prefix="/user", tags=["User"])


# ─────────────────────────────────────────
# GET /user/wallet
# ─────────────────────────────────────────
@router.get("/wallet")
def get_wallet(uid: str = Depends(verify_token)):
    """
    Returns KC wallet summary for the authenticated user.
    """
    wallet = get_wallet_data(uid)
    return {"success": True, "data": wallet}


# ─────────────────────────────────────────
# GET /user/profile
# ─────────────────────────────────────────
@router.get("/profile")
def get_profile(uid: str = Depends(verify_token)):
    """
    Returns the authenticated user's full profile.
    """
    user = get_user(uid)
    if not user:
        return {"success": False, "message": "User profile not found."}
    return {"success": True, "data": user}


# ─────────────────────────────────────────
# POST /user/register
# ─────────────────────────────────────────
@router.post("/register")
def register_user(body: UserProfileModel, uid: str = Depends(verify_token)):
    """
    Creates a user profile in Firestore after Firebase signup.
    Android calls this once after registration.
    """
    existing = get_user(uid)
    if existing:
        return {"success": False, "message": "User already registered."}

    user_data = body.model_dump()
    create_user(uid, user_data)

    return {"success": True, "message": "User registered successfully."}


# ─────────────────────────────────────────
# POST /user/session/complete
# ─────────────────────────────────────────
@router.post("/session/complete")
def complete_session(body: SessionCompleteModel, uid: str = Depends(verify_token)):
    """
    Mark session as complete and transfer KC from escrow to tutor.
    Called by either participant when session ends.
    """
    session = get_session(body.session_id)
    if not session:
        return {"success": False, "message": "Session not found."}

    if session["status"] == "completed":
        return {"success": False, "message": "Session already completed."}

    tutor_id = session["tutor_id"]
    credits  = session["credits_offered"]

    # Transfer KC to tutor
    award_credits(tutor_id, credits)

    # Mark session complete
    update_session(body.session_id, {"status": "completed"})

    return {"success": True, "message": f"{credits} KC transferred to tutor successfully."}


# ─────────────────────────────────────────
# GET /user/leaderboard/top
# ─────────────────────────────────────────
@router.get("/leaderboard/top", response_model=list[LeaderboardEntry])
def get_leaderboard(uid: str = Depends(verify_token)):
    """
    Returns the top 10 users on the platform sorted by Knowledge Credits.
    """
    from services.firestore_service import get_top_users
    top_users = get_top_users(limit=10)

    leaderboard = []
    for user in top_users:
        leaderboard.append(LeaderboardEntry(
            user_id=user["id"],
            display_name=user.get("name", "Anonymous"),
            avatar_url="",
            knowledge_credits=user.get("balance", 0),
            skill_level="Expert" if user.get("balance", 0) > 1000 else "Learner"
        ))

    return leaderboard
