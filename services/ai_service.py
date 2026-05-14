import os
import logging
import json
import google.generativeai as genai
from typing import List, Dict, Any

# Setup professional logging
logger = logging.getLogger("AI_Service")

# Initialize Gemini (will fallback if GEMINI_API_KEY is missing)
GEMINI_KEY = os.getenv("GEMINI_API_KEY")
if GEMINI_KEY:
    genai.configure(api_key=GEMINI_KEY)
    model = genai.GenerativeModel('gemini-1.5-flash')
else:
    logger.warning("[AI] GEMINI_API_KEY not found. Using deterministic fallback algorithm.")
    model = None

def get_ai_scores(user_profile: Dict[str, Any], active_requests: List[Dict[str, Any]]) -> Dict[str, float]:
    """
    AI Synergy Engine — Uses Gemini LLM for semantic matching with a local fallback.
    """
    if not model:
        return get_fallback_scores(user_profile, active_requests)

    try:
        # Batch requests to save AI tokens and time
        requests_summary = [
            {
                "id": r.get("id"),
                "title": r.get("title"),
                "subject": r.get("subject"),
                "description": r.get("description")
            } for r in active_requests
        ]
        
        prompt = f"""
        As an expert education consultant, rank these learning requests for this tutor profile:
        Tutor Profile: {json.dumps(user_profile)}
        
        Learning Requests: {json.dumps(requests_summary)}
        
        Analyze the semantic relevance between the tutor's subjects/skills and each request's title/description.
        Return ONLY a JSON object where keys are request IDs and values are synergy scores (0-100).
        Example: {{"req_1": 85.5, "req_2": 42.0}}
        """
        
        response = model.generate_content(prompt)
        # Clean up response (sometimes LLMs wrap JSON in backticks)
        json_str = response.text.strip().replace("```json", "").replace("```", "")
        ai_scores = json.loads(json_str)
        
        logger.info(f"[AI] Gemini successfully scored {len(ai_scores)} requests.")
        return {k: float(v) for k, v in ai_scores.items()}

    except Exception as e:
        logger.error(f"[AI] Gemini failed: {e}. Falling back to local algorithm.")
        return get_fallback_scores(user_profile, active_requests)

def get_fallback_scores(user_profile: Dict[str, Any], active_requests: List[Dict[str, Any]]) -> Dict[str, float]:
    """
    Deterministic Weighted Synergy Engine (Used as primary fallback)
    """
    expert_subjects = [s.lower() for s in user_profile.get("subjects_known", []) if isinstance(s, str)]
    expert_level = user_profile.get("skill_level", "beginner").lower()
    skill_multiplier = {"expert": 1.2, "intermediate": 1.1, "beginner": 1.0}.get(expert_level, 1.0)
    
    scores = {}
    for req in active_requests:
        score = 0.0
        req_id = req.get("id")
        req_subject = str(req.get("subject", "")).lower()
        
        if req_subject in expert_subjects:
            score += 70.0
        elif any(req_subject in s or s in req_subject for s in expert_subjects):
            score += 35.0
            
        final_score = min(100.0, score * skill_multiplier)
        scores[req_id] = round(final_score, 2)
        
    return scores

def attach_scores_to_requests(requests: List[Dict[str, Any]], scores: Dict[str, float]) -> List[Dict[str, Any]]:
    """
    Merges AI match scores into each request dict and ranks them.
    """
    for req in requests:
        req["ai_match_score"] = float(scores.get(req.get("id", ""), 0.0))

    requests.sort(key=lambda r: r.get("ai_match_score", 0.0), reverse=True)
    return requests
