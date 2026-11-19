package com.theonlyashraf.pocketmoni

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.theonlyashraf.pocketmoni.ui.MerchantDashboardScreen
import com.theonlyashraf.pocketmoni.ui.MerchantDashboardViewModel
import com.theonlyashraf.pocketmoni.ui.theme.PocketMoniTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PocketMoniTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: MerchantDashboardViewModel = viewModel()
                    MerchantDashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}
