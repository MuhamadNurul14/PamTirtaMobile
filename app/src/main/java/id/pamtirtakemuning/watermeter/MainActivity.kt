package id.pamtirtakemuning.watermeter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import id.pamtirtakemuning.watermeter.ui.PamApp
import id.pamtirtakemuning.watermeter.ui.theme.PamTheme

class MainActivity : ComponentActivity() {
    private val vm: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { PamTheme { PamApp(vm) } }
    }
}
