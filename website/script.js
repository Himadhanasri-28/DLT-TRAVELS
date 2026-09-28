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
});
