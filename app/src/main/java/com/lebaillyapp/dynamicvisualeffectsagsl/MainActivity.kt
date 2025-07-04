package com.lebaillyapp.dynamicvisualeffectsagsl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import com.lebaillyapp.dynamicvisualeffectsagsl.ui.theme.DynamicVisualEffectsAGSLTheme
import com.lebaillyapp.dynamicvisualeffectsagsl.waterEffect.composition.WaterEffectBitmapShader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()





        setContent {
            //default bitmap
            val bitmap = ImageBitmap.imageResource(id = R.drawable.demopic_k)

            DynamicVisualEffectsAGSLTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {

                        // [1] - Water Effect composition screen

                        WaterEffectBitmapShader(
                            modifier = Modifier.fillMaxSize(),
                            bitmap = bitmap,
                            shaderResId = R.raw.water_shader
                        )

                    }
                }
            }
        }
    }
}
