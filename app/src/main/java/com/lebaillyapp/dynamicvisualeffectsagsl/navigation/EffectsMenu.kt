package com.lebaillyapp.dynamicvisualeffectsagsl.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EffectsMenu(onSelect: (String) -> Unit) {
    val items = listOf(
        "Water Effect" to "water",
        "Holographic Base" to "holo_base",
        "Holographic Iridescent " to "holo_Iridescent",
        "Holographic Card" to "holo_card",
        "Topographic Flow" to "topo",
        "Topographic Flow + Controls" to "topo_controls"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(Color.Black)
    ) {
        LazyColumn(modifier = Modifier.align(Alignment.Center).padding(16.dp)) {
            items(items) { (label, route) ->
                MenuButton(label = label) {
                    onSelect(route)
                }
            }
        }
    }
}

@Composable
fun MenuButton(label: String, onClick: () -> Unit) {
    androidx.compose.material3.Button(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 86.dp)
            .padding(vertical = 8.dp),
        onClick = onClick,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(Color(0xFF1C1A1A))
    ) {
        androidx.compose.material3.Text(text = label, color = Color.White)
    }
}