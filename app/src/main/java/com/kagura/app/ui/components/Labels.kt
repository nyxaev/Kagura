package com.kagura.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kagura.app.domain.model.ItemStatus
import com.kagura.app.domain.model.MediaType

fun ItemStatus.label(type: MediaType): String {
    val manga = type == MediaType.MANGA
    return when (this) {
        ItemStatus.NONE -> "Nenhum"
        ItemStatus.WANT -> if (manga) "Quero ler" else "Quero ver"
        ItemStatus.IN_PROGRESS -> if (manga) "Lendo" else "Assistindo"
        ItemStatus.COMPLETED -> "Concluído"
    }
}

@Composable
fun Pill(text: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier, shape = RoundedCornerShape(50), color = color.copy(alpha = 0.18f),
        border = BorderStroke(1.dp, color)
    ) {
        Text(text, Modifier.padding(horizontal = 10.dp, vertical = 3.dp), color = color, style = MaterialTheme.typography.labelSmall)
    }
}
