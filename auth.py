from fastapi import HTTPException, Header
from firebase_admin import auth


def verify_token(authorization: str = Header(...)) -> str:
    """
    Extracts and verifies Firebase ID token from Authorization header.
    Android sends:  Authorization: Bearer <firebase_token>
    Returns the authenticated user's UID.
    """
    if not authorization.startswith("Bearer "):
        raise HTTPException(status_code=401, detail="Invalid authorization format. Use: Bearer <token>")

    token = authorization.split("Bearer ")[1]

    try:
        decoded_token = auth.verify_id_token(token)
        uid = decoded_token["uid"]
        return uid

    except auth.ExpiredIdTokenError:
        raise HTTPException(status_code=401, detail="Token has expired. Please login again.")
    except auth.InvalidIdTokenError:
        raise HTTPException(status_code=401, detail="Invalid token. Authentication failed.")
    except Exception as e:
        raise HTTPException(status_code=401, detail=f"Authentication error: {str(e)}")
