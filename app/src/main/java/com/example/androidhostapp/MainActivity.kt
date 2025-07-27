package com.example.androidhostapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
class MainActivity : AppCompatActivity() {
    
    companion object {
        const val EXTRA_SCREEN_NAME = "screen_name"
        const val SCREEN_MAIN = "main"
        const val SCREEN_ABOUT = "about"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Find the buttons and set click listeners
        val openFlutterButton = findViewById<Button>(R.id.openFlutterButton)
        val openAboutButton = findViewById<Button>(R.id.openAboutButton)
        val openWebviewButton = findViewById<Button>(R.id.openWebviewButton)
        openFlutterButton.setOnClickListener {
            openFlutterModule(SCREEN_MAIN)
        }
        
        openAboutButton.setOnClickListener {
            openFlutterModule(SCREEN_ABOUT)
        }

        openWebviewButton.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun openFlutterModule(screenName: String) {
        // Launch the Flutter Activity with screen name parameter
        val intent = Intent(this, FlutterActivity::class.java)
        intent.putExtra(EXTRA_SCREEN_NAME, screenName)
        startActivity(intent)
    }
}
