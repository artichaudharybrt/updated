package com.gamegards.bigjackpot.wallet

import android.content.Context
import android.content.Intent
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
import com.gamegards.bigjackpot.Activity.Homepage
import com.gamegards.bigjackpot.account.Register_Activity
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.gamegards.bigjackpot.R
import com.gamegards.bigjackpot.ApiClasses.Const
import com.gamegards.bigjackpot.Interface.ApiRequest
import com.gamegards.bigjackpot.Interface.Callback
import com.gamegards.bigjackpot.WalletApplication
import org.json.JSONObject
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

    // Check wallet connection status and call login API if connected
    LaunchedEffect(Unit) {
        // Check if wallet is already connected
        val currentAccount = AppKit.getAccount()
        val isWalletConnected = currentAccount != null

        android.util.Log.d("WalletConnect", "Wallet connection check - isConnected: $isWalletConnected")
        if (isWalletConnected) {
            android.util.Log.d("WalletConnect", "Wallet connected - Address: ${currentAccount?.address}")
            isConnected = true
            walletAddress = currentAccount?.address

            // Set global wallet address
            WalletApplication.setWalletAddress(currentAccount?.address)

            // Call login_withpassword API with only wallet address
            currentAccount?.address?.let { address ->
                callLoginWithWalletAddress(address, context)
            }
        } else {
            android.util.Log.d("WalletConnect", "No wallet connected - staying on wallet screen")
            // Clear global wallet address when not connected
            WalletApplication.setWalletAddress(null)
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

                                // Set global wallet address
                                WalletApplication.setWalletAddress(account.address)

                                // Call login API with wallet address
                                account.address?.let { address ->
                                    callLoginWithWalletAddress(address, context)
                                }
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
                            // Clear global wallet address on disconnect
                            WalletApplication.setWalletAddress(null)
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

// Function to call login_withpassword API with only wallet address
private fun callLoginWithWalletAddress(walletAddress: String, context: Context) {
    android.util.Log.d("WalletConnect", "Calling login API with wallet address: $walletAddress")

    val params = HashMap<String, String>()
    params["wallet_address"] = walletAddress
    params["type"] = "LOGIN"

    ApiRequest.Call_Api(context, Const.login_withpassword, params, object : Callback {
        override fun Responce(response: String?, type: String?, bundle: Bundle?) {
            android.util.Log.d("WalletConnect", "=== WALLET LOGIN API RESPONSE START ===")
            android.util.Log.d("WalletConnect", "Full Response: $response")
            android.util.Log.d("WalletConnect", "API Endpoint: ${Const.login_withpassword}")
            android.util.Log.d("WalletConnect", "Params sent: $params")
            android.util.Log.d("WalletConnect", "=== WALLET LOGIN API RESPONSE END ===")

            if (response != null) {
                try {
                    val jsonObject = JSONObject(response)
                    val code = jsonObject.getString("code")
                    android.util.Log.d("WalletConnect", "Response Code: $code")

                    when (code) {
                        "201" -> {
                            // Login successful - user exists (same as existing login)
                            android.util.Log.d("WalletConnect", "Login successful - storing user data")

                            val token = jsonObject.getString("token")

                            if (jsonObject.has("user")) {
                                val jsonObject1 = jsonObject.getJSONArray("user").getJSONObject(0)
                                val id = jsonObject1.getString("id")
                                val name = jsonObject1.getString("name")
                                val mobile = jsonObject1.getString("mobile")

                                // Store user data in SharedPreferences (exactly like existing login)
                                val prefs = context.getSharedPreferences("Login_data", Context.MODE_PRIVATE)
                                val editor = prefs.edit()
                                editor.putString("user_id", id)
                                editor.putString("name", name)
                                editor.putString("mobile", mobile)
                                editor.putString("token", token)
                                editor.apply()

                                android.util.Log.d("WalletConnect", "User data stored - navigating to Homepage")

                                val intent = Intent(context, Homepage::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                                (context as ComponentActivity).finish()
                            } else {
                                android.util.Log.e("WalletConnect", "Login successful but no user data found")
                                val intent = Intent(context, Register_Activity::class.java)
                                context.startActivity(intent)
                                (context as ComponentActivity).finish()
                            }
                        }
                        "200" -> {
                            // Registration successful - new user (exactly like loginwithPassword)
                            android.util.Log.d("WalletConnect", "Registration successful - storing user data")

                            val userDataArray = jsonObject.optJSONArray("user_data")
                            if (userDataArray != null && userDataArray.length() > 0) {
                                val userDataObject = userDataArray.getJSONObject(0)
                                val token = userDataObject.getString("token")
                                val user_id = userDataObject.getString("id")

                                // Store user data in SharedPreferences (exactly like loginwithPassword)
                                val prefs = context.getSharedPreferences("Login_data", Context.MODE_PRIVATE)
                                val editor = prefs.edit()
                                editor.putString("user_id", user_id)
                                editor.putString("token", token)
                                editor.apply()

                                android.util.Log.d("WalletConnect", "User data stored - navigating to Homepage")

                                val intent = Intent(context, Homepage::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                                (context as ComponentActivity).finish()
                            } else {
                                android.util.Log.e("WalletConnect", "Registration successful but no user_data found")
                                val intent = Intent(context, Register_Activity::class.java)
                                context.startActivity(intent)
                                (context as ComponentActivity).finish()
                            }
                        }
                        else -> {
                            // Login failed - go to registration
                            android.util.Log.d("WalletConnect", "Login failed - navigating to Register")
                            val intent = Intent(context, Register_Activity::class.java)
                            context.startActivity(intent)
                            (context as ComponentActivity).finish()
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("WalletConnect", "Error parsing login response: ${e.message}")
                    // On error, go to registration
                    val intent = Intent(context, Register_Activity::class.java)
                    context.startActivity(intent)
                    (context as ComponentActivity).finish()
                }
            } else {
                android.util.Log.e("WalletConnect", "Login API returned null response")
                // On null response, go to registration
                val intent = Intent(context, Register_Activity::class.java)
                context.startActivity(intent)
                (context as ComponentActivity).finish()
            }
        }
    })
}
