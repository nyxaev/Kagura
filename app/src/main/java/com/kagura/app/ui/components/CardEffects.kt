package com.kagura.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun Modifier.tiltOnTouch(maxDeg: Float = 8f): Modifier {
    var tx by remember { mutableFloatStateOf(0f) }
    var ty by remember { mutableFloatStateOf(0f) }
    var pressed by remember { mutableStateOf(false) }
    val ax by animateFloatAsState(tx, label = "tx")
    val ay by animateFloatAsState(ty, label = "ty")
    val sc by animateFloatAsState(if (pressed) 1.04f else 1f, label = "sc")
    return this
        .pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                pressed = true
                do {
                    val ev = awaitPointerEvent()
                    val p = ev.changes.firstOrNull() ?: break
                    if (size.height > 0 && size.width > 0) {
                        tx = -((p.position.y / size.height.toFloat()) - 0.5f) * 2f * maxDeg
                        ty = ((p.position.x / size.width.toFloat()) - 0.5f) * 2f * maxDeg
                    }
                } while (ev.changes.any { it.pressed })
                pressed = false; tx = 0f; ty = 0f
            }
        }
        .graphicsLayer {
            rotationX = if (ax.isNaN()) 0f else ax
            rotationY = if (ay.isNaN()) 0f else ay
            scaleX = if (sc.isNaN()) 1f else sc
            scaleY = if (sc.isNaN()) 1f else sc
            cameraDistance = 12f * density
        }
}
