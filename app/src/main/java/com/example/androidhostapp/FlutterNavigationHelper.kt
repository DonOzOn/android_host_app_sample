package com.example.androidhostapp

import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Utility class for launching Flutter module with specific screens
 * Useful for testing and programmatic navigation
 */
object FlutterNavigationHelper {
    
    private const val TAG = "FlutterNavigationHelper"
    
    /**
     * Launch Flutter module with main game screen
     */
    fun openMainGame(context: Context) {
        Log.d(TAG, "Opening main game screen")
        val intent = Intent(context, FlutterActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_SCREEN_NAME, MainActivity.SCREEN_MAIN)
        context.startActivity(intent)
    }
    
    /**
     * Launch Flutter module with about screen
     */
    fun openAboutScreen(context: Context) {
        Log.d(TAG, "Opening about screen")
        val intent = Intent(context, FlutterActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_SCREEN_NAME, MainActivity.SCREEN_ABOUT)
        context.startActivity(intent)
    }
    
    /**
     * Launch Flutter module with game screen directly
     */
    fun openGameScreen(context: Context) {
        Log.d(TAG, "Opening game screen")
        val intent = Intent(context, FlutterActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_SCREEN_NAME, FlutterActivity.SCREEN_GAME)
        context.startActivity(intent)
    }
    
    /**
     * Launch Flutter module with custom screen
     */
    fun openCustomScreen(context: Context, screenName: String) {
        Log.d(TAG, "Opening custom screen: $screenName")
        val intent = Intent(context, FlutterActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_SCREEN_NAME, screenName)
        context.startActivity(intent)
    }
}

/*
 * Usage Examples:
 * 
 * From any Activity or Fragment:
 * 
 * // Open main game
 * FlutterNavigationHelper.openMainGame(this)
 * 
 * // Open about screen
 * FlutterNavigationHelper.openAboutScreen(this)
 * 
 * // Open specific game screen
 * FlutterNavigationHelper.openGameScreen(this)
 * 
 * // Open custom screen
 * FlutterNavigationHelper.openCustomScreen(this, "custom_screen")
 */
