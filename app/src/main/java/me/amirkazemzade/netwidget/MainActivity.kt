package me.amirkazemzade.netwidget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.amirkazemzade.netwidget.ui.AuthStateViewModel
import me.amirkazemzade.netwidget.ui.navigation.NavGraph
import me.amirkazemzade.netwidget.ui.theme.NetWidgetAppTheme
import me.amirkazemzade.netwidget.ui.widgets.remained.setRemainedWidgetPreview

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authStateViewModel: AuthStateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NetWidgetAppTheme {
                NavGraph(authStateViewModel = authStateViewModel)
            }
        }

        lifecycleScope.launch {
            setRemainedWidgetPreview(this@MainActivity)
        }
    }
}
