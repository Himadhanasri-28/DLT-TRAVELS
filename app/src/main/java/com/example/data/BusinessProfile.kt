package com.example.data

import android.content.Context
import android.content.SharedPreferences

data class BusinessProfile(
    val businessName: String = "DLT TRAVELS",
    val phoneNumber: String = "9493665524",
    val whatsappNumber: String = "919493665524",
    val serviceArea: String = "Visakhapatnam",
    val address: String = "Visakhapatnam, Andhra Pradesh",
    val carModel: String = "Maruti Suzuki Dzire",
    val baseRatePerKm: String = "₹12 / km",
    val minOutstationKm: String = "250 km / day"
) {
    fun saveToPrefs(context: Context) {
        val prefs = context.getSharedPreferences("business_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("businessName", businessName)
            putString("phoneNumber", phoneNumber)
            putString("whatsappNumber", whatsappNumber)
            putString("serviceArea", serviceArea)
            putString("address", address)
            putString("carModel", carModel)
            putString("baseRatePerKm", baseRatePerKm)
            putString("minOutstationKm", minOutstationKm)
            apply()
        }
    }

    companion object {
        fun loadFromPrefs(context: Context): BusinessProfile {
            val prefs = context.getSharedPreferences("business_prefs", Context.MODE_PRIVATE)
            return BusinessProfile(
                businessName = prefs.getString("businessName", "DLT TRAVELS") ?: "DLT TRAVELS",
                phoneNumber = prefs.getString("phoneNumber", "9493665524") ?: "9493665524",
                whatsappNumber = prefs.getString("whatsappNumber", "919493665524") ?: "919493665524",
                serviceArea = prefs.getString("serviceArea", "Visakhapatnam") ?: "Visakhapatnam",
                address = prefs.getString("address", "Visakhapatnam, Andhra Pradesh") ?: "Visakhapatnam, Andhra Pradesh",
                carModel = prefs.getString("carModel", "Maruti Suzuki Dzire") ?: "Maruti Suzuki Dzire",
                baseRatePerKm = prefs.getString("baseRatePerKm", "₹12 / km") ?: "₹12 / km",
                minOutstationKm = prefs.getString("minOutstationKm", "250 km / day") ?: "250 km / day"
            )
        }
    }
}
