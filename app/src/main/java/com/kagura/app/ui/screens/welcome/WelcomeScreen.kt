package com.kagura.app.ui.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kagura.app.ui.theme.*

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KaguraBgBrush),
        contentAlignment = Alignment.Center
    ) {
        // Glowing aura behind moon
        Box(
            modifier = Modifier
                .size(360.dp)
                .blur(80.dp)
                .background(KaguraPink.copy(alpha = 0.3f), CircleShape)
        )

        // Moon / central circle with gradient
        Box(
            modifier = Modifier
                .size(260.dp)
                .clip(CircleShape)
                .background(KaguraMoonBrush)
        )

        // Content overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(40.dp))

            // Header / Brand
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "KAGURA",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 48.sp,
                        letterSpacing = 16.sp
                    ),
                    color = Color.White,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "神楽",
                    style = MaterialTheme.typography.titleLarge.copy(
                        letterSpacing = 8.sp
                    ),
                    color = Color(0xFFFFD6E4).copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }

            // Bottom action card / button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Sua jornada definitiva pelo universo dos animes e mangás",
                    style = MaterialTheme.typography.bodyMedium,
                    color = KaguraMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(58.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(KaguraPink)
                        .clickable(onClick = onStart),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Começar a Jornada",
                            style = MaterialTheme.typography.titleLarge,
                            color = KaguraBg,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Outlined.ArrowForward,
                            contentDescription = null,
                            tint = KaguraBg
                        )
                    }
                }
            }
        }
    }
}
