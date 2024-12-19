package com.example.ussd_access

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.KeyEvent
import android.widget.Toast

class UssdAccessibilityService : AccessibilityService() {
    private val TAG = "UssdAccessibilityService"
    private val steps = listOf("1", "01854969657", "20", "1", "12345")
    private var currentStep = 0

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val rootNode = rootInActiveWindow
            rootNode?.let {
                handleUssdDialog(it)
            }
        }
    }

    private fun handleUssdDialog(node: AccessibilityNodeInfo) {
        // Log the node for debugging purposes
        Log.d(TAG, "Root Node Text: ${node.text}")


        node.findAccessibilityNodeInfosByText("Send")?.forEach { button ->
            Log.d(TAG, "Found Send button")


            if (currentStep < steps.size) {

                val inputField = findInputField(node)


                if (inputField != null) {
                    Log.d(TAG, "Entering text: ${steps[currentStep]}")

                    inputField.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, null) // Set text is handled by ACTION_SET_TEXT


                    currentStep++
                }
            } else {
                Log.d(TAG, "USSD session completed.")
                stopSelf()
            }


            button.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }


    private fun findInputField(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {

        return node.findAccessibilityNodeInfosByText("Enter number")?.firstOrNull()
                ?: node.findAccessibilityNodeInfosByText("Amount")?.firstOrNull()
                ?: node.findAccessibilityNodeInfosByText("Reference")?.firstOrNull()
                ?: node.findAccessibilityNodeInfosByText("Enter PIN")?.firstOrNull()
                ?: findNodeByClassName(node, "android.widget.EditText")
    }


    private fun findNodeByClassName(node: AccessibilityNodeInfo, className: String): AccessibilityNodeInfo? {
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null && child.className == className) {
                return child
            }
        }
        return null
    }

    override fun onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted.")
    }
}
