package com.theonlyashraf.pocketmoni.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.theonlyashraf.pocketmoni.audio.CreditSoundPlayer
import com.theonlyashraf.pocketmoni.data.Transaction
import com.theonlyashraf.pocketmoni.data.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for the Merchant Dashboard.
 */
data class MerchantDashboardUiState(
    val merchantName: String = "eTranzact Agent Terminal #2067",
    val merchantId: String = "MERCHANT_ETR_2067",
    val walletBalance: Double = 185000.00,
    val isRealtimeConnected: Boolean = true,
    val transactions: List<Transaction> = listOf(
        Transaction(
            amount = 15000.00,
            senderName = "Amina Bello",
            timestamp = "12:45:10 PM"
        ),
        Transaction(
            amount = 5000.00,
            senderName = "Chidi Okafor",
            timestamp = "11:20:05 AM"
        )
    ),
    val latestCreditAlert: Transaction? = null,
    val isSimulating: Boolean = false
)

/**
 * ViewModel orchestrating real-time transactions, wallet balance updates,
 * and immediate acoustic feedback triggers upon credit receipt.
 */
class MerchantDashboardViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = TransactionRepository()
    private val soundPlayer = CreditSoundPlayer(application)

    private val _uiState = MutableStateFlow(MerchantDashboardUiState())
    val uiState: StateFlow<MerchantDashboardUiState> = _uiState.asStateFlow()

    init {
        // Observe Realtime connection state
        viewModelScope.launch {
            repository.isRealtimeConnected.collect { connected ->
                _uiState.update { it.copy(isRealtimeConnected = connected) }
            }
        }

        // Observe incoming real-time credit transactions
        viewModelScope.launch {
            repository.incomingTransactions.collect { newTransaction ->
                // 1. Play immediate low-latency audio alert chime
                soundPlayer.playChime()

                // 2. Update wallet balance & transaction feed
                _uiState.update { currentState ->
                    currentState.copy(
                        walletBalance = currentState.walletBalance + newTransaction.amount,
                        transactions = listOf(newTransaction) + currentState.transactions,
                        latestCreditAlert = newTransaction,
                        isSimulating = false
                    )
                }
            }
        }
    }

    /**
     * Triggered by the merchant UI to simulate an instant transfer.
     */
    fun simulateTransfer(amount: Double, senderName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSimulating = true) }
            repository.simulateCustomerTransfer(amount, senderName)
        }
    }

    /**
     * Dismisses the current floating credit alert banner.
     */
    fun dismissAlert() {
        _uiState.update { it.copy(latestCreditAlert = null) }
    }

    override fun onCleared() {
        super.onCleared()
        soundPlayer.release()
    }
}
