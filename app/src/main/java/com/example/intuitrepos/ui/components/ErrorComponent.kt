package com.example.intuitrepos.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.intuitrepos.R

@Composable
fun ErrorComponent(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry
            ) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
} 