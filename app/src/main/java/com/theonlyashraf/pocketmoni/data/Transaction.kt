package com.theonlyashraf.pocketmoni.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Data model for incoming fund transfers and credit alerts.
 */
@Serializable
data class Transaction(
    @SerialName("id")
    val id: String = UUID.randomUUID().toString(),

    @SerialName("merchant_id")
    val merchantId: String = "MERCHANT_ETR_2067",

    @SerialName("amount")
    val amount: Double,

    @SerialName("sender_name")
    val senderName: String,

    @SerialName("reference")
    val reference: String = "REF-${UUID.randomUUID().toString().take(8).uppercase()}",

    @SerialName("timestamp")
    val timestamp: String = getCurrentFormattedTime(),

    @SerialName("type")
    val type: String = "CREDIT"
) {
    companion object {
        fun getCurrentFormattedTime(): String {
            val formatter = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
            return formatter.format(Date())
        }
    }
}
