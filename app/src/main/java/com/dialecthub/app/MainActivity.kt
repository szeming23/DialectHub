package com.dialecthub.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.dialecthub.app.data.model.ThemeMode
import com.dialecthub.app.ui.navigation.DialectHubNavHost
import com.dialecthub.app.ui.theme.DialectHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = LocalContext.current.applicationContext as DialectHubApplication
            val themeMode by app.progressRepository.themeMode.collectAsState(initial = ThemeMode.SYSTEM)

            DialectHubTheme(themeMode = themeMode) {
                DialectHubNavHost()
            }
        }
    }
}
