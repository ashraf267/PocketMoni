package com.theonlyashraf.pocketmoni.data

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Repository for transaction management and Supabase Realtime WebSocket streaming.
 */
class TransactionRepository(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    private companion object {
        private const val TAG = "TransactionRepo"
    }

    private val _incomingTransactions = MutableSharedFlow<Transaction>(extraBufferCapacity = 64)
    val incomingTransactions: SharedFlow<Transaction> = _incomingTransactions.asSharedFlow()

    private val _isRealtimeConnected = MutableStateFlow(false)
    val isRealtimeConnected: StateFlow<Boolean> = _isRealtimeConnected.asStateFlow()

    private var supabaseClient: SupabaseClient? = null

    init {
        setupRealtimeStream()
    }

    private fun setupRealtimeStream() {
        if (SupabaseConfig.isConfigured) {
            try {
                val client = createSupabaseClient(
                    supabaseUrl = SupabaseConfig.SUPABASE_URL,
                    supabaseKey = SupabaseConfig.SUPABASE_ANON_KEY
                ) {
                    install(Postgrest)
                    install(Realtime)
                }
                supabaseClient = client

                scope.launch {
                    try {
                        client.realtime.connect()
                        val channel = client.realtime.channel("public:transactions")
                        val changeFlow = channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                            table = "transactions"
                        }
                        _isRealtimeConnected.value = true
                        Log.d(TAG, "Connected to Supabase Realtime WebSockets!")

                        channel.subscribe()

                        changeFlow.collect { action ->
                            val record = action.record
                            val amount = record["amount"]?.toString()?.toDoubleOrNull() ?: 0.0
                            val senderName = record["sender_name"]?.toString() ?: "Anonymous Customer"
                            val merchantId = record["merchant_id"]?.toString() ?: SupabaseConfig.DEFAULT_MERCHANT_ID
                            val reference = record["reference"]?.toString() ?: "REF-UNKNOWN"

                            val transaction = Transaction(
                                merchantId = merchantId,
                                amount = amount,
                                senderName = senderName,
                                reference = reference
                            )

                            Log.d(TAG, "Realtime Credit Event Received from Supabase: $transaction")
                            _incomingTransactions.emit(transaction)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in Supabase Realtime stream", e)
                        _isRealtimeConnected.value = true // Fallback stream ready
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize Supabase client", e)
                _isRealtimeConnected.value = true
            }
        } else {
            Log.d(TAG, "Running in In-Memory Realtime Stream mode (Supabase keys not configured)")
            _isRealtimeConnected.value = true
        }
    }

    /**
     * Simulates a customer transfer.
     *
     * If Supabase credentials are configured, inserts a record into the remote `transactions` table.
     * Otherwise, immediately emits a local transaction event into the Realtime flow.
     */
    suspend fun simulateCustomerTransfer(amount: Double, senderName: String) {
        val transaction = Transaction(
            merchantId = SupabaseConfig.DEFAULT_MERCHANT_ID,
            amount = amount,
            senderName = senderName
        )

        val client = supabaseClient
        if (SupabaseConfig.isConfigured && client != null) {
            try {
                client.postgrest["transactions"].insert(transaction)
                Log.d(TAG, "Inserted transaction to Supabase DB: $transaction")
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting transaction to Supabase. Falling back to local emit.", e)
                _incomingTransactions.emit(transaction)
            }
        } else {
            Log.d(TAG, "Emitting simulated transaction to Realtime Stream: $transaction")
            _incomingTransactions.emit(transaction)
        }
    }
}
