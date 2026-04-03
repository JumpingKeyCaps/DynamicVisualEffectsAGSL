package com.lebaillyapp.dynamicvisualeffectsagsl.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EffectsMenu(
    onSelect: (String) -> Unit,
    onEdit: (String) -> Unit
) {
    val items = listOf(
        Triple("Water Effect", "water", "water_shader"),
        Triple("Holographic Base", "holo_base", "holographic_rainbow"),
        Triple("Holographic Iridescent", "holo_Iridescent", "holographic_realistic_shader"),
        Triple("Holographic Card", "holo_card", "holographic_card_shader"),
        Triple("Topographic Flow", "topo", "topographicflow_shader"),
        Triple("Topographic Flow + Controls", "topo_controls", "topographicflow_shader")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(Color.Black)
    ) {
        LazyColumn(modifier = Modifier.align(Alignment.Center).padding(16.dp)) {
            items(items) { (label, route, shaderName) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MenuButton(
                        label = label,
                        modifier = Modifier.weight(1f)
                    ) {
                        onSelect(route)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { onEdit(shaderName) },
                        modifier = Modifier.background(Color(0xFF1C1A1A), androidx.compose.foundation.shape.CircleShape)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Shader", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun MenuButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    androidx.compose.material3.Button(
        modifier = modifier
            .heightIn(min = 64.dp)
            .padding(vertical = 8.dp),
        onClick = onClick,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(Color(0xFF1C1A1A))
    ) {
        androidx.compose.material3.Text(text = label, color = Color.White)
    }
}
