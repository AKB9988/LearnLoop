from fastapi import HTTPException
from services.firestore_service import get_user, update_user


# ─────────────────────────────────────────
# KNOWLEDGE CREDIT (KC) WALLET LOGIC
# ─────────────────────────────────────────

def deduct_credits(uid: str, amount: int):
    """
    Deduct KC from requester when they post a learning request.
    Credits are 'held' — moved to in-escrow state.
    """
    user = get_user(uid)
    if not user:
        raise HTTPException(status_code=404, detail="User not found.")

    if user["balance"] < amount:
        raise HTTPException(
            status_code=400,
            detail=f"Insufficient KC balance. You have {user['balance']} KC but need {amount} KC."
        )

    new_balance = user["balance"] - amount
    new_spent = user.get("total_spent", 0) + amount

    update_user(uid, {
        "balance": new_balance,
        "total_spent": new_spent
    })


def award_credits(uid: str, amount: int):
    """
    Award KC to tutor after session is completed.
    """
    user = get_user(uid)
    if not user:
        raise HTTPException(status_code=404, detail="Tutor user not found.")

    new_balance = user["balance"] + amount
    new_earned = user.get("total_earned", 0) + amount
    new_sessions = user.get("sessions_completed", 0) + 1

    update_user(uid, {
        "balance": new_balance,
        "total_earned": new_earned,
        "sessions_completed": new_sessions
    })


def refund_credits(uid: str, amount: int):
    """
    Refund KC to requester if request is cancelled.
    """
    user = get_user(uid)
    if not user:
        raise HTTPException(status_code=404, detail="User not found.")

    new_balance = user["balance"] + amount
    new_spent = max(0, user.get("total_spent", 0) - amount)

    update_user(uid, {
        "balance": new_balance,
        "total_spent": new_spent
    })


def get_wallet_data(uid: str) -> dict:
    """
    Return full wallet summary for the user.
    """
    user = get_user(uid)
    if not user:
        raise HTTPException(status_code=404, detail="User not found.")

    return {
        "balance": user.get("balance", 0),
        "total_earned": user.get("total_earned", 0),
        "total_spent": user.get("total_spent", 0),
        "sessions_completed": user.get("sessions_completed", 0),
        "streak_days": user.get("streak_days", 0)
    }
