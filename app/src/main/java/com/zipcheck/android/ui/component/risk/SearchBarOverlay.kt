package com.zipcheck.android.ui.component.risk

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zipcheck.android.R
import com.zipcheck.android.ui.theme.Black
import com.zipcheck.android.ui.theme.White

@Composable
public fun SearchBarOverlay(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        modifier = modifier
            .height(48.dp)
            .background(
                color = White.copy(alpha = 0.95f),
                shape = RoundedCornerShape(24.dp)
            )
            .shadow(6.dp, RoundedCornerShape(24.dp)),
        textStyle =  MaterialTheme.typography.bodyMedium.copy(
            fontSize = 14.sp,
            color = Black
        ),
        shape = RoundedCornerShape(24.dp),
        leadingIcon = leadingIcon ?: {
            Icon(
                painterResource(id = R.drawable.ic_home_search),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Icon(
                    painterResource(id = R.drawable.ic_clear),
                    contentDescription = "지우기",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onQueryChange("") }
                )
            } else {
                trailingIcon?.invoke()
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}