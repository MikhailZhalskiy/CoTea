package com.mw.cotea

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mw.cotea.main.MainSideEffect
import com.mw.cotea.main.MainViewState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun MainScreen(viewModel: MainActivityViewModel) {

    val mainViewState by viewModel.mainViewState.collectAsState()

    MainView(
        mainViewState,
        viewModel.mainSideEffect,
        onValueChange = viewModel::onInputText,
        onLoadDataClick = viewModel::onLoadDataClick
    )
}

@Composable
fun MainView(
    mainViewState: MainViewState,
    mainSideEffect: SharedFlow<MainSideEffect>,
    onValueChange: (String) -> Unit = {},
    onLoadDataClick: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        mainSideEffect
            .onEach { sideEffect ->
                scope.launch {
                    snackbarHostState.showSnackbar(sideEffect.toString())
                }
            }
            .launchIn(this)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { value ->
                        onValueChange(value)
                        inputText = value
                    },
                    label = {
                        Text(text = "Input text")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                if (mainViewState.isLoadingList) {
                    CircularProgressIndicator(modifier = Modifier.align(CenterHorizontally))
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(mainViewState.words.size) {
                            Text(
                                text = mainViewState.words[it],
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Button(
                    enabled = !mainViewState.isLoadingOnButton,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onClick = onLoadDataClick
                ) {
                    if (mainViewState.isLoadingOnButton) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(text = "load data ${mainViewState.size}")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = mainViewState.toString())
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
private fun PreviewMainView() {
    val mainViewState = MainViewState(
        words = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
        isLoadingList = false,
        isLoadingOnButton = false
    )
    MainView(
        mainViewState = mainViewState,
        mainSideEffect = MutableSharedFlow(),
    )
}