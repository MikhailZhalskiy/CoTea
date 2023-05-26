package com.mw.cotea

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(viewModel: MainActivityViewModel) {

    val mainState by viewModel.mainState.collectAsState()
    var showSnackBar by remember { mutableStateOf("") }

    LaunchedEffect(key1 = true) {
        viewModel.mainSideEffect.collect { sideEffect ->
            showSnackBar = sideEffect.toString()
        }
    }

    Column {
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = viewModel::loadOne
        ) {
            Text(text = "load_one")
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = viewModel::loadTwo
        ) {
            Text(text = "load_two")
        }

        Text(text = mainState.toString())
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = showSnackBar)
    }
}