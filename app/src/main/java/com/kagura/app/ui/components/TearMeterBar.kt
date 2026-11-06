package com.kagura.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.kagura.app.ui.theme.KaguraPink

@Composable
fun TearMeterBar(level: Int, modifier: Modifier = Modifier, max: Int = 5, onLevelChange: ((Int) -> Unit)? = null) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        for (i in 1..max) {
            val click = if (onLevelChange != null) Modifier.clickable { onLevelChange(if (level == i) 0 else i) } else Modifier
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = KaguraPink,
                modifier = Modifier.alpha(if (i <= level) 1f else 0.25f).then(click)
            )
        }
    }
}
