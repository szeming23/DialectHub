package com.dialecthub.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dialecthub.app.data.HokkienTranslation
import com.dialecthub.app.data.TranslationOutcome
import com.dialecthub.app.ui.theme.FlashcardHanjiStyle
import com.dialecthub.app.viewmodel.TranslateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslateScreen(
    viewModel: TranslateViewModel,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit
) {
    val hasApiKey by viewModel.hasApiKey.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ask Claude") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (hasApiKey) {
                null -> Unit
                false -> MissingApiKey(onOpenSettings = onOpenSettings)
                true -> TranslateContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun MissingApiKey(onOpenSettings: () -> Unit) {
    Text(
        text = "Add your Claude API key in Settings to look up Hokkien words.",
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(vertical = 16.dp)
    )
    Button(onClick = onOpenSettings) {
        Text("Open Settings")
    }
}

@Composable
private fun TranslateContent(viewModel: TranslateViewModel) {
    OutlinedTextField(
        value = viewModel.input,
        onValueChange = viewModel::onInputChange,
        label = { Text("English word or phrase") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { viewModel.translate() }),
        modifier = Modifier.fillMaxWidth()
    )

    Button(
        onClick = viewModel::translate,
        enabled = viewModel.input.isNotBlank() && !viewModel.isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        Text("Translate to Hokkien")
    }

    if (viewModel.isLoading) {
        CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp).size(40.dp))
        return
    }

    when (val outcome = viewModel.outcome) {
        null -> Unit
        is TranslationOutcome.Found -> TranslationCard(outcome.translation)
        TranslationOutcome.NoEquivalent -> Text(
            text = "Claude doesn't know a Hokkien equivalent for that.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp)
        )
        is TranslationOutcome.Failed -> Text(
            text = outcome.message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

@Composable
private fun TranslationCard(translation: HokkienTranslation) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = translation.hanji,
                style = FlashcardHanjiStyle,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
            Text(
                text = translation.tailo,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = "pinyin-style: ${translation.pinyinStyle}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
    Text(
        text = "AI-generated -- double-check with a native speaker.",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp)
    )
}
