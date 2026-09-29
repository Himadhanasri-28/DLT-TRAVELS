/**
 * DLT Travels - Firebase Configuration & Storage Service
 * Handles Firestore & Firebase Authentication with automatic offline fallback.
 */

// Default configuration placeholder
const DEFAULT_FIREBASE_CONFIG = {
  apiKey: "",
  authDomain: "",
  projectId: "",
  storageBucket: "",
  messagingSenderId: "",
  appId: ""
};

// Retrieve active config (from localStorage if custom configured, or default)
function getActiveFirebaseConfig() {
  try {
    const saved = localStorage.getItem('dlt_firebase_custom_config');
    if (saved) {
      const parsed = JSON.parse(saved);
      if (parsed && parsed.projectId && parsed.apiKey) {
        return parsed;
      }
    }
  } catch (e) {
    console.warn('Error reading stored Firebase config', e);
  }
  return DEFAULT_FIREBASE_CONFIG;
}

let firebaseApp = null;
let firebaseAuth = null;
let firestoreDb = null;
let isFirebaseReady = false;

// Initialize Firebase if credentials exist
function initFirebase() {
  const config = getActiveFirebaseConfig();
  if (window.firebase && config.apiKey && config.projectId) {
    try {
      if (!firebase.apps.length) {
        firebaseApp = firebase.initializeApp(config);
      } else {
        firebaseApp = firebase.app();
      }
      firebaseAuth = firebase.auth();
      firestoreDb = firebase.firestore();
      isFirebaseReady = true;
      console.log('✅ Firebase initialized successfully for DLT Travels');
    } catch (err) {
      console.warn('⚠️ Firebase init error, continuing in local storage mode:', err);
      isFirebaseReady = false;
    }
  } else {
    isFirebaseReady = false;
    console.log('ℹ️ Running in local offline storage mode until Firebase config is added.');
  }
}

// Auto-initialize on script load
if (typeof window !== 'undefined') {
  if (window.firebase) {
    initFirebase();
  } else {
    window.addEventListener('load', () => {
      if (window.firebase) initFirebase();
    });
  }
}

// ============================================================================
// DATA STORAGE INTERFACE (Firestore with LocalStorage Fallback)
// ============================================================================

const DLT_DB = {
  // --- BOOKINGS ---
  async addBooking(bookingData) {
    const record = {
      id: 'bk_' + Date.now() + '_' + Math.random().toString(36).substring(2, 6),
      name: bookingData.name || '',
      phone: bookingData.phone || '',
      pickup: bookingData.pickup || '',
      drop: bookingData.drop || '',
      date: bookingData.date || '',
      time: bookingData.time || '',
      passengers: bookingData.passengers || '1',
      tripType: bookingData.tripType || 'Local City',
      message: bookingData.message || '',
      status: 'upcoming', // 'upcoming' | 'completed'
      createdAt: new Date().toISOString()
    };

    // Always mirror to localStorage for instantaneous offline availability
    try {
      const local = JSON.parse(localStorage.getItem('dlt_bookings') || '[]');
      local.unshift(record);
      localStorage.setItem('dlt_bookings', JSON.stringify(local));
    } catch (e) {}

    // Save to Firestore if available
    if (isFirebaseReady && firestoreDb) {
      try {
        await firestoreDb.collection('bookings').doc(record.id).set(record);
      } catch (err) {
        console.warn('Firestore add booking failed, saved locally:', err);
      }
    }

    return record;
  },

  async getBookings() {
    // Try Firestore first
    if (isFirebaseReady && firestoreDb && firebaseAuth && firebaseAuth.currentUser) {
      try {
        const snapshot = await firestoreDb.collection('bookings').orderBy('date', 'asc').get();
        const list = [];
        snapshot.forEach(doc => {
          list.push({ ...doc.data(), id: doc.id });
        });
        // Update local cache
        localStorage.setItem('dlt_bookings', JSON.stringify(list));
        return list;
      } catch (err) {
        console.warn('Firestore fetch failed, reading from local cache:', err);
      }
    }

    // Fallback to local storage
    try {
      const local = JSON.parse(localStorage.getItem('dlt_bookings') || '[]');
      return local;
    } catch (e) {
      return [];
    }
  },

  async updateBookingStatus(bookingId, newStatus) {
    // Update local cache
    try {
      const local = JSON.parse(localStorage.getItem('dlt_bookings') || '[]');
      const item = local.find(b => b.id === bookingId);
      if (item) item.status = newStatus;
      localStorage.setItem('dlt_bookings', JSON.stringify(local));
    } catch (e) {}

    if (isFirebaseReady && firestoreDb) {
      try {
        await firestoreDb.collection('bookings').doc(bookingId).update({ status: newStatus });
      } catch (err) {
        console.warn('Firestore update failed:', err);
      }
    }
  },

  async deleteBooking(bookingId) {
    try {
      let local = JSON.parse(localStorage.getItem('dlt_bookings') || '[]');
      local = local.filter(b => b.id !== bookingId);
      localStorage.setItem('dlt_bookings', JSON.stringify(local));
    } catch (e) {}

    if (isFirebaseReady && firestoreDb) {
      try {
        await firestoreDb.collection('bookings').doc(bookingId).delete();
      } catch (err) {
        console.warn('Firestore delete failed:', err);
      }
    }
  },

  // --- CUSTOMER FEEDBACK ---
  async addFeedback(feedbackData) {
    const record = {
      id: 'fb_' + Date.now() + '_' + Math.random().toString(36).substring(2, 6),
      name: feedbackData.name || 'Valued Customer',
      rating: parseInt(feedbackData.rating, 10) || 5,
      message: feedbackData.message || '',
      status: 'pending', // 'pending' | 'approved'
      createdAt: new Date().toISOString()
    };

    try {
      const local = JSON.parse(localStorage.getItem('dlt_feedback') || '[]');
      local.unshift(record);
      localStorage.setItem('dlt_feedback', JSON.stringify(local));
    } catch (e) {}

    if (isFirebaseReady && firestoreDb) {
      try {
        await firestoreDb.collection('feedback').doc(record.id).set(record);
      } catch (err) {
        console.warn('Firestore add feedback failed:', err);
      }
    }

    return record;
  },

  async getApprovedFeedback() {
    if (isFirebaseReady && firestoreDb) {
      try {
        const snapshot = await firestoreDb.collection('feedback')
          .where('status', '==', 'approved')
          .get();
        const list = [];
        snapshot.forEach(doc => {
          list.push({ ...doc.data(), id: doc.id });
        });
        if (list.length > 0) return list;
      } catch (err) {
        console.warn('Firestore feedback fetch failed, using cache:', err);
      }
    }

    // Local storage filtered for approved
    try {
      const local = JSON.parse(localStorage.getItem('dlt_feedback') || '[]');
      const approved = local.filter(f => f.status === 'approved');
      if (approved.length > 0) return approved;
    } catch (e) {}

    // Fallback seed feedback for immediate demonstration if empty
    return [
      {
        id: 'sample_1',
        name: 'Suresh Varma',
        rating: 5,
        message: 'Booked DLT Travels for a 2-day family trip to Araku Valley. The Maruti Dzire was spotless and the AC was chilling throughout. Safe, defensive driving on the ghat roads!',
        createdAt: '2026-09-20T10:00:00Z',
        status: 'approved'
      },
      {
        id: 'sample_2',
        name: 'Dr. Ananya Rao',
        rating: 5,
        message: 'Prompt 4:30 AM airport pickup from MVP Colony to Visakhapatnam Airport (VTZ). Driver reached 10 minutes early and helped with all luggage. Very dependable!',
        createdAt: '2026-09-22T14:30:00Z',
        status: 'approved'
      },
      {
        id: 'sample_3',
        name: 'K. Rajesh',
        rating: 5,
        message: 'Great full-day city ride covering Simhachalam Temple and Rushikonda Beach. Very clean sedan with rear AC vents, very courteous driver.',
        createdAt: '2026-09-24T18:00:00Z',
        status: 'approved'
      }
    ];
  },

  async getAllFeedbackForAdmin() {
    if (isFirebaseReady && firestoreDb && firebaseAuth && firebaseAuth.currentUser) {
      try {
        const snapshot = await firestoreDb.collection('feedback').get();
        const list = [];
        snapshot.forEach(doc => {
          list.push({ ...doc.data(), id: doc.id });
        });
        localStorage.setItem('dlt_feedback', JSON.stringify(list));
        return list;
      } catch (err) {
        console.warn('Firestore admin feedback fetch error:', err);
      }
    }

    try {
      const local = JSON.parse(localStorage.getItem('dlt_feedback') || '[]');
      return local;
    } catch (e) {
      return [];
    }
  },

  async updateFeedbackStatus(feedbackId, newStatus) {
    try {
      const local = JSON.parse(localStorage.getItem('dlt_feedback') || '[]');
      const item = local.find(f => f.id === feedbackId);
      if (item) item.status = newStatus;
      localStorage.setItem('dlt_feedback', JSON.stringify(local));
    } catch (e) {}

    if (isFirebaseReady && firestoreDb) {
      try {
        await firestoreDb.collection('feedback').doc(feedbackId).update({ status: newStatus });
      } catch (err) {
        console.warn('Firestore feedback update error:', err);
      }
    }
  },

  async deleteFeedback(feedbackId) {
    try {
      let local = JSON.parse(localStorage.getItem('dlt_feedback') || '[]');
      local = local.filter(f => f.id !== feedbackId);
      localStorage.setItem('dlt_feedback', JSON.stringify(local));
    } catch (e) {}

    if (isFirebaseReady && firestoreDb) {
      try {
        await firestoreDb.collection('feedback').doc(feedbackId).delete();
      } catch (err) {
        console.warn('Firestore feedback delete error:', err);
      }
    }
  }
};
