from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

# Firebase must initialize before any route imports that use db
import firebase_config  # noqa: F401 — side effect: initializes firebase_admin

from routes.request_routes import router as request_router, active_router
from routes.user_routes    import router as user_router
from routes.match_routes   import router as match_router

# ─────────────────────────────────────────
# App Initialization
# ─────────────────────────────────────────
app = FastAPI(
    title="LearnLoop Backend API",
    description="Peer-to-Peer Knowledge Exchange — Backend by Member 2",
    version="1.0.0",
)

# Allow Android app to connect (CORS)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],     # Restrict to your domain in production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ─────────────────────────────────────────
# Register Routers
# ─────────────────────────────────────────
app.include_router(request_router)   # /request/create, /request/cancel
app.include_router(active_router)    # /requests/active
app.include_router(user_router)      # /user/wallet, /user/profile, /user/register
app.include_router(match_router)     # /match/accept, /match/session/{id}


# ─────────────────────────────────────────
# Health Check
# ─────────────────────────────────────────
@app.get("/", tags=["Health"])
def root():
    return {
        "status": "LearnLoop backend is running 🚀",
        "version": "1.0.0"
    }

@app.get("/health", tags=["Health"])
def health():
    return {"status": "ok"}


# ─────────────────────────────────────────
# Run with: uvicorn main:app --reload
# ─────────────────────────────────────────
if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
