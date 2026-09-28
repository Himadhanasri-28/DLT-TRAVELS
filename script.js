/**
 * DLT TRAVELS - Maruti Suzuki Dzire Travels & Taxi Service (Visakhapatnam)
 * Interactive functionality, form handling, and WhatsApp enquiry integration
 */

document.addEventListener('DOMContentLoaded', () => {
  // 1. Set current year in footer
  const yearSpan = document.getElementById('current-year');
  if (yearSpan) {
    yearSpan.textContent = new Date().getFullYear();
  }

  // 2. Set minimum date for travel date picker to today
  const travelDateInput = document.getElementById('travelDate');
  if (travelDateInput) {
    const today = new Date().toISOString().split('T')[0];
    travelDateInput.min = today;
    if (!travelDateInput.value) {
      travelDateInput.value = today;
    }
  }

  // 3. Mobile Navigation Menu Toggle
  const mobileToggle = document.getElementById('mobile-toggle');
  const navMenu = document.getElementById('nav-menu');
  const navLinks = document.querySelectorAll('.nav-link');

  if (mobileToggle && navMenu) {
    mobileToggle.addEventListener('click', () => {
      navMenu.classList.toggle('open');
      const spans = mobileToggle.querySelectorAll('span');
      if (navMenu.classList.contains('open')) {
        spans[0].style.transform = 'rotate(45deg) translate(5px, 5px)';
        spans[1].style.opacity = '0';
        spans[2].style.transform = 'rotate(-45deg) translate(5px, -5px)';
      } else {
        spans[0].style.transform = 'none';
        spans[1].style.opacity = '1';
        spans[2].style.transform = 'none';
      }
    });

    // Close menu when any nav link is tapped
    navLinks.forEach(link => {
      link.addEventListener('click', () => {
        navMenu.classList.remove('open');
        const spans = mobileToggle.querySelectorAll('span');
        spans[0].style.transform = 'none';
        spans[1].style.opacity = '1';
        spans[2].style.transform = 'none';
      });
    });
  }

  // 4. Smooth scroll & Active link highlight on scroll
  const sections = document.querySelectorAll('section[id]');
  window.addEventListener('scroll', () => {
    const scrollY = window.pageYOffset;
    sections.forEach(section => {
      const sectionHeight = section.offsetHeight;
      const sectionTop = section.offsetTop - 100;
      const sectionId = section.getAttribute('id');
      const correspondingLink = document.querySelector(`.nav-link[href*="${sectionId}"]`);

      if (correspondingLink) {
        if (scrollY > sectionTop && scrollY <= sectionTop + sectionHeight) {
          correspondingLink.classList.add('active');
        } else {
          correspondingLink.classList.remove('active');
        }
      }
    });
  });

  // 5. Pre-fill Trip Type when clicking "Book" buttons on service cards
  const serviceButtons = document.querySelectorAll('.service-btn');
  const tripTypeSelect = document.getElementById('tripType');

  serviceButtons.forEach(btn => {
    btn.addEventListener('click', () => {
      const tripCategory = btn.getAttribute('data-trip');
      if (tripCategory && tripTypeSelect) {
        for (let i = 0; i < tripTypeSelect.options.length; i++) {
          if (tripTypeSelect.options[i].text.includes(tripCategory) || 
              tripTypeSelect.options[i].value.includes(tripCategory)) {
            tripTypeSelect.selectedIndex = i;
            break;
          }
        }
      }
    });
  });

  // 6. Interactive Car Feature Thumbnails
  const thumbs = document.querySelectorAll('.car-thumb');
  const mainPhotoArt = document.querySelector('.photo-art');
  const featureDescriptions = {
    'Dual Tone Interior': 'Plush beige and black dual-tone interior with premium seat cushioning and rear center armrest.',
    '378L Boot Space': 'Deep 378-liter boot capacity accommodates up to 4 airport trolley bags with ease.',
    'Rear AC Vents': 'Dedicated rear air-conditioning vents ensure fast, uniform cooling for back-seat passengers.',
    'Mobile Charging': 'Front and rear USB power sockets to keep your smartphones and devices charged throughout the journey.'
  };

  thumbs.forEach(thumb => {
    thumb.addEventListener('click', () => {
      thumbs.forEach(t => t.classList.remove('active'));
      thumb.classList.add('active');
      const featureTitle = thumb.querySelector('small').textContent.trim();
      if (mainPhotoArt && featureDescriptions[featureTitle]) {
        mainPhotoArt.querySelector('h4').textContent = featureTitle;
        mainPhotoArt.querySelector('p').textContent = featureDescriptions[featureTitle];
      }
    });
  });

  // 7. Booking Form Submission -> Formats WhatsApp Message & Launches WhatsApp
  const bookingForm = document.getElementById('booking-form');
  const feedbackBox = document.getElementById('booking-feedback');
  const feedbackLink = document.getElementById('feedback-whatsapp-link');
  const callDirectBtn = document.getElementById('call-direct-btn');

  const defaultWhatsAppNumber = '919493665524';
  const defaultPhoneNumber = '9493665524';

  if (bookingForm) {
    bookingForm.addEventListener('submit', (e) => {
      e.preventDefault();

      // Retrieve form values
      const name = document.getElementById('customerName').value.trim();
      const phone = document.getElementById('customerPhone').value.trim();
      const pickup = document.getElementById('pickupLocation').value.trim();
      const destination = document.getElementById('destinationLocation').value.trim();
      const date = document.getElementById('travelDate').value;
      const time = document.getElementById('travelTime').value || 'Not specified';
      const passengers = document.getElementById('passengers').value;
      const tripType = document.getElementById('tripType').value;
      const message = document.getElementById('additionalMessage').value.trim();

      // Basic validation
      if (!name || !phone || !pickup || !destination || !date) {
        alert('Please fill in all required fields (marked with *).');
        return;
      }

      // Format clean, professional WhatsApp enquiry message for DLT Travels
      const formattedMessage = 
`🚖 *NEW CAR BOOKING ENQUIRY - DLT TRAVELS*
━━━━━━━━━━━━━━━━━━━━
👤 *Customer:* ${name}
📞 *Contact:* ${phone}
📍 *Pickup:* ${pickup}
🏁 *Destination:* ${destination}
📅 *Date:* ${date}
⏰ *Time:* ${time}
👥 *Passengers:* ${passengers}
🚗 *Car:* Maruti Suzuki Dzire (Sedan)
🛣️ *Trip Type:* ${tripType}
${message ? `📝 *Notes:* ${message}\n` : ''}━━━━━━━━━━━━━━━━━━━━
_Please confirm availability and rate quote for this trip._`;

      const encodedMessage = encodeURIComponent(formattedMessage);
      const whatsappUrl = `https://wa.me/${defaultWhatsAppNumber}?text=${encodedMessage}`;

      // Forward booking details to n8n webhook
      try {
        const activeSessionId = getOrCreateSessionId();
        fetch('https://himadhanasri.app.n8n.cloud/webhook/248c973a-3532-45d7-b22f-c598eaa7d103/chat', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            action: 'sendMessage',
            sessionId: activeSessionId,
            chatInput: `New Cab Booking Enquiry: Customer ${name} (${phone}) requested ${tripType} from "${pickup}" to "${destination}" on ${date} at ${time}. Passengers: ${passengers}. Notes: ${message || 'None'}. Please confirm vehicle availability and quote.`,
            customerName: name,
            customerPhone: phone,
            pickupLocation: pickup,
            destinationLocation: destination,
            travelDate: date,
            travelTime: time,
            passengers: passengers,
            tripType: tripType,
            additionalMessage: message,
            timestamp: new Date().toISOString()
          })
        }).catch(function() {});
      } catch (err) {}

      // Show feedback box
      if (feedbackBox && feedbackLink) {
        feedbackLink.href = whatsappUrl;
        feedbackBox.classList.remove('hidden');
      }

      // Open WhatsApp directly
      window.open(whatsappUrl, '_blank');
    });
  }

  // 8. Direct Call Button from form
  if (callDirectBtn) {
    callDirectBtn.addEventListener('click', () => {
      window.location.href = `tel:${defaultPhoneNumber}`;
    });
  }

  // ==========================================================================
  // 9. DLT Travels n8n AI Chat Assistant
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
    // Open/Close Chat Window
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

    // Handle suggestion chips
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

    // Handle form submit
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

    // Format line breaks and bold markers
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
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
  }

  async function handleUserChatMessage(userText) {
    if (!userText || !chatInput) return;

    // 1. Clear input & append user message
    chatInput.value = '';
    appendChatMessage('user', userText);

    // 2. Hide suggestions after first prompt
    if (chatSuggestions) {
      chatSuggestions.style.display = 'none';
    }

    // 3. Show typing indicator
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

    // 4. Send request to n8n webhook
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

      // Remove typing indicator
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
          botReply = 'Thank you for reaching out! For instant cab booking with our Maruti Suzuki Dzire, please call or WhatsApp us on +91 9493665524.';
        }
        appendChatMessage('bot', botReply);
      } else {
        appendChatMessage('bot', 'Our AI assistant is temporarily busy. You can instantly book or get a fare quote by calling or messaging +91 9493665524 on WhatsApp!');
      }
    } catch (err) {
      if (typingElem && typingElem.parentNode) {
        typingElem.parentNode.removeChild(typingElem);
      }
      appendChatMessage('bot', 'Network connection interrupted. Please call +91 9493665524 or chat with us directly on WhatsApp for immediate service!');
    }
  }
});
