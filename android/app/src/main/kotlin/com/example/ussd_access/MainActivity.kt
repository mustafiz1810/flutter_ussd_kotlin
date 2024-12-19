package com.example.ussd_access

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.ussd_access"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "openAccessibilitySettings" -> {
                    openAccessibilitySettings()
                    result.success(null)
                }
                "startUssdSession" -> {
                    val ussdCode = call.argument<String>("ussdCode")
                    if (ussdCode != null) {
                        startUssdSession(ussdCode)
                        result.success("USSD Session started for $ussdCode")
                    } else {
                        result.error("INVALID_CODE", "USSD code is null or invalid", null)
                    }
                }
                else -> result.notImplemented()
            }
        }
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    private fun isAccessibilityServiceEnabled(context: Context, service: Class<*>): Boolean {
        val expectedComponentName = ComponentName(context, service)
        val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: ""
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServices)
        while (colonSplitter.hasNext()) {
            val componentName = ComponentName.unflattenFromString(colonSplitter.next())
            if (componentName != null && componentName == expectedComponentName) {
                return true
            }
        }
        return false
    }

    private fun startUssdSession(ussdCode: String) {
        try {
            val encodedHash = Uri.encode("#")
            val ussdUri = "tel:${ussdCode.replace("#", encodedHash)}"
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse(ussdUri)
            }
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
