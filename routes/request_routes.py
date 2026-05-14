from fastapi import APIRouter, Depends
from auth import verify_token
from models.request_model import LearningRequestModel
from services.firestore_service import create_request, get_active_requests, get_user, update_request
from services.wallet_service import deduct_credits
from services.ai_service import get_ai_scores, attach_scores_to_requests

router = APIRouter(prefix="/request", tags=["Requests"])


# ─────────────────────────────────────────
# POST /request/create
# ─────────────────────────────────────────
@router.post("/create")
def create_learning_request(
    body: LearningRequestModel,
    uid: str = Depends(verify_token)
):
    """
    Create a new learning request.
    - Verifies Firebase token
    - Deducts KC from requester's wallet
    - Stores request in Firestore
    """
    # Deduct credits (raises 400 if insufficient)
    deduct_credits(uid, body.credits_offered)

    # Build Firestore document
    request_data = body.model_dump()
    request_data["requester_id"] = uid

    # Fetch requester name for display in feed
    user = get_user(uid)
    request_data["poster_name"] = user.get("name", "Anonymous") if user else "Anonymous"

    request_id = create_request(uid, request_data)

    return {
        "success": True,
        "message": "Request created successfully",
        "request_id": request_id
    }


# ─────────────────────────────────────────
# GET /requests/active   (note: on the main router)
# ─────────────────────────────────────────
active_router = APIRouter(prefix="/requests", tags=["Requests"])

@active_router.get("/active")
def get_active_feed(uid: str = Depends(verify_token)):
    """
    Returns all active learning requests with AI match scores attached.
    """
    requests = get_active_requests()

    if not requests:
        return {"success": True, "data": []}

    # Get current user profile for AI scoring
    user_profile = get_user(uid) or {}

    # Attach AI match scores from Member 3
    scores = get_ai_scores(user_profile, requests)
    requests = attach_scores_to_requests(requests, scores)

    return {"success": True, "data": requests}


# ─────────────────────────────────────────
# POST /request/cancel
# ─────────────────────────────────────────
@router.post("/cancel")
def cancel_request(request_id: str, uid: str = Depends(verify_token)):
    """
    Cancel an active request and refund KC to requester.
    """
    from services.firestore_service import get_request
    from services.wallet_service import refund_credits

    req = get_request(request_id)
    if not req:
        return {"success": False, "message": "Request not found."}

    if req["requester_id"] != uid:
        return {"success": False, "message": "You can only cancel your own requests."}

    if req["status"] != "active":
        return {"success": False, "message": f"Cannot cancel a request with status: {req['status']}"}

    # Refund KC
    refund_credits(uid, req["credits_offered"])
    update_request(request_id, {"status": "cancelled"})

    return {"success": True, "message": "Request cancelled and KC refunded."}
