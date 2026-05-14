from firebase_config import db
from datetime import datetime


# ─────────────────────────────────────────
# USER OPERATIONS
# ─────────────────────────────────────────

def get_user(uid: str) -> dict:
    """Fetch user document from Firestore by UID."""
    doc = db.collection("users").document(uid).get()
    if not doc.exists:
        return None
    return {"id": doc.id, **doc.to_dict()}


def create_user(uid: str, data: dict) -> dict:
    """Create a new user profile in Firestore."""
    data["created_at"] = datetime.utcnow().isoformat()
    db.collection("users").document(uid).set(data)
    return data


def update_user(uid: str, updates: dict):
    """Partially update user document."""
    db.collection("users").document(uid).update(updates)


def get_top_users(limit: int = 10) -> list:
    """Fetch top users sorted by balance (Knowledge Credits)."""
    from firebase_admin import firestore
    docs = (
        db.collection("users")
        .order_by("balance", direction=firestore.Query.DESCENDING)
        .limit(limit)
        .stream()
    )
    results = []
    for doc in docs:
        results.append({"id": doc.id, **doc.to_dict()})
    return results


# ─────────────────────────────────────────
# REQUEST OPERATIONS
# ─────────────────────────────────────────

def create_request(uid: str, data: dict) -> str:
    """Store a new learning request and return its document ID."""
    data["requester_id"] = uid
    data["status"] = "active"
    data["created_at"] = datetime.utcnow().isoformat()

    ref = db.collection("requests").add(data)
    return ref[1].id       # Firestore returns (timestamp, doc_ref)


def get_active_requests() -> list:
    """Fetch all requests with status == active."""
    from firebase_admin import firestore
    docs = (
        db.collection("requests")
        .where(filter=firestore.FieldFilter("status", "==", "active"))
        .stream()
    )
    results = []
    for doc in docs:
        results.append({"id": doc.id, **doc.to_dict()})
    return results



def get_request(request_id: str) -> dict:
    """Fetch a single request document."""
    doc = db.collection("requests").document(request_id).get()
    if not doc.exists:
        return None
    return {"id": doc.id, **doc.to_dict()}


def update_request(request_id: str, updates: dict):
    """Update a request document."""
    db.collection("requests").document(request_id).update(updates)


# ─────────────────────────────────────────
# SESSION OPERATIONS
# ─────────────────────────────────────────

def create_session(data: dict) -> str:
    """Store a matched session document."""
    data["created_at"] = datetime.utcnow().isoformat()
    data["status"] = "ongoing"
    ref = db.collection("sessions").add(data)
    return ref[1].id


def get_session(session_id: str) -> dict:
    """Fetch a session document."""
    doc = db.collection("sessions").document(session_id).get()
    if not doc.exists:
        return None
    return {"id": doc.id, **doc.to_dict()}


def update_session(session_id: str, updates: dict):
    """Update a session document."""
    db.collection("sessions").document(session_id).update(updates)
