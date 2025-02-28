package com.example.sa.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.sa.R
import com.example.sa.domain.model.Repository
import com.example.sa.ui.theme.IntuitGreen
import com.example.sa.ui.theme.TextSecondary

@Composable
fun RepositoryItem(
    repository: Repository,
    onClick: (Repository) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(repository) },
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = repository.name,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primary
            )
            
            if (repository.description != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = repository.description,
                    style = MaterialTheme.typography.body2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = IntuitGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.stars, repository.stars),
                    style = MaterialTheme.typography.caption
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = repository.language ?: stringResource(R.string.no_language),
                    style = MaterialTheme.typography.caption,
                    color = TextSecondary
                )
            }
        }
    }
} 