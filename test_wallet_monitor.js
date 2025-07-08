// Test script for WalletMonitor
const { WalletMonitor, WalletGenerator } = require('./own_pay.js');

// Mock wallet addresses and private keys
// const usdtReceiveWallet = '0x2764C0e40F14602E7843Ed43447c0C41D954f6d0';
// const gasWallet = '0x2eC44098cfb24694F34De19F08d1079d197D6903';
// const gasPrivateKey = '0094d475f0b240b06efc9e555c173f53cf5719d5ec127225f0d9cd3e24463070';

// Test WalletGenerator
console.log("=== Testing WalletGenerator ===");
try {
    const generator = new WalletGenerator();
    const wallet = generator.generateWallet();
    console.log("Generated wallet:", wallet.address);
    console.log("Private key:", wallet.privateKey.substring(0, 6) + "..." + wallet.privateKey.substring(wallet.privateKey.length - 4));
    console.log("WalletGenerator test passed!");
} catch (error) {
    console.error("Error in WalletGenerator test:", error.message);
}

// Test WalletMonitor constructor
console.log("\n=== Testing WalletMonitor Constructor ===");
let monitor;
try {
    console.log("Creating WalletMonitor instance...");
    monitor = new WalletMonitor(usdtReceiveWallet, gasWallet, gasPrivateKey);
    console.log("WalletMonitor constructor test passed!");
} catch (error) {
    console.error("Error in WalletMonitor constructor test:", error.message);
    process.exit(1);
}

// Test monitorAndTransfer method with a test wallet
async function testMonitorAndTransfer() {
    console.log("\n=== Testing monitorAndTransfer Method ===");
    try {
        // Generate a test wallet
        const generator = new WalletGenerator();
        const testWallet = generator.generateWallet();
        console.log("Test wallet address:", testWallet.address);
        
        // Call monitorAndTransfer
        console.log("Calling monitorAndTransfer...");
        const result = await monitor.monitorAndTransfer(testWallet);
        
        console.log("monitorAndTransfer result:", result);
        console.log("monitorAndTransfer test completed!");
    } catch (error) {
        console.error("Error in monitorAndTransfer test:", error.message);
    }
}

// Run the tests
testMonitorAndTransfer().then(() => {
    console.log("\n=== All tests completed ===");
}).catch(error => {
    console.error("Error running tests:", error.message);
});
