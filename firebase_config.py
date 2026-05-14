import firebase_admin
from firebase_admin import credentials, firestore
import os
import json

# Initialize Firebase only once
if not firebase_admin._apps:
    # Check if we are running locally (file exists) or on Render (ENV exists)
    if os.path.exists("firebase_key.json"):
        # Option 1: Use the local JSON key file
        cred = credentials.Certificate("firebase_key.json")
    else:
        # Option 2: Use environment variables (for Render deployment)
        # On Render, you will create an environment variable called FIREBASE_CONFIG
        # and paste the entire content of your JSON key file into it.
        firebase_json = os.getenv("FIREBASE_CONFIG")
        if firebase_json:
            try:
                cred_dict = json.loads(firebase_json)
                cred = credentials.Certificate(cred_dict)
            except Exception as e:
                 raise Exception(f"Failed to parse FIREBASE_CONFIG env var: {e}")
        else:
            raise Exception("No Firebase credentials found (missing firebase_key.json and FIREBASE_CONFIG env var)!")

    firebase_admin.initialize_app(cred)

# Firestore client — import this wherever needed
db = firestore.client()
