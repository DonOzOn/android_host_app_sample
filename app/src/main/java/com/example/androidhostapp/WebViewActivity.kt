package com.example.androidhostapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class WebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        // Setup toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = "Webview"
            setDisplayHomeAsUpEnabled(true) // <- Shows back arrow
        }

        // Handle back arrow click
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        // Setup WebView
        webView = findViewById(R.id.webview)
        webView.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl("https://illustrious-blancmange-4914c7.netlify.app/")
        }

        // Proper back handling using dispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                    finish()

            }
        })
    }

    override fun onDestroy() {
        // Clean WebView cache, history, and cookies
        webView.apply {
            clearHistory()
            clearCache(true)
            clearFormData()
            loadUrl("about:blank")
        }

        // Also clear cookies if needed
        CookieManager.getInstance().apply {
            removeAllCookies(null)
            flush()
        }

        super.onDestroy()
    }
}
