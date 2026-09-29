# DLT TRAVELS - FIREBASE CONSOLE STEP-BY-STEP SETUP GUIDE

Follow these simple steps in the [Firebase Console](https://console.firebase.google.com/) to connect your database and admin login for your father:

---

### Step 1: Create a Free Firebase Project
1. Go to **[https://console.firebase.google.com/](https://console.firebase.google.com/)** and sign in with your Google account.
2. Click **"Add project"** (or **"Create a project"**).
3. Enter Project Name: `DLT-Travels` (or any name you prefer).
4. (Optional) Disable Google Analytics for simplicity, then click **"Create Project"**.

---

### Step 2: Enable Firebase Authentication (Email/Password)
1. In your Firebase project dashboard, click on **Build** in the left sidebar and select **Authentication**.
2. Click **"Get started"**.
3. Under the **Sign-in method** tab, click **Email/Password**.
4. Toggle **Enable** on for "Email/Password" (keep "Email link (passwordless sign-in)" disabled).
5. Click **"Save"**.
6. Switch to the **Users** tab (next to Sign-in method).
7. Click **"Add user"**:
   * **Email:** `father@dlttravels.com` (or your father's personal email).
   * **Password:** Choose a secure password (e.g. `dlt9493665524` or any password he can remember).
   * Click **"Add user"**.
   * *This is the single admin account your father will use to log into `/admin.html`!*

---

### Step 3: Create Cloud Firestore Database
1. In the left sidebar, click **Build** -> **Firestore Database**.
2. Click **"Create database"**.
3. Choose a location closest to your users (e.g., `asia-south1` Mumbai).
4. When asked about Security rules, choose **"Start in production mode"**, then click **"Create"**.

---

### Step 4: Add Firestore Security Rules
1. In Firestore Database, click on the **Rules** tab at the top.
2. Replace all the code inside the editor with the following:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // 1. Bookings Collection:
    // Anyone can create a booking; only authenticated admin (your father) can view, complete, or delete.
    match /bookings/{bookingId} {
      allow create: if true;
      allow read, update, delete: if request.auth != null;
    }

    // 2. Feedback Collection:
    // Anyone can post feedback; visitors only see approved reviews; only admin can approve or delete.
    match /feedback/{feedbackId} {
      allow create: if true;
      allow read: if resource.data.status == "approved" || request.auth != null;
      allow update, delete: if request.auth != null;
    }
  }
}
```
3. Click **"Publish"**.

---

### Step 5: Register Your Web App & Get Config Keys
1. In the Firebase Console, click the **Gear icon (Project settings)** at the top left next to "Project Overview".
2. Scroll down to the **"Your apps"** section and click the **Web icon (`</>`)**.
3. App nickname: `DLT Travels Web` and click **"Register app"**.
4. You will see a script with `firebaseConfig`:
```javascript
const firebaseConfig = {
  apiKey: "AIzaSy...",
  authDomain: "dlt-travels-....firebaseapp.com",
  projectId: "dlt-travels-...",
  storageBucket: "dlt-travels-....appspot.com",
  messagingSenderId: "...",
  appId: "..."
};
```
5. Copy this JSON object.
6. Open your DLT Travels Admin page (`/admin.html`), click the **"⚙️ Firebase"** button at the top, paste the JSON, and tap **"Save & Connect"**!
   *(Or paste it into `/firebase-config.js` in the `DEFAULT_FIREBASE_CONFIG` object).*

---

### Step 6: Test Father's Admin Flow
1. Visit `https://ais-dev-yrngvusakwqk4pbtaaie2y-102187581118.asia-southeast1.run.app/admin.html`.
2. Enter your father's email and password.
3. You will see:
   * **Upcoming Rides** (with phone call button, WhatsApp button, and "Mark Completed").
   * **Add New Phone Call Booking** button.
   * **Customer Feedback** with "Approve" button.
4. Try booking a test ride on the main website; watch it appear in his Admin list instantly!
