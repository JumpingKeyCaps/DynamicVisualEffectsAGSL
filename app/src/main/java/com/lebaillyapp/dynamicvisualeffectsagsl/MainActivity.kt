package com.lebaillyapp.dynamicvisualeffectsagsl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lebaillyapp.dynamicvisualeffectsagsl.navigation.AppNavHost
import com.lebaillyapp.dynamicvisualeffectsagsl.ui.theme.DynamicVisualEffectsAGSLTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DynamicVisualEffectsAGSLTheme {
                AppNavHost()
            }
        }
    }
}
