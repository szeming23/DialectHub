package com.dialecthub.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.dialecthub.app.data.model.ThemeMode
import com.dialecthub.app.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val claudeApiKey by viewModel.claudeApiKey.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Appearance", style = MaterialTheme.typography.titleMedium)
            ThemeMode.entries.forEach { mode ->
                ThemeOptionRow(mode = mode, selected = themeMode == mode, onSelect = { viewModel.setThemeMode(mode) })
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))

            ClaudeApiKeySection(
                savedKey = claudeApiKey,
                onSave = viewModel::setClaudeApiKey
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))

            Text("About Tâi-lô romanization", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "This app writes Hokkien pronunciation using Tâi-lô, " +
                    "the romanization system promoted by Taiwan's Ministry of Education. " +
                    "Tone marks above vowels (like â, é, ī) show pitch -- they take " +
                    "practice to hear, so each card also includes a pinyin-style spelling.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "Reading the pinyin-style spelling",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "Read it like Hanyu Pinyin, plus a few Hokkien sounds:\n" +
                    "• ⁿ -- say the vowel through your nose (saⁿ, three)\n" +
                    "• ending in h, k, t or p -- cut the syllable short (bah, meat)\n" +
                    "• e -- as in \"bed\", not the pinyin e (de, tea)\n" +
                    "• ng or m on its own -- a hummed syllable (nng, egg)\n" +
                    "Tones aren't shown here -- use the Tâi-lô tone marks for those.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))

            Button(
                onClick = { showResetDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset all progress")
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset all progress?") },
            text = { Text("This clears every quiz score, completion badge and review schedule. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetProgress()
                    showResetDialog = false
                }) { Text("Reset") }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ClaudeApiKeySection(savedKey: String, onSave: (String) -> Unit) {
    var draft by rememberSaveable { mutableStateOf("") }

    Text("Claude API key", style = MaterialTheme.typography.titleMedium)
    Text(
        text = if (savedKey.isBlank()) {
            "Used by Ask Claude to look up Hokkien words. Stored only on this device."
        } else {
            "Key saved (ending ${savedKey.takeLast(4)}). Stored only on this device."
        },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp)
    )
    OutlinedTextField(
        value = draft,
        onValueChange = { draft = it.trim() },
        label = { Text(if (savedKey.isBlank()) "sk-ant-..." else "Replace key") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Button(
            onClick = {
                onSave(draft)
                draft = ""
            },
            enabled = draft.isNotBlank()
        ) {
            Text("Save key")
        }
        if (savedKey.isNotBlank()) {
            OutlinedButton(onClick = { onSave("") }) {
                Text("Remove key")
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(mode: ThemeMode, selected: Boolean, onSelect: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect)
            .padding(vertical = 8.dp)
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(
            text = when (mode) {
                ThemeMode.SYSTEM -> "Follow system"
                ThemeMode.LIGHT -> "Light"
                ThemeMode.DARK -> "Dark"
            },
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
