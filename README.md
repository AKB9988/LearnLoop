# LearnLoop Backend — Member 2

> FastAPI backend connecting Android app ↔ Firebase ↔ AI Matching

---

## ⚡ Quick Start

### 1. Clone & Install
```bash
pip install -r requirements.txt
```

### 2. Add Firebase Key
- Go to Firebase Console → Project Settings → Service Accounts
- Click **"Generate New Private Key"**
- Download the JSON file
- Rename it to `firebase_key.json`
- Place it in the root of this project (same folder as `main.py`)

### 3. Run the Server
```bash
uvicorn main:app --reload
```

Server runs at: `http://localhost:8000`

Interactive API docs: `http://localhost:8000/docs`

---

## 📌 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Health check |
| POST | `/user/register` | Register new user profile |
| GET | `/user/profile` | Get user profile |
| GET | `/user/wallet` | Get KC wallet summary |
| POST | `/request/create` | Post a learning request |
| GET | `/requests/active` | Get active requests with AI scores |
| POST | `/request/cancel` | Cancel a request + refund KC |
| POST | `/match/accept` | Tutor accepts request → Jitsi link |
| GET | `/match/session/{id}` | Get session details |
| POST | `/user/session/complete` | Complete session → transfer KC |

---

## 🔑 Authentication

All endpoints (except `/` and `/health`) require:

```
Authorization: Bearer <Firebase_ID_Token>
```

Android sends this via Retrofit header.

---

## 💰 Knowledge Credit Flow

```
User posts request → KC deducted from balance
         ↓
Tutor accepts → Session created (KC in escrow)
         ↓
Session completed → KC transferred to tutor
         ↓
If cancelled → KC refunded to requester
```

---

## 🤖 AI Integration (Member 3)

Edit `services/ai_service.py`:

```python
# Uncomment and replace mock with Member 3's function:
from ai_module import compute_match_scores
return compute_match_scores(user_profile, active_requests)
```

Member 3's function must accept `(user_profile: dict, active_requests: list)`  
and return `{ "request_id": score }`.

---

## 🚀 Deployment (Render/Railway)

1. Push to GitHub
2. Set environment variables (instead of `firebase_key.json`):
   - `FIREBASE_PROJECT_ID`
   - `FIREBASE_PRIVATE_KEY`
   - `FIREBASE_CLIENT_EMAIL`
   - *(uncomment env-var block in `firebase_config.py`)*
3. Start command: `uvicorn main:app --host 0.0.0.0 --port 8000`

---

## 🗃️ Firestore Collections

| Collection | Purpose |
|------------|---------|
| `users` | Profiles + KC balances |
| `requests` | Learning requests |
| `sessions` | Matched sessions + Jitsi links |
