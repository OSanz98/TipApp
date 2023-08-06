package sanzlimited.com.tipapp.widgets

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val IconButtonSizeModifier = Modifier.size(40.dp)

@Composable
fun RoundedIconButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    onClick: () -> Unit,
    tint: Color = Color.Black.copy(alpha = 0.8f),
    elevation: Dp = 4.dp
){
    Box(
        modifier = modifier.clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(elevation)
            .border(width = 2.dp, color = MaterialTheme.colorScheme.primary, shape = CircleShape)
            .then(IconButtonSizeModifier),
        contentAlignment = Center,
    ){
        Icon(imageVector, contentDescription = "Plus or minus icon", tint = tint)
    }
}