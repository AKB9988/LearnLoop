"""
AI Service — Integration layer for Member 3's matching algorithm.

Member 3 provides a function that takes:
  - user_profile (dict)
  - active_requests (list of dicts)

And returns a dict of:
  { "request_id": score (int 0–100), ... }

HOW TO INTEGRATE MEMBER 3:
1. Member 3 shares their scoring function (or module).
2. Import it here and replace the mock below.
3. The rest of the backend stays unchanged.
"""


# ─────────────────────────────────────────
# MOCK AI SCORING (replace with Member 3's real function)
# ─────────────────────────────────────────

def get_ai_scores(user_profile: dict, active_requests: list) -> dict:
    """
    Mock AI scoring — returns a fake relevance score for each request.
    Replace this function body with Member 3's real implementation.

    Args:
        user_profile  : dict  — The tutor's profile (subjects known, skills, etc.)
        active_requests: list — All active learning requests from Firestore

    Returns:
        dict  — { request_id: score } e.g. {"req_123": 88, "req_456": 72}
    """

    # ── Uncomment below when Member 3's module is ready ──────────────
    # from ai_module import compute_match_scores   # Member 3's file
    # return compute_match_scores(user_profile, active_requests)
    # ─────────────────────────────────────────────────────────────────

    # MOCK: generate a simple score based on subject overlap
    subjects = user_profile.get("subjects_known")
    if not subjects or not isinstance(subjects, list):
        subjects = []
        
    user_subjects = set(s.lower() for s in subjects)
    scores = {}

    for req in active_requests:
        req_subject = str(req.get("subject", "")).lower()
        req_topic   = str(req.get("topic",   "")).lower()

        if req_subject in user_subjects or req_topic in user_subjects:
            score = 85  # high relevance
        else:
            score = 40  # low relevance

        scores[req["id"]] = score

    return scores


def attach_scores_to_requests(requests: list, scores: dict) -> list:
    """
    Merges AI match scores into each request dict.
    """
    for req in requests:
        req["ai_match_score"] = scores.get(req["id"], 0)

    # Sort by score descending so best matches appear first
    requests.sort(key=lambda r: r["ai_match_score"], reverse=True)
    return requests
