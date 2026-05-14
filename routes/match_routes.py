from fastapi import APIRouter, Depends
from auth import verify_token
from models.request_model import MatchAcceptModel
from services.firestore_service import get_request, update_request, create_session
import uuid

router = APIRouter(prefix="/match", tags=["Match"])


def generate_jitsi_url(session_id: str) -> str:
    """Generate a unique Jitsi Meet URL for the session."""
    room_code = session_id[:8].upper()
    return f"https://meet.jit.si/LearnLoop_Session_{room_code}"


# ─────────────────────────────────────────
# POST /match/accept
# ─────────────────────────────────────────
@router.post("/accept")
def accept_match(body: MatchAcceptModel, uid: str = Depends(verify_token)):
    """
    Tutor accepts a learning request.
    - Marks request as matched
    - Generates Jitsi meeting URL
    - Creates session document in Firestore
    - Returns meeting link
    """
    req = get_request(body.request_id)

    if not req:
        return {"success": False, "message": "Request not found."}

    if req["status"] != "active":
        return {"success": False, "message": f"Request is no longer available. Status: {req['status']}"}

    if req["requester_id"] == uid:
        return {"success": False, "message": "You cannot accept your own request."}

    # Create session document
    session_data = {
        "request_id": body.request_id,
        "tutor_id":   uid,
        "learner_id": req["requester_id"],
        "credits_offered": req["credits_offered"],
        "subject": req.get("subject", ""),
        "topic":   req.get("topic", ""),
        "duration": req.get("duration", 30),
    }

    session_id   = create_session(session_data)
    meeting_link = generate_jitsi_url(session_id)

    # Save meeting link back to session
    from services.firestore_service import update_session
    update_session(session_id, {"meeting_link": meeting_link})

    # Mark request as matched
    update_request(body.request_id, {
        "status":     "matched",
        "tutor_id":   uid,
        "session_id": session_id
    })

    return {
        "success": True,
        "session_id":   session_id,
        "meeting_link": meeting_link
    }


# ─────────────────────────────────────────
# GET /match/session/{session_id}
# ─────────────────────────────────────────
@router.get("/session/{session_id}")
def get_session_details(session_id: str, uid: str = Depends(verify_token)):
    """
    Returns session details including the Jitsi link.
    Both tutor and learner can call this.
    """
    from services.firestore_service import get_session
    session = get_session(session_id)

    if not session:
        return {"success": False, "message": "Session not found."}

    # Only participants can view session details
    if uid not in [session.get("tutor_id"), session.get("learner_id")]:
        return {"success": False, "message": "Access denied. You are not part of this session."}

    return {"success": True, "data": session}
