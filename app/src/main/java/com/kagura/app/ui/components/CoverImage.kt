package com.kagura.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.kagura.app.ui.theme.KaguraSurface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CoverImage(
    url: String, title: String, accent: Long, modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = null, contentScale: ContentScale = ContentScale.Crop
) {
    Box(modifier.background(Brush.verticalGradient(listOf(Color(accent).copy(alpha = 0.55f), KaguraSurface)))) {
        Text(
            title.take(2).uppercase(), Modifier.align(Alignment.Center),
            color = Color.White.copy(alpha = 0.3f), style = MaterialTheme.typography.headlineLarge
        )
        AsyncImage(
            model = url, contentDescription = title, contentScale = contentScale,
            colorFilter = colorFilter, modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun rememberDominantColor(url: String, fallback: Color): State<Color> {
    val context = LocalContext.current
    val state = remember(url) { mutableStateOf(fallback) }
    LaunchedEffect(url) {
        val req = ImageRequest.Builder(context).data(url).allowHardware(false).size(200, 300).build()
        val res = context.imageLoader.execute(req)
        if (res is SuccessResult) {
            val bmp = res.drawable.toBitmap()
            val p = withContext(Dispatchers.Default) { Palette.from(bmp).maximumColorCount(16).generate() }
            val c = p.getVibrantColor(p.getDarkVibrantColor(p.getDominantColor(0)))
            if (c != 0) state.value = Color(c)
        }
    }
    return state
}
