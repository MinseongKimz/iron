import requests
import json
import sys

BASE_URL = "http://localhost:8080/api"

def run_test():
    print("Step 1: Login")
    login_payload = {"username": "test1", "apiKey": "dummy-key"}
    try:
        res = requests.post(f"{BASE_URL}/users/login", json=login_payload)
        res.raise_for_status()
        user_data = res.json()
        user_id = user_data['userId']
        print(f"Login successful. UserId: {user_id}")
    except Exception as e:
        print(f"Login failed: {e}")
        return

    print("\nStep 2: Add Workout (Chat)")
    chat_payload = {
        "userId": user_id,
        "date": "2026-01-31",
        "rawInput": "벤치프레스 60kg 10회 5세트, 인클라인 덤벨 프레스 20kg 10회 4세트, 딥스 10회 4세트, 케이블 크로스오버 15kg 12회 4세트"
    }
    try:
        res = requests.post(f"{BASE_URL}/ai/chat", json=chat_payload)
        res.raise_for_status()
        chat_resp = res.json()
        print("Chat successful.")
        print("Feedback:", chat_resp.get("aiFeedbackSummary", "No feedback"))
    except Exception as e:
        print(f"Chat failed: {e}")
        return

    print("\nStep 3: Verify History")
    try:
        res = requests.get(f"{BASE_URL}/workout/history/2026-01-31?userId={user_id}")
        res.raise_for_status()
        workouts = res.json()
        if len(workouts) == 0:
            print("Error: No workouts found for date.")
            return
        
        print(f"Found {len(workouts)} workout session(s).")
        session = workouts[0]
        session_id = session['sessionId']
        print(f"Session ID: {session_id}")
        
        exercises = session['exercises']
        print(f"Exercises found: {len(exercises)}")
        
        # Find Bench Press
        bench = next((ex for ex in exercises if "벤치" in ex['name']), None)
        if not bench:
            print("Error: Bench Press not found")
        else:
            print("Bench Press found. Sets:", len(bench['sets']))
            first_set = bench['sets'][0]
            print(f"First set: {first_set['weight']}kg x {first_set['reps']}")
            set_id = first_set.get('setId') or first_set.get('id') # check key name
            
            # Step 4: Edit
            if set_id:
                print(f"\nStep 4: Edit Set {set_id}")
                edit_payload = {
                    "setEdits": [
                        {"setId": set_id, "weight": 65, "reps": 10}
                    ]
                }
                res = requests.put(f"{BASE_URL}/workout/sets", json=edit_payload)
                res.raise_for_status()
                print("Edit successful.")
                
                # Verify Edit
                res = requests.get(f"{BASE_URL}/workout/history/2026-01-31?userId={user_id}")
                new_data = res.json()
                new_set = new_data[0]['exercises'][0]['sets'][0]
                print(f"New First set: {new_set['weight']}kg x {new_set['reps']}")
                if new_set['weight'] == 65:
                    print("Verification Pass: Weight updated to 65")
                else:
                    print(f"Verification Fail: Weight is {new_set['weight']}")

    except Exception as e:
        print(f"History/Edit failed: {e}")
        return

    print("\nStep 5: Delete Session")
    try:
        res = requests.delete(f"{BASE_URL}/workout/session/{session_id}")
        res.raise_for_status()
        print("Delete successful.")
        
        # Verify Delete
        res = requests.get(f"{BASE_URL}/workout/history/2026-01-31?userId={user_id}")
        workouts = res.json()
        if len(workouts) == 0:
             print("Verification Pass: No workouts found.")
        else:
             print(f"Verification Fail: {len(workouts)} workouts still exist.")

    except Exception as e:
        print(f"Delete failed: {e}")

if __name__ == "__main__":
    run_test()
