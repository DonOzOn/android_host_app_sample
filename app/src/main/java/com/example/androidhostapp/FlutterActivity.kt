package com.example.androidhostapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class FlutterActivity : AppCompatActivity() {
    
    companion object {
        private const val ENGINE_ID = "flutter_engine_id"
        private const val CHANNEL = "flutter_module_example/navigation"
        private const val TAG = "FlutterActivity"
        
        // Screen name constants (match Flutter side)
        const val SCREEN_MAIN = "main"
        const val SCREEN_ABOUT = "about"
        const val SCREEN_GAME = "game"
    }
    
    private var methodChannel: MethodChannel? = null
    private var currentScreenName: String = SCREEN_MAIN
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flutter)
        
        // Get the screen name from intent
        currentScreenName = intent.getStringExtra(MainActivity.EXTRA_SCREEN_NAME) ?: SCREEN_MAIN
        Log.d(TAG, "Opening Flutter module with screen: $currentScreenName")
        
        // Check if we already have a cached engine
        var flutterEngine = FlutterEngineCache.getInstance().get(ENGINE_ID)
        
        if (flutterEngine == null) {
            // Create a new FlutterEngine
            flutterEngine = FlutterEngine(this)
            
            // Start executing Dart code
            flutterEngine.dartExecutor.executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
            )
            
            // Cache the FlutterEngine to reuse
            FlutterEngineCache.getInstance().put(ENGINE_ID, flutterEngine)
        }
        
        // Set up method channel
        setupMethodChannel(flutterEngine)
        
        // Create FlutterFragment with cached engine
        val flutterFragment = FlutterFragment.withCachedEngine(ENGINE_ID).build<FlutterFragment>()
        
        // Add FlutterFragment to the activity
        supportFragmentManager
            .beginTransaction()
            .add(R.id.flutter_container, flutterFragment)
            .commit()
//        supportFragmentManager.executePendingTransactions()
        navigateToScreen(currentScreenName)
    }
    
    private fun setupMethodChannel(flutterEngine: FlutterEngine) {
        methodChannel = MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        )
        
        methodChannel?.setMethodCallHandler { call, result ->
            when (call.method) {
                "onBackNavigation" -> {
                    handleBackNavigation(call, result)
                }
                "onScreenChanged" -> {
                    handleScreenChanged(call, result)
                }
                "onCustomEvent" -> {
                    handleCustomEvent(call, result)
                }
                else -> {
                    result.notImplemented()
                }
            }
        }
    }
    
    private fun navigateToScreen(screenName: String) {
        if (methodChannel == null) {
            Log.w(TAG, "Method channel not initialized, retrying in 200ms...")
            // Retry after a short delay if method channel is not ready
            navigateToScreen(screenName)
            return
        }
        
        Log.d(TAG, "Attempting to navigate to screen: $screenName")
        
        val arguments = mapOf(
            "screenName" to screenName,
            "params" to mapOf(
                "timestamp" to System.currentTimeMillis(),
                "source" to "android_host_app",
                "from" to ""
            )
        )
        
        methodChannel?.invokeMethod("navigateToScreen", arguments, object : MethodChannel.Result {
            override fun success(result: Any?) {
                Log.d(TAG, "✅ Navigation to $screenName successful: $result")
                currentScreenName = screenName
                showToast("Opened $screenName screen")
            }
            
            override fun error(errorCode: String, errorMessage: String?, errorDetails: Any?) {
                Log.e(TAG, "❌ Navigation to $screenName error: $errorCode - $errorMessage")
                Log.e(TAG, "Error details: $errorDetails")
                showToast("Failed to navigate to $screenName: $errorMessage")
            }
            
            override fun notImplemented() {
                Log.w(TAG, "⚠️ Navigation method not implemented in Flutter")
                showToast("Navigation not supported by Flutter module")
            }
        })
    }
    
    private fun handleBackNavigation(call: MethodCall, result: MethodChannel.Result) {
        val fromScreen = call.argument<String>("fromScreen")
        val data = call.argument<Map<String, Any>>("data")
        
        Log.d(TAG, "Back navigation from screen: $fromScreen")
        
        when (fromScreen) {
            SCREEN_MAIN -> {
                // User pressed back on main screen - close Flutter module
                Log.d(TAG, "Closing Flutter module - back pressed on main screen")
                showToast("Returning to Android app")
                finish()
            }
            SCREEN_ABOUT -> {
                // User pressed back on about screen - this is handled internally by Flutter
                // But we can track it for analytics or other purposes
                Log.d(TAG, "Back pressed on about screen - handled by Flutter")
                finish() // Assume we're going back to main
            }
            else -> {
                Log.d(TAG, "Back pressed on unknown screen: $fromScreen")
            }
        }
        
        result.success(null)
    }
    
    private fun handleScreenChanged(call: MethodCall, result: MethodChannel.Result) {
        val screenName = call.argument<String>("screenName")
        val data = call.argument<Map<String, Any>>("data")
        
        Log.d(TAG, "Screen changed to: $screenName")
        currentScreenName = screenName ?: SCREEN_MAIN
        
        // You can track analytics or update UI based on screen changes
        when (screenName) {
            SCREEN_MAIN -> {
                // Track main screen view
                Log.d(TAG, "User is now on main game screen")
            }
            SCREEN_ABOUT -> {
                // Track about screen view
                Log.d(TAG, "User is now on about screen")
            }
        }
        
        result.success(null)
    }
    
    private fun handleCustomEvent(call: MethodCall, result: MethodChannel.Result) {
        val eventName = call.argument<String>("eventName")
        val data = call.argument<Map<String, Any>>("data")
        
        Log.d(TAG, "Custom event: $eventName with data: $data")
        
        // Handle custom events like game scores, achievements, etc.
        when (eventName) {
            "game_score_updated" -> {
                val score = data?.get("score") as? Int
                val level = data?.get("level") as? Int
                Log.d(TAG, "Game score updated: $score, level: $level")
                // You could show a notification, update a leaderboard, etc.
            }
            "game_over" -> {
                val finalScore = data?.get("finalScore") as? Int
                Log.d(TAG, "Game over with final score: $finalScore")
                showToast("Game Over! Score: $finalScore")
            }
            "achievement_unlocked" -> {
                val achievement = data?.get("achievement") as? String
                Log.d(TAG, "Achievement unlocked: $achievement")
                showToast("Achievement: $achievement")
            }
        }
        
        result.success(null)
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Let Flutter handle back navigation first
        // If Flutter doesn't handle it, it will send us a back navigation event
        Log.d(TAG, "Android back button pressed on screen: $currentScreenName")
        
        // For main screen, we close the activity directly
        if (currentScreenName == SCREEN_MAIN) {
            super.onBackPressed()
            finish()
        } else {
            // For other screens, let Flutter handle it
            // Flutter will send us a back navigation event if needed
            super.onBackPressed()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        methodChannel?.setMethodCallHandler(null)
        Log.d(TAG, "FlutterActivity destroyed")
    }
}
