/**
 * DLT Travels - Main Public Controller
 * Car animations, WhatsApp booking with database save, feedback carousel, and AI chatbot.
 */

document.addEventListener('DOMContentLoaded', () => {
  // 1. Current Year in Footer
  const yearElem = document.getElementById('year-copy');
  if (yearElem) {
    yearElem.textContent = new Date().getFullYear();
  }

  // Set default booking date to tomorrow
  const dateInput = document.getElementById('book-date');
  if (dateInput) {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    dateInput.value = tomorrow.toISOString().split('T')[0];
    dateInput.min = new Date().toISOString().split('T')[0];
  }

  // ==========================================================================
  // 2. ABOUT SECTION: Scroll-Triggered Car Animation
  // ==========================================================================
  const aboutRunner = document.getElementById('about-car-runner');
  const aboutSection = document.getElementById('about');

  if (aboutRunner && aboutSection && 'IntersectionObserver' in window) {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          aboutRunner.classList.add('drive-active');
        }
      });
    }, { threshold: 0.25 });

    observer.observe(aboutSection);
  }

  // ==========================================================================
  // 3. BOOKING FORM: Submit to DB & Open WhatsApp
  // ==========================================================================
  const bookingForm = document.getElementById('public-booking-form');
  const successBox = document.getElementById('booking-success-box');
  const manualWhatsAppLink = document.getElementById('manual-whatsapp-link');
  const bookAnotherBtn = document.getElementById('btn-book-another');

  if (bookingForm) {
    bookingForm.addEventListener('submit', async (e) => {
      e.preventDefault();

      const name = document.getElementById('book-name').value.trim();
      const phone = document.getElementById('book-phone').value.trim();
      const pickup = document.getElementById('book-pickup').value.trim();
      const drop = document.getElementById('book-drop').value.trim();
      const date = document.getElementById('book-date').value;
      const time = document.getElementById('book-time').value;
      const passengers = document.getElementById('book-passengers').value;
      const tripType = document.getElementById('book-trip-type').value;
      const message = document.getElementById('book-message').value.trim();

      // Formatted WhatsApp message
      const formattedWhatsApp = 
`🚖 *NEW CAR BOOKING ENQUIRY - DLT TRAVELS*
━━━━━━━━━━━━━━━━━━━━
👤 *Customer:* ${name}
📞 *Contact:* ${phone}
📍 *Pickup:* ${pickup}
🏁 *Destination:* ${drop}
📅 *Date:* ${date}
⏰ *Time:* ${time}
👥 *Passengers:* ${passengers}
🚗 *Car:* Maruti Suzuki Dzire (Sedan)
🛣️ *Trip Type:* ${tripType}
${message ? `📝 *Notes:* ${message}\n` : ''}━━━━━━━━━━━━━━━━━━━━
_Please confirm vehicle availability and rate quote._`;

      const encodedMessage = encodeURIComponent(formattedWhatsApp);
      const whatsappUrl = `https://wa.me/919493665524?text=${encodedMessage}`;

      // (A) Save to Database (Firestore + LocalStorage)
      if (window.DLT_DB) {
        try {
          await window.DLT_DB.addBooking({
            name,
            phone,
            pickup,
            drop,
            date,
            time,
            passengers,
            tripType,
            message
          });
        } catch (dbErr) {
          console.warn('Booking save warning:', dbErr);
        }
      }

      // (B) Send lead to n8n AI webhook in background
      try {
        const sid = getOrCreateSessionId();
        fetch('https://himadhanasri.app.n8n.cloud/webhook/248c973a-3532-45d7-b22f-c598eaa7d103/chat', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            action: 'sendMessage',
            sessionId: sid,
            chatInput: `New Booking: ${name} (${phone}) requested ${tripType} from ${pickup} to ${drop} on ${date} at ${time}.`,
            customerName: name,
            customerPhone: phone,
            pickupLocation: pickup,
            destinationLocation: drop,
            travelDate: date,
            travelTime: time,
            passengers,
            tripType,
            additionalMessage: message,
            timestamp: new Date().toISOString()
          })
        }).catch(() => {});
      } catch (e) {}

      // (C) Show friendly success card with driving away car animation
      if (successBox) {
        bookingForm.style.display = 'none';
        successBox.classList.remove('hidden');
        if (manualWhatsAppLink) {
          manualWhatsAppLink.href = whatsappUrl;
        }
      }

      // (D) Open WhatsApp directly in new tab
      window.open(whatsappUrl, '_blank');
    });
  }

  if (bookAnotherBtn && bookingForm && successBox) {
    bookAnotherBtn.addEventListener('click', () => {
      bookingForm.reset();
      bookingForm.style.display = 'block';
      successBox.classList.add('hidden');
    });
  }

  // ==========================================================================
  // 4. CUSTOMER FEEDBACK: Approved Carousel & Submission
  // ==========================================================================
  const feedbackContainer = document.getElementById('feedback-cards-container');
  const feedbackForm = document.getElementById('public-feedback-form');
  const feedbackSuccessMsg = document.getElementById('feedback-success-msg');
  const starPicker = document.getElementById('star-picker');
  const ratingInput = document.getElementById('fb-rating');

  // Load and render approved reviews
  async function loadApprovedReviews() {
    if (!feedbackContainer) return;
    try {
      const reviews = window.DLT_DB ? await window.DLT_DB.getApprovedFeedback() : [];

      if (!reviews || reviews.length === 0) {
        feedbackContainer.innerHTML = `
          <div class="feedback-scroll-card" style="flex: 0 0 100%; text-align: center;">
            <p>🌟 Be the first to leave a review after your journey with DLT Travels!</p>
          </div>
        `;
        return;
      }

      feedbackContainer.innerHTML = reviews.map(r => {
        const starCount = Math.max(1, Math.min(5, parseInt(r.rating, 10) || 5));
        const stars = '★'.repeat(starCount) + '☆'.repeat(5 - starCount);
        return `
          <div class="feedback-scroll-card">
            <div>
              <div class="feedback-stars-row">${stars}</div>
              <p class="feedback-quote">"${escapeHtml(r.message || '')}"</p>
            </div>
            <div class="feedback-author-row">
              <span class="feedback-author-name">${escapeHtml(r.name || 'Valued Customer')}</span>
              <span class="feedback-badge-verified">Verified Ride</span>
            </div>
          </div>
        `;
      }).join('');
    } catch (e) {
      console.warn('Error loading reviews:', e);
    }
  }

  // Star Rating Click Handler
  if (starPicker && ratingInput) {
    const starBtns = starPicker.querySelectorAll('.star-btn');
    starBtns.forEach(btn => {
      btn.addEventListener('click', () => {
        const val = parseInt(btn.getAttribute('data-rating'), 10);
        ratingInput.value = val;
        starBtns.forEach(b => {
          const bVal = parseInt(b.getAttribute('data-rating'), 10);
          if (bVal <= val) {
            b.classList.add('active');
          } else {
            b.classList.remove('active');
          }
        });
      });
    });
  }

  // Handle Feedback Submission
  if (feedbackForm) {
    feedbackForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const name = document.getElementById('fb-name').value.trim();
      const rating = parseInt(ratingInput ? ratingInput.value : 5, 10);
      const message = document.getElementById('fb-message').value.trim();

      if (window.DLT_DB) {
        await window.DLT_DB.addFeedback({ name, rating, message });
      }

      feedbackForm.reset();
      // Reset stars to 5
      if (starPicker) {
        starPicker.querySelectorAll('.star-btn').forEach(b => b.classList.add('active'));
        if (ratingInput) ratingInput.value = 5;
      }

      if (feedbackSuccessMsg) {
        feedbackSuccessMsg.classList.remove('hidden');
        setTimeout(() => {
          feedbackSuccessMsg.classList.add('hidden');
        }, 8000);
      }
    });
  }

  // Initial load of approved reviews
  loadApprovedReviews();

  // ==========================================================================
  // 5. DLT TRAVELS AI CHATBOT CONTROLLER (n8n Webhook)
  // ==========================================================================
  const n8nChatWebhook = 'https://himadhanasri.app.n8n.cloud/webhook/248c973a-3532-45d7-b22f-c598eaa7d103/chat';

  window.toggleDltChat = function(forceOpen) {
    const chatWindow = document.getElementById('dlt-chat-window');
    if (!chatWindow) return;

    const isClosed = chatWindow.style.display === 'none' || chatWindow.style.display === '';
    const shouldOpen = forceOpen !== undefined ? forceOpen : isClosed;

    if (shouldOpen) {
      chatWindow.style.display = 'flex';
      const input = document.getElementById('dlt-chat-input');
      if (input) setTimeout(() => input.focus(), 150);
      scrollChatDown();
    } else {
      chatWindow.style.display = 'none';
    }
  };

  window.sendQuickChatMessage = function(text) {
    const input = document.getElementById('dlt-chat-input');
    if (input) input.value = text;
    submitUserMessage(text);
  };

  window.handleChatSubmit = function(e) {
    if (e && e.preventDefault) e.preventDefault();
    const input = document.getElementById('dlt-chat-input');
    if (!input) return;
    const message = input.value.trim();
    if (!message) return;
    input.value = '';
    submitUserMessage(message);
  };

  function scrollChatDown() {
    const container = document.getElementById('dlt-chat-messages');
    if (container) {
      container.scrollTop = container.scrollHeight;
    }
  }

  async function submitUserMessage(userText) {
    const messagesContainer = document.getElementById('dlt-chat-messages');
    if (!messagesContainer) return;

    // 1. Append User Message Bubble
    const userRow = document.createElement('div');
    userRow.className = 'dlt-bubble-row dlt-row-user';
    userRow.innerHTML = `
      <div class="dlt-bubble-card"><p>${escapeHtml(userText)}</p></div>
      <div class="dlt-avatar-icon">👤</div>
    `;
    messagesContainer.appendChild(userRow);
    scrollChatDown();

    // Hide quick suggestion chips after first question
    const chips = document.getElementById('dlt-quick-chips');
    if (chips) chips.style.display = 'none';

    // 2. Append Typing Indicator
    const typingRow = document.createElement('div');
    typingRow.className = 'dlt-bubble-row dlt-row-bot';
    typingRow.innerHTML = `
      <div class="dlt-avatar-icon">🚗</div>
      <div class="dlt-bubble-card">
        <div class="dlt-bouncing-dots">
          <span></span><span></span><span></span>
        </div>
      </div>
    `;
    messagesContainer.appendChild(typingRow);
    scrollChatDown();

    // 3. Make fetch call to user's n8n webhook
    const sid = getOrCreateSessionId();
    try {
      const response = await fetch(n8nChatWebhook, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          action: 'sendMessage',
          sessionId: sid,
          chatInput: userText
        })
      });

      if (typingRow && typingRow.parentNode) {
        typingRow.parentNode.removeChild(typingRow);
      }

      if (response.ok) {
        const data = await response.json();
        let reply = '';
        if (typeof data.output === 'string') {
          reply = data.output;
        } else if (Array.isArray(data) && data[0] && data[0].output) {
          reply = data[0].output;
        } else if (data.text) {
          reply = data.text;
        } else if (data.message) {
          reply = data.message;
        } else {
          reply = 'Thank you for reaching out to DLT Travels! For instant bookings with our Maruti Suzuki Dzire, please call or WhatsApp us on +91 9493665524.';
        }
        appendBotReply(reply);
      } else {
        appendBotReply('Our AI assistant is temporarily busy. You can instantly book or get a fare quote by calling or WhatsApping **+91 9493665524**!');
      }
    } catch (err) {
      if (typingRow && typingRow.parentNode) {
        typingRow.parentNode.removeChild(typingRow);
      }
      appendBotReply('Connection error. Please call **+91 9493665524** or message us directly on WhatsApp for immediate service!');
    }
  }

  function appendBotReply(text) {
    const messagesContainer = document.getElementById('dlt-chat-messages');
    if (!messagesContainer) return;

    const botRow = document.createElement('div');
    botRow.className = 'dlt-bubble-row dlt-row-bot';

    const formatted = escapeHtml(text)
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(.*?)\*/g, '<em>$1</em>')
      .replace(/\n/g, '<br/>');

    botRow.innerHTML = `
      <div class="dlt-avatar-icon">🚗</div>
      <div class="dlt-bubble-card"><p>${formatted}</p></div>
    `;
    messagesContainer.appendChild(botRow);
    scrollChatDown();
  }

  // Helper for Session ID (Used by booking lead delivery & chatbot)
  function getOrCreateSessionId() {
    try {
      let sid = localStorage.getItem('dlt_travels_chat_session');
      if (!sid) {
        sid = 'dlt-' + Date.now() + '-' + Math.random().toString(36).substring(2, 9);
        localStorage.setItem('dlt_travels_chat_session', sid);
      }
      return sid;
    } catch (e) {
      return 'dlt-' + Date.now();
    }
  }

  function escapeHtml(str) {
    if (!str) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
  }
});
