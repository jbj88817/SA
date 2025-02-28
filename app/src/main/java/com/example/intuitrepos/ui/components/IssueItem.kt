package com.example.intuitrepos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.intuitrepos.R
import com.example.intuitrepos.domain.model.Issue
import com.example.intuitrepos.ui.theme.IssueClosed
import com.example.intuitrepos.ui.theme.IssueOpen
import com.example.intuitrepos.ui.theme.TextSecondary

@Composable
fun IssueItem(issue: Issue) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (issue.state == "open") IssueOpen else IssueClosed)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = stringResource(R.string.issue_number, issue.number),
                    style = MaterialTheme.typography.caption,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = issue.title,
                    style = MaterialTheme.typography.subtitle1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (issue.body != null) {
                Text(
                    text = issue.body,
                    style = MaterialTheme.typography.body2,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.created_at, issue.createdAt.substring(0, 10)),
                    style = MaterialTheme.typography.caption,
                    color = TextSecondary
                )
                
                Text(
                    text = "by ${issue.user.login}",
                    style = MaterialTheme.typography.caption,
                    color = TextSecondary
                )
            }
        }
    }
} 