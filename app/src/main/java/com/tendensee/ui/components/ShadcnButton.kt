package com.tendensee.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tendensee.ui.theme.TendenSeeTheme

enum class ButtonVariant {
    Primary,
    Secondary,
    Destructive,
    Outline,
    Ghost,
    Link
}

@Composable
fun ShadcnButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    variant: ButtonVariant = ButtonVariant.Primary,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(6.dp), // Shadcn typically has small radii
    textStyle: TextStyle = MaterialTheme.typography.titleSmall,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    softWrap: Boolean = true
) {
    val baseTextStyle = textStyle.copy(fontWeight = FontWeight.Medium)

    when (variant) {
        ButtonVariant.Primary -> {
            Button(
                onClick = onClick,
                modifier = modifier,
                shape = shape,
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                contentPadding = contentPadding
            ) {
                Text(text = text, style = baseTextStyle, softWrap = softWrap)
            }
        }
        ButtonVariant.Secondary -> {
             Button(
                onClick = onClick,
                modifier = modifier,
                shape = shape,
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                contentPadding = contentPadding
            ) {
                Text(text = text, style = baseTextStyle, softWrap = softWrap)
            }
        }
        ButtonVariant.Destructive -> {
             Button(
                onClick = onClick,
                modifier = modifier,
                shape = shape,
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                contentPadding = contentPadding
            ) {
                Text(text = text, style = baseTextStyle, softWrap = softWrap)
            }
        }
        ButtonVariant.Outline -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier,
                shape = shape,
                enabled = enabled,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                contentPadding = contentPadding
            ) {
                Text(text = text, style = baseTextStyle, softWrap = softWrap)
            }
        }
        ButtonVariant.Ghost -> {
            TextButton(
                onClick = onClick,
                modifier = modifier,
                shape = shape,
                enabled = enabled,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                contentPadding = contentPadding
            ) {
                Text(text = text, style = baseTextStyle, softWrap = softWrap)
            }
        }
        ButtonVariant.Link -> {
            TextButton(
                onClick = onClick,
                modifier = modifier,
                shape = shape,
                enabled = enabled,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = contentPadding
            ) {
                Text(
                    text = text, 
                    style = baseTextStyle.copy(
                        textDecoration = TextDecoration.Underline
                    ),
                    softWrap = softWrap
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShadcnButtonPreview() {
    TendenSeeTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShadcnButton(onClick = {}, text = "Primary Button", variant = ButtonVariant.Primary)
            ShadcnButton(onClick = {}, text = "Secondary Button", variant = ButtonVariant.Secondary)
            ShadcnButton(onClick = {}, text = "Destructive Button", variant = ButtonVariant.Destructive)
            ShadcnButton(onClick = {}, text = "Outline Button", variant = ButtonVariant.Outline)
            ShadcnButton(onClick = {}, text = "Ghost Button", variant = ButtonVariant.Ghost)
            ShadcnButton(onClick = {}, text = "Link Button", variant = ButtonVariant.Link)
        }
    }
}
