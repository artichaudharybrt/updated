package com.gamegards.gaming27

import android.app.Application
import com.reown.android.Core
import com.reown.android.CoreClient
import com.reown.appkit.client.AppKit
import com.reown.appkit.client.Modal
import com.reown.appkit.presets.AppKitChainsPresets

class WalletApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeWalletConnect()
    }

    private fun initializeWalletConnect() {
        try {
            // Your Project ID from https://cloud.reown.com/
            val projectId = "7b44092789f3f1fd094a8bbec200372b"

            val appMetaData = Core.Model.AppMetaData(
                name = "Gaming27",
                description = "Real wallet connection with multiple wallet options for Gaming27 app",
                url = "https://gamegards.com",
                icons = listOf("https://gamegards.com/logo.png"),
                redirect = "gaming27://request"
            )

            // Initialize Core Client
            CoreClient.initialize(
                application = this,
                projectId = projectId,
                metaData = appMetaData,
                onError = { error ->
                    println("CoreClient initialization error: ${error.throwable?.message}")
                }
            )

            // Initialize AppKit
            AppKit.initialize(
                init = Modal.Params.Init(CoreClient),
                onSuccess = {
                    // Set supported chains (Ethereum, Polygon, etc.)
                    val supportedChains = listOf(
                        AppKitChainsPresets.ethChains["1"],      // Ethereum Mainnet
                        AppKitChainsPresets.ethChains["137"],    // Polygon
                        AppKitChainsPresets.ethChains["11155111"], // Sepolia Testnet
                    ).filterNotNull()

                    AppKit.setChains(supportedChains)
                    println("AppKit initialized successfully with ${supportedChains.size} chains")
                },
                onError = { error ->
                    println("AppKit initialization error: ${error.throwable?.message}")
                }
            )

        } catch (e: Exception) {
            println("Error initializing WalletConnect: ${e.message}")
        }
    }
}
