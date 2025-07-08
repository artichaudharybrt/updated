package com.gamegards.gaming27.wallet

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gamegards.gaming27.Activity.Homepage
import com.gamegards.gaming27.account.LoginScreen
import com.gamegards.gaming27.account.Register_Activity
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.gamegards.gaming27.R
import com.reown.appkit.client.AppKit
import com.reown.appkit.ui.appKitGraph
import com.reown.appkit.ui.openAppKit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WalletConnectActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                val modalSheetState = rememberModalBottomSheetState(
                    initialValue = ModalBottomSheetValue.Hidden,
                    skipHalfExpanded = true
                )
                val bottomSheetNavigator = BottomSheetNavigator(modalSheetState)
                val navController = rememberNavController(bottomSheetNavigator)

                ModalBottomSheetLayout(bottomSheetNavigator = bottomSheetNavigator) {
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            // Use same background image as other pages
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                // Background image matching your app
                                Image(
                                    painter = painterResource(id = R.drawable.home_bg2),
                                    contentDescription = "Background",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.FillHeight
                                )

                                // Content on top of background
                                WalletConnectApp(navController = navController)
                            }
                        }
                        // Add AppKit navigation graph
                        appKitGraph(navController)
                    }
                }
            }
        }
    }
}

@Composable
fun WalletConnectApp(navController: androidx.navigation.NavController) {
    val context = LocalContext.current

    // Simple state management - will be updated when real connection happens
    var isConnected by remember { mutableStateOf(false) }
    var walletAddress by remember { mutableStateOf<String?>(null) }
    var chainId by remember { mutableStateOf<String?>(null) }

    // Check wallet connection status and only navigate if connected
    LaunchedEffect(Unit) {
        // Check if wallet is already connected
        val currentAccount = AppKit.getAccount()
        val isWalletConnected = currentAccount != null

        android.util.Log.d("WalletConnect", "Wallet connection check - isConnected: $isWalletConnected")
        if (isWalletConnected) {
            android.util.Log.d("WalletConnect", "Wallet connected - Address: ${currentAccount?.address}")
            isConnected = true
            walletAddress = currentAccount?.address

            // Only navigate if wallet is connected
            delay(2000)

            // Use the same logic as splash screen for navigation
            val prefs = context.getSharedPreferences("Login_data", Context.MODE_PRIVATE)
            val userId = prefs.getString("user_id", "")

            android.util.Log.d("WalletConnect", "Wallet connected, checking login status - userId: '$userId'")

            val intent = if (!userId.isNullOrEmpty() && userId.trim().isNotEmpty()) {
                android.util.Log.d("WalletConnect", "User is logged in, navigating to Homepage")
                Intent(context, Homepage::class.java)
            } else {
                android.util.Log.d("WalletConnect", "User not logged in, navigating to Register")
                Intent(context, LoginScreen::class.java)
            }

            context.startActivity(intent)
            (context as ComponentActivity).finish()
        } else {
            android.util.Log.d("WalletConnect", "No wallet connected - staying on wallet screen")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Gaming27 Header with gold theme
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            backgroundColor = Color(0xFF2A2A3E),
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Gaming27 Logo/Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD700), // Gold
                                    Color(0xFFB8860B)  // Dark gold
                                )
                            ),
                            shape = RoundedCornerShape(40.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Wallet Icon",
                        modifier = Modifier.size(40.dp),
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "BRT MULTI GAMING",
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFFFD700) // Gold color
                )

                Text(
                    text = "Web3 Wallet Connection",
                    style = MaterialTheme.typography.h6,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Connect your wallet to access premium gaming features",
                    style = MaterialTheme.typography.body2,
                    color = Color(0xFFB0B0B0),
                    textAlign = TextAlign.Center
                )

                // Wallet Connection Status
                if (isConnected && walletAddress != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✅ Wallet Connected",
                        style = MaterialTheme.typography.body2,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Address: ${walletAddress?.take(6)}...${walletAddress?.takeLast(4)}",
                        style = MaterialTheme.typography.caption,
                        color = Color(0xFFB0B0B0),
                        textAlign = TextAlign.Center
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "❌ No Wallet Connected",
                        style = MaterialTheme.typography.body2,
                        color = Color(0xFFFF5722),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (!isConnected) {
            // Connection Status
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        contentDescription = "Status",
                        tint = Color.Red,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Not Connected")
                }
            }

            // Gaming27 Styled Connect Button
            Button(
                onClick = {
                    try {
                        // This opens the REAL Reown AppKit modal with all wallets
                        navController.openAppKit(
                            shouldOpenChooseNetwork = false,
                            onError = { error ->
                                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                            }
                        )
                        Toast.makeText(context, "Opening wallet selection...", Toast.LENGTH_SHORT).show()

                        // Check connection status after a short delay
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(3000) // Wait for potential connection
                            val account = AppKit.getAccount()
                            if (account != null) {
                                isConnected = true
                                walletAddress = account.address
                                Toast.makeText(context, "Wallet connected successfully!", Toast.LENGTH_SHORT).show()

                                // Navigate after successful connection
                                delay(1500)
                                val prefs = context.getSharedPreferences("Login_data", Context.MODE_PRIVATE)
                                val userId = prefs.getString("user_id", "")

                                val intent = if (!userId.isNullOrEmpty() && userId.trim().isNotEmpty()) {
                                    Intent(context, Homepage::class.java)
                                } else {
                                    Intent(context, Register_Activity::class.java)
                                }

                                context.startActivity(intent)
                                (context as ComponentActivity).finish()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error opening wallet: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(30.dp)),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFFD700), // Gold
                                    Color(0xFFFFA500), // Orange gold
                                    Color(0xFFFFD700)  // Gold
                                )
                            ),
                            shape = RoundedCornerShape(30.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Connect",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isConnected) "WALLET CONNECTED" else "CONNECT WALLET",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

        } else {
            // Connected State
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                backgroundColor = Color.Green.copy(alpha = 0.1f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Connected",
                            tint = Color.Green,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Connected",
                            fontWeight = FontWeight.Bold,
                            color = Color.Green
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    walletAddress?.let { address ->
                        Text(
                            text = "Address: ${address.substring(0, 6)}...${address.substring(address.length - 4)}",
                            style = MaterialTheme.typography.body2
                        )
                    }

                    chainId?.let { chain ->
                        Text(
                            text = "Chain ID: $chain",
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }

            // Real AppKit Disconnect Button
            OutlinedButton(
                onClick = {
                    // Use real AppKit disconnect
                    AppKit.disconnect(
                        onSuccess = {
                            Toast.makeText(context, "Disconnected successfully", Toast.LENGTH_SHORT).show()
                        },
                        onError = { error ->
                            Toast.makeText(context, "Error disconnecting: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LinkOff,
                    contentDescription = "Disconnect"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Disconnect Wallet")
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
