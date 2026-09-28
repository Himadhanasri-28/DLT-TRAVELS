package com.example.data

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class BookingEnquiry(
    val id: String = System.currentTimeMillis().toString(),
    val customerName: String = "",
    val customerPhone: String = "",
    val pickupLocation: String = "",
    val destination: String = "",
    val travelDate: String = "",
    val travelTime: String = "",
    val passengers: String = "4 Passengers (Full Car)",
    val tripType: String = "Outstation Round Trip",
    val additionalNotes: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    fun toFormattedWhatsAppMessage(businessName: String): String {
        val sb = StringBuilder()
        sb.append("🚖 *CAR BOOKING ENQUIRY - ").append(businessName).append("*\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("👤 *Customer:* ").append(customerName.ifBlank { "Not specified" }).append("\n")
        sb.append("📞 *Phone:* ").append(customerPhone.ifBlank { "Not specified" }).append("\n")
        sb.append("📍 *Pickup:* ").append(pickupLocation.ifBlank { "Not specified" }).append("\n")
        sb.append("🏁 *Destination:* ").append(destination.ifBlank { "Not specified" }).append("\n")
        sb.append("📅 *Travel Date:* ").append(travelDate.ifBlank { "As soon as possible" }).append("\n")
        if (travelTime.isNotBlank()) {
            sb.append("⏰ *Pickup Time:* ").append(travelTime).append("\n")
        }
        sb.append("👥 *Passengers:* ").append(passengers).append("\n")
        sb.append("🚗 *Vehicle:* Maruti Suzuki Dzire (Sedan)\n")
        sb.append("🛣️ *Trip Category:* ").append(tripType).append("\n")
        if (additionalNotes.isNotBlank()) {
            sb.append("📝 *Notes:* ").append(additionalNotes).append("\n")
        }
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("_Kindly reply with fare quotation & car availability._")
        return sb.toString()
    }

    fun toUrlEncodedWhatsAppMessage(businessName: String): String {
        return URLEncoder.encode(toFormattedWhatsAppMessage(businessName), StandardCharsets.UTF_8.toString())
    }
}
