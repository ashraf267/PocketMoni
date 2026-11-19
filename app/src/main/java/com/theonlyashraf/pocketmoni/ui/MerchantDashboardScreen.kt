package com.theonlyashraf.pocketmoni.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theonlyashraf.pocketmoni.data.Transaction
import com.theonlyashraf.pocketmoni.ui.theme.PocketMoniTheme
import java.text.NumberFormat
import java.util.Locale

private val BrandBlue = Color(0xFF0F3661)
private val BrandAccentGreen = Color(0xFF10B981)
private val LightBg = Color(0xFFF8FAFC)
private val CardBg = Color(0xFFFFFFFF)

@Composable
fun MerchantDashboardScreen(
    viewModel: MerchantDashboardViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    MerchantDashboardContent(
        uiState = uiState,
        onSimulateTransfer = { amount, sender ->
            viewModel.simulateTransfer(amount, sender)
        },
        onDismissAlert = {
            viewModel.dismissAlert()
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantDashboardContent(
    uiState: MerchantDashboardUiState,
    onSimulateTransfer: (Double, String) -> Unit,
    onDismissAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAmount by remember { mutableDoubleStateOf(5000.0) }
    var senderName by remember { mutableStateOf("Emeka Davies") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "eTranzact PocketMoni",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = uiState.merchantName,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        // Realtime status pill
                        Surface(
                            shape = CircleShape,
                            color = BrandAccentGreen.copy(alpha = 0.2f),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(BrandAccentGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Realtime Active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = BrandAccentGreen
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandBlue
                )
            )
        },
        containerColor = LightBg,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Floating Instant Credit Banner Alert
            AnimatedVisibility(
                visible = uiState.latestCreditAlert != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                uiState.latestCreditAlert?.let { alert ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BrandAccentGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clickable { onDismissAlert() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Alert Sound Played",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "⚡ INSTANT CREDIT ALERT!",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "+${formatNaira(alert.amount)} from ${alert.senderName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Wallet Balance Card
            Card(
                colors = CardDefaults.cardColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Merchant Wallet Balance",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = BrandAccentGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Instant Acoustic Alert Enabled",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandAccentGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = formatNaira(uiState.walletBalance),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Merchant ID: ${uiState.merchantId}",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Simulation Panel
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Simulate Customer Transfer (Recruiter Demo)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = BrandBlue
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset Amount Chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val amounts = listOf(2500.0, 5000.0, 10000.0, 50000.0)
                        amounts.forEach { amt ->
                            val isSelected = selectedAmount == amt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) BrandBlue else LightBg
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) BrandBlue else Color.LightGray,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedAmount = amt }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "₦${amt.toInt()}",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color.DarkGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = senderName,
                        onValueChange = { senderName = it },
                        label = { Text("Sender / Customer Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandBlue,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            onSimulateTransfer(selectedAmount, senderName.ifBlank { "Customer" })
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandAccentGreen),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !uiState.isSimulating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Simulate Transfer & Play Alert 🔔",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recent Credit History
            Text(
                text = "Recent Settlement Alerts",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = BrandBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.transactions, key = { it.id }) { item ->
                    TransactionRow(transaction = item)
                }
            }
        }
    }
}

@Composable
fun TransactionRow(transaction: Transaction) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BrandAccentGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = "Credit",
                    tint = BrandAccentGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.senderName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "${transaction.reference} • ${transaction.timestamp}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = "+${formatNaira(transaction.amount)}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = BrandAccentGreen
            )
        }
    }
}

private fun formatNaira(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("NG").build())
    val result = formatter.format(amount)
    return result.replace("NGN", "₦").replace("NGA", "₦")
}

@Preview(showBackground = true)
@Composable
fun MerchantDashboardPreview() {
    PocketMoniTheme {
        MerchantDashboardContent(
            uiState = MerchantDashboardUiState(),
            onSimulateTransfer = { _, _ -> },
            onDismissAlert = {}
        )
    }
}
