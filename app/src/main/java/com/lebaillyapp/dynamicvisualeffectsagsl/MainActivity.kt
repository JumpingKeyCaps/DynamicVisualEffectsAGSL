package com.lebaillyapp.dynamicvisualeffectsagsl

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.lebaillyapp.dynamicvisualeffectsagsl.navigation.AppNavHost
import com.lebaillyapp.dynamicvisualeffectsagsl.ui.theme.DynamicVisualEffectsAGSLTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable modern edge-to-edge support
        enableEdgeToEdge()

        // 1. Allow the window to use the full screen including the notch/cutout area
        window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        
        // 2. Use FLAG_LAYOUT_NO_LIMITS to allow the window to extend beyond standard screen decorations
        // This ensures the visualisation fills the entire display without being constrained by system bar areas.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        // 3. Hide the system bars (Status Bar and Navigation Bar)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            DynamicVisualEffectsAGSLTheme {
                AppNavHost()
            }
        }
    }
}
