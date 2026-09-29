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
  // 5. AI TRAVEL ASSISTANT CHATBOT (n8n Webhook Integration)
  // ==========================================================================
  const n8nWebhookUrl = 'https://himadhanasri.app.n8n.cloud/webhook/248c973a-3532-45d7-b22f-c598eaa7d103/chat';

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

  const chatToggleBtn = document.getElementById('dlt-chat-toggle-btn');
  const chatWindow = document.getElementById('dlt-chat-window');
  const chatCloseBtn = document.getElementById('dlt-chat-close-btn');
  const chatForm = document.getElementById('dlt-chat-form');
  const chatInput = document.getElementById('dlt-chat-input');
  const chatMessages = document.getElementById('dlt-chat-messages');
  const chatSuggestions = document.getElementById('dlt-chat-suggestions');

  if (chatToggleBtn && chatWindow) {
    chatToggleBtn.addEventListener('click', () => {
      const isHidden = chatWindow.classList.contains('dlt-chat-hidden');
      if (isHidden) {
        chatWindow.classList.remove('dlt-chat-hidden');
        if (chatInput) chatInput.focus();
        scrollChatToBottom();
      } else {
        chatWindow.classList.add('dlt-chat-hidden');
      }
    });

    if (chatCloseBtn) {
      chatCloseBtn.addEventListener('click', () => {
        chatWindow.classList.add('dlt-chat-hidden');
      });
    }

    if (chatSuggestions) {
      chatSuggestions.addEventListener('click', (e) => {
        const chip = e.target.closest('.dlt-chip-btn');
        if (chip) {
          const query = chip.getAttribute('data-query');
          if (query && chatInput) {
            chatInput.value = query;
            handleUserChatMessage(query);
          }
        }
      });
    }

    if (chatForm && chatInput) {
      chatForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const text = chatInput.value.trim();
        if (!text) return;
        handleUserChatMessage(text);
      });
    }
  }

  function scrollChatToBottom() {
    if (chatMessages) {
      chatMessages.scrollTop = chatMessages.scrollHeight;
    }
  }

  function appendChatMessage(sender, text) {
    if (!chatMessages) return;

    const msgDiv = document.createElement('div');
    msgDiv.className = `dlt-chat-msg dlt-chat-${sender}`;

    const avatar = document.createElement('div');
    avatar.className = 'dlt-msg-avatar';
    avatar.textContent = sender === 'bot' ? '🚗' : '👤';

    const content = document.createElement('div');
    content.className = 'dlt-msg-content';

    const formatted = escapeHtml(text)
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(.*?)\*/g, '<em>$1</em>')
      .replace(/\n/g, '<br/>');

    content.innerHTML = `<p>${formatted}</p>`;

    msgDiv.appendChild(avatar);
    msgDiv.appendChild(content);
    chatMessages.appendChild(msgDiv);
    scrollChatToBottom();
    return msgDiv;
  }

  function escapeHtml(str) {
    if (!str) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
  }

  async function handleUserChatMessage(userText) {
    if (!userText || !chatInput) return;

    chatInput.value = '';
    appendChatMessage('user', userText);

    if (chatSuggestions) {
      chatSuggestions.style.display = 'none';
    }

    const typingElem = document.createElement('div');
    typingElem.className = 'dlt-chat-msg dlt-chat-bot';
    typingElem.innerHTML = `
      <div class="dlt-msg-avatar">🚗</div>
      <div class="dlt-msg-content">
        <div class="dlt-typing-indicator">
          <div class="dlt-typing-dot"></div>
          <div class="dlt-typing-dot"></div>
          <div class="dlt-typing-dot"></div>
        </div>
      </div>
    `;
    chatMessages.appendChild(typingElem);
    scrollChatToBottom();

    const sid = getOrCreateSessionId();
    try {
      const response = await fetch(n8nWebhookUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          action: 'sendMessage',
          sessionId: sid,
          chatInput: userText
        })
      });

      if (typingElem && typingElem.parentNode) {
        typingElem.parentNode.removeChild(typingElem);
      }

      if (response.ok) {
        const data = await response.json();
        let botReply = '';
        if (typeof data.output === 'string') {
          botReply = data.output;
        } else if (Array.isArray(data) && data[0] && data[0].output) {
          botReply = data[0].output;
        } else if (data.text) {
          botReply = data.text;
        } else if (data.message) {
          botReply = data.message;
        } else {
          botReply = 'Thank you! For instant bookings with our Maruti Suzuki Dzire, please call or WhatsApp us on +91 9493665524.';
        }
        appendChatMessage('bot', botReply);
      } else {
        appendChatMessage('bot', 'Our AI assistant is temporarily busy. You can instantly book or get a fare quote by calling or WhatsApping +91 9493665524!');
      }
    } catch (err) {
      if (typingElem && typingElem.parentNode) {
        typingElem.parentNode.removeChild(typingElem);
      }
      appendChatMessage('bot', 'Network connection interrupted. Please call +91 9493665524 or chat directly on WhatsApp for immediate service!');
    }
  }
});
