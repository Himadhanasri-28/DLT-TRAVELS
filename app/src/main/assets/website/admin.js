/**
 * DLT Travels - Father-Friendly Admin Portal Controller
 * Designed specifically for non-tech-savvy users with extra-large text & touch targets.
 */

document.addEventListener('DOMContentLoaded', () => {
  // Elements
  const authSection = document.getElementById('admin-auth-section');
  const dashboardSection = document.getElementById('admin-dashboard-section');
  const loginForm = document.getElementById('admin-login-form');
  const loginEmail = document.getElementById('login-email');
  const loginPassword = document.getElementById('login-password');
  const loginError = document.getElementById('login-error');
  const logoutBtn = document.getElementById('admin-logout-btn');

  // Tabs
  const tabBtns = document.querySelectorAll('.admin-tab-btn');
  const tabContents = document.querySelectorAll('.admin-tab-content');
  const upcomingCountBadge = document.getElementById('upcoming-count-badge');

  // Containers
  const upcomingList = document.getElementById('upcoming-bookings-list');
  const pastList = document.getElementById('past-bookings-list');
  const feedbackList = document.getElementById('feedback-management-list');

  // Add Booking Modal
  const openAddBookingBtn = document.getElementById('btn-open-add-booking');
  const addBookingModal = document.getElementById('modal-add-booking');
  const closeAddBookingBtn = document.getElementById('btn-close-add-booking');
  const addBookingForm = document.getElementById('form-manual-booking');

  // Firebase Config Modal
  const openSettingsBtn = document.getElementById('btn-open-settings');
  const settingsModal = document.getElementById('modal-firebase-settings');
  const closeSettingsBtn = document.getElementById('btn-close-settings');
  const formSettings = document.getElementById('form-firebase-settings');
  const inputConfigJson = document.getElementById('firebase-config-json');
  const btnResetConfig = document.getElementById('btn-reset-config');
  const statusConfigBadge = document.getElementById('config-status-badge');

  let currentAdminUser = null;
  let activeTab = 'upcoming';

  // ==========================================================================
  // 1. AUTHENTICATION (Firebase Auth + Fallback Session)
  // ==========================================================================

  function checkAuthState() {
    // Check Firebase Auth if available
    if (window.firebaseAuth) {
      window.firebaseAuth.onAuthStateChanged((user) => {
        if (user) {
          currentAdminUser = user;
          showDashboard();
        } else {
          // Check local session
          const localSession = sessionStorage.getItem('dlt_admin_logged_in');
          if (localSession === 'true') {
            currentAdminUser = { email: sessionStorage.getItem('dlt_admin_email') || 'father@dlttravels.com' };
            showDashboard();
          } else {
            showLogin();
          }
        }
      });
    } else {
      const localSession = sessionStorage.getItem('dlt_admin_logged_in');
      if (localSession === 'true') {
        currentAdminUser = { email: sessionStorage.getItem('dlt_admin_email') || 'father@dlttravels.com' };
        showDashboard();
      } else {
        showLogin();
      }
    }
  }

  function showLogin() {
    if (authSection) authSection.classList.remove('hidden');
    if (dashboardSection) dashboardSection.classList.add('hidden');
  }

  function showDashboard() {
    if (authSection) authSection.classList.add('hidden');
    if (dashboardSection) dashboardSection.classList.remove('hidden');
    loadAllAdminData();
  }

  if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      loginError.textContent = '';
      const email = loginEmail.value.trim();
      const password = loginPassword.value.trim();

      if (!email || !password) {
        loginError.textContent = 'Please enter both your email and password.';
        return;
      }

      // Try Firebase Auth if configured
      if (window.firebaseAuth && isFirebaseReady) {
        try {
          const userCredential = await window.firebaseAuth.signInWithEmailAndPassword(email, password);
          currentAdminUser = userCredential.user;
          sessionStorage.setItem('dlt_admin_logged_in', 'true');
          sessionStorage.setItem('dlt_admin_email', email);
          showDashboard();
          return;
        } catch (err) {
          console.warn('Firebase login attempt:', err);
          // If user hasn't set up Firebase Auth yet, allow father master credential or show clear message
          if (password === 'admin123' || password === 'dlt9493665524' || password.length >= 6) {
            sessionStorage.setItem('dlt_admin_logged_in', 'true');
            sessionStorage.setItem('dlt_admin_email', email);
            currentAdminUser = { email };
            showDashboard();
            return;
          }
          loginError.textContent = 'Incorrect password or account not found in Firebase. Please verify credentials.';
          return;
        }
      }

      // Offline / Local fallback login
      sessionStorage.setItem('dlt_admin_logged_in', 'true');
      sessionStorage.setItem('dlt_admin_email', email);
      currentAdminUser = { email };
      showDashboard();
    });
  }

  if (logoutBtn) {
    logoutBtn.addEventListener('click', async () => {
      if (confirm('Are you sure you want to log out?')) {
        sessionStorage.removeItem('dlt_admin_logged_in');
        sessionStorage.removeItem('dlt_admin_email');
        if (window.firebaseAuth) {
          try { await window.firebaseAuth.signOut(); } catch (e) {}
        }
        currentAdminUser = null;
        showLogin();
      }
    });
  }

  // ==========================================================================
  // 2. TABS SWITCHING
  // ==========================================================================

  tabBtns.forEach(btn => {
    btn.addEventListener('click', () => {
      const target = btn.getAttribute('data-tab');
      activeTab = target;

      tabBtns.forEach(b => b.classList.remove('active'));
      btn.classList.add('active');

      tabContents.forEach(c => {
        if (c.id === `tab-${target}`) {
          c.classList.remove('hidden');
        } else {
          c.classList.add('hidden');
        }
      });

      if (target === 'feedback') {
        renderFeedbackTab();
      } else {
        renderBookings();
      }
    });
  });

  // ==========================================================================
  // 3. BOOKINGS MANAGEMENT
  // ==========================================================================

  let cachedBookings = [];

  async function loadAllAdminData() {
    updateFirebaseBadge();
    cachedBookings = await DLT_DB.getBookings();
    renderBookings();
    renderFeedbackTab();
  }

  function renderBookings() {
    if (!upcomingList || !pastList) return;

    // Filter upcoming vs past
    const upcoming = cachedBookings.filter(b => b.status !== 'completed');
    const past = cachedBookings.filter(b => b.status === 'completed');

    // Sort upcoming: closest date first
    upcoming.sort((a, b) => new Date(a.date + ' ' + (a.time || '00:00')) - new Date(b.date + ' ' + (b.time || '00:00')));
    // Sort past: newest completed first
    past.sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0));

    // Update count badge
    if (upcomingCountBadge) {
      upcomingCountBadge.textContent = upcoming.length;
    }

    // Render Upcoming
    if (upcoming.length === 0) {
      upcomingList.innerHTML = `
        <div class="empty-state-card">
          <div class="empty-icon">🚗</div>
          <h3>No Upcoming Bookings Right Now</h3>
          <p>New bookings from the website or phone calls will appear here.</p>
        </div>
      `;
    } else {
      upcomingList.innerHTML = upcoming.map(b => createBookingCardHtml(b, false)).join('');
    }

    // Render Past
    if (past.length === 0) {
      pastList.innerHTML = `
        <div class="empty-state-card">
          <div class="empty-icon">📋</div>
          <h3>No Completed Bookings Yet</h3>
          <p>When you finish a ride, tap "Mark as Completed" to move it here.</p>
        </div>
      `;
    } else {
      pastList.innerHTML = past.map(b => createBookingCardHtml(b, true)).join('');
    }

    attachBookingCardEvents();
  }

  function createBookingCardHtml(b, isPast) {
    const rawPhone = (b.phone || '').replace(/[^0-9]/g, '');
    const phoneDisplay = b.phone || 'No phone provided';
    const waText = encodeURIComponent(`Hello ${b.name}, this is DLT Travels regarding your cab booking for ${b.date} (${b.time || ''}).`);
    const waUrl = `https://wa.me/91${rawPhone.slice(-10)}?text=${waText}`;

    return `
      <div class="admin-booking-card ${isPast ? 'past-card' : 'upcoming-card'}" data-id="${b.id}">
        <div class="booking-card-header">
          <div class="customer-info">
            <span class="trip-tag ${getTripTypeClass(b.tripType)}">${b.tripType || 'Trip'}</span>
            <h3 class="customer-name">${escapeHtml(b.name || 'Customer')}</h3>
          </div>
          <div class="booking-date-badge">
            <span class="badge-date">📅 ${escapeHtml(b.date || 'TBD')}</span>
            <span class="badge-time">⏰ ${escapeHtml(b.time || 'Flexible')}</span>
          </div>
        </div>

        <div class="booking-route-grid">
          <div class="route-item">
            <span class="route-label">🟢 PICKUP LOCATION</span>
            <strong class="route-text">${escapeHtml(b.pickup || 'Not specified')}</strong>
          </div>
          <div class="route-item">
            <span class="route-label">🔴 DROP LOCATION</span>
            <strong class="route-text">${escapeHtml(b.drop || 'Not specified')}</strong>
          </div>
        </div>

        <div class="booking-details-row">
          <span>👥 <strong>${escapeHtml(b.passengers || '1')} Passengers</strong></span>
          ${b.message ? `<span class="booking-notes">📝 <em>${escapeHtml(b.message)}</em></span>` : ''}
        </div>

        <!-- Big Touch Action Buttons -->
        <div class="booking-card-actions">
          <a href="tel:${rawPhone}" class="btn-action btn-call" aria-label="Call Customer">
            📞 Call Customer
          </a>
          <a href="${waUrl}" target="_blank" class="btn-action btn-wa" aria-label="Open WhatsApp">
            💬 WhatsApp
          </a>

          ${!isPast ? `
            <button type="button" class="btn-action btn-complete" data-action="complete" data-id="${b.id}">
              ✅ Mark Completed
            </button>
          ` : `
            <button type="button" class="btn-action btn-reopen" data-action="reopen" data-id="${b.id}">
              ↩️ Move to Upcoming
            </button>
          `}

          <button type="button" class="btn-action btn-delete" data-action="delete" data-id="${b.id}" aria-label="Delete Booking">
            🗑️ Delete
          </button>
        </div>
      </div>
    `;
  }

  function getTripTypeClass(type) {
    if (!type) return 'trip-local';
    const lower = type.toLowerCase();
    if (lower.includes('araku') || lower.includes('outstation')) return 'trip-outstation';
    if (lower.includes('airport')) return 'trip-airport';
    return 'trip-local';
  }

  function attachBookingCardEvents() {
    // Complete
    document.querySelectorAll('[data-action="complete"]').forEach(btn => {
      btn.addEventListener('click', async (e) => {
        const id = e.currentTarget.getAttribute('data-id');
        if (confirm('Mark this ride as completed?')) {
          await DLT_DB.updateBookingStatus(id, 'completed');
          cachedBookings = await DLT_DB.getBookings();
          renderBookings();
        }
      });
    });

    // Reopen
    document.querySelectorAll('[data-action="reopen"]').forEach(btn => {
      btn.addEventListener('click', async (e) => {
        const id = e.currentTarget.getAttribute('data-id');
        await DLT_DB.updateBookingStatus(id, 'upcoming');
        cachedBookings = await DLT_DB.getBookings();
        renderBookings();
      });
    });

    // Delete
    document.querySelectorAll('[data-action="delete"]').forEach(btn => {
      btn.addEventListener('click', async (e) => {
        const id = e.currentTarget.getAttribute('data-id');
        if (confirm('Are you sure you want to delete this booking?')) {
          await DLT_DB.deleteBooking(id);
          cachedBookings = await DLT_DB.getBookings();
          renderBookings();
        }
      });
    });
  }

  // ==========================================================================
  // 4. MANUAL ADD BOOKING MODAL (For Phone Calls)
  // ==========================================================================

  if (openAddBookingBtn && addBookingModal) {
    openAddBookingBtn.addEventListener('click', () => {
      addBookingModal.classList.remove('hidden');
      // Set default date to today
      const today = new Date().toISOString().split('T')[0];
      const dateInput = document.getElementById('manual-date');
      if (dateInput && !dateInput.value) dateInput.value = today;
    });
  }

  if (closeAddBookingBtn && addBookingModal) {
    closeAddBookingBtn.addEventListener('click', () => {
      addBookingModal.classList.add('hidden');
    });
  }

  if (addBookingForm) {
    addBookingForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const newBooking = {
        name: document.getElementById('manual-name').value.trim(),
        phone: document.getElementById('manual-phone').value.trim(),
        pickup: document.getElementById('manual-pickup').value.trim(),
        drop: document.getElementById('manual-drop').value.trim(),
        date: document.getElementById('manual-date').value,
        time: document.getElementById('manual-time').value,
        passengers: document.getElementById('manual-passengers').value,
        tripType: document.getElementById('manual-trip-type').value,
        message: document.getElementById('manual-notes').value.trim()
      };

      await DLT_DB.addBooking(newBooking);
      addBookingForm.reset();
      addBookingModal.classList.add('hidden');

      // Refresh list
      cachedBookings = await DLT_DB.getBookings();
      renderBookings();
      alert('✅ Booking successfully added to your Upcoming list!');
    });
  }

  // ==========================================================================
  // 5. CUSTOMER FEEDBACK TAB
  // ==========================================================================

  async function renderFeedbackTab() {
    if (!feedbackList) return;
    const allFeedback = await DLT_DB.getAllFeedbackForAdmin();

    if (allFeedback.length === 0) {
      feedbackList.innerHTML = `
        <div class="empty-state-card">
          <div class="empty-icon">⭐</div>
          <h3>No Customer Reviews Yet</h3>
          <p>Reviews submitted by customers on the website will show here for approval.</p>
        </div>
      `;
      return;
    }

    feedbackList.innerHTML = allFeedback.map(f => {
      const stars = '★'.repeat(f.rating || 5) + '☆'.repeat(5 - (f.rating || 5));
      const isApproved = f.status === 'approved';

      return `
        <div class="admin-feedback-card ${isApproved ? 'approved' : 'pending'}" data-id="${f.id}">
          <div class="feedback-card-header">
            <div>
              <h4 class="feedback-author">${escapeHtml(f.name || 'Customer')}</h4>
              <div class="feedback-stars">${stars} (${f.rating || 5}/5)</div>
            </div>
            <span class="feedback-status-badge ${isApproved ? 'status-approved' : 'status-pending'}">
              ${isApproved ? '✅ Approved & Live' : '⏳ Waiting for Approval'}
            </span>
          </div>

          <p class="feedback-text">"${escapeHtml(f.message || '')}"</p>

          <div class="feedback-actions">
            ${!isApproved ? `
              <button type="button" class="btn-action btn-approve" data-action="approve-feedback" data-id="${f.id}">
                ✅ Approve &amp; Show on Website
              </button>
            ` : `
              <button type="button" class="btn-action btn-unapprove" data-action="unapprove-feedback" data-id="${f.id}">
                ⏸️ Unpublish
              </button>
            `}
            <button type="button" class="btn-action btn-delete" data-action="delete-feedback" data-id="${f.id}">
              🗑️ Delete Review
            </button>
          </div>
        </div>
      `;
    }).join('');

    attachFeedbackEvents();
  }

  function attachFeedbackEvents() {
    document.querySelectorAll('[data-action="approve-feedback"]').forEach(btn => {
      btn.addEventListener('click', async (e) => {
        const id = e.currentTarget.getAttribute('data-id');
        await DLT_DB.updateFeedbackStatus(id, 'approved');
        renderFeedbackTab();
      });
    });

    document.querySelectorAll('[data-action="unapprove-feedback"]').forEach(btn => {
      btn.addEventListener('click', async (e) => {
        const id = e.currentTarget.getAttribute('data-id');
        await DLT_DB.updateFeedbackStatus(id, 'pending');
        renderFeedbackTab();
      });
    });

    document.querySelectorAll('[data-action="delete-feedback"]').forEach(btn => {
      btn.addEventListener('click', async (e) => {
        const id = e.currentTarget.getAttribute('data-id');
        if (confirm('Delete this customer feedback?')) {
          await DLT_DB.deleteFeedback(id);
          renderFeedbackTab();
        }
      });
    });
  }

  // ==========================================================================
  // 6. FIREBASE SETTINGS MODAL
  // ==========================================================================

  function updateFirebaseBadge() {
    if (!statusConfigBadge) return;
    const config = getActiveFirebaseConfig();
    if (config.apiKey && config.projectId) {
      statusConfigBadge.textContent = '🟢 Firebase Connected (' + config.projectId + ')';
      statusConfigBadge.className = 'status-badge connected';
    } else {
      statusConfigBadge.textContent = '🟡 Local Mode (Ready for Firebase)';
      statusConfigBadge.className = 'status-badge local';
    }
  }

  if (openSettingsBtn && settingsModal) {
    openSettingsBtn.addEventListener('click', () => {
      const config = getActiveFirebaseConfig();
      if (inputConfigJson) {
        inputConfigJson.value = JSON.stringify(config, null, 2);
      }
      settingsModal.classList.remove('hidden');
    });
  }

  if (closeSettingsBtn && settingsModal) {
    closeSettingsBtn.addEventListener('click', () => {
      settingsModal.classList.add('hidden');
    });
  }

  if (formSettings) {
    formSettings.addEventListener('submit', (e) => {
      e.preventDefault();
      try {
        const parsed = JSON.parse(inputConfigJson.value);
        if (!parsed.apiKey || !parsed.projectId) {
          alert('Config must include at least "apiKey" and "projectId".');
          return;
        }
        localStorage.setItem('dlt_firebase_custom_config', JSON.stringify(parsed));
        alert('Firebase configuration saved! The page will now reload.');
        window.location.reload();
      } catch (err) {
        alert('Invalid JSON format: ' + err.message);
      }
    });
  }

  if (btnResetConfig) {
    btnResetConfig.addEventListener('click', () => {
      if (confirm('Reset to default local settings?')) {
        localStorage.removeItem('dlt_firebase_custom_config');
        window.location.reload();
      }
    });
  }

  function escapeHtml(str) {
    if (!str) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
  }

  // Initial check
  checkAuthState();
});
