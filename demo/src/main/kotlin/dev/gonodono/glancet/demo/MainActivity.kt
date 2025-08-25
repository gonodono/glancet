package dev.gonodono.glancet.demo

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import dev.gonodono.glancet.demo.internal.appSettings
import dev.gonodono.glancet.demo.internal.appWidgetManager
import dev.gonodono.glancet.demo.internal.canPinAppWidgets
import dev.gonodono.glancet.demo.internal.displayName
import dev.gonodono.glancet.demo.internal.isComponentEnabled
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MainContent() }
    }

    internal fun requestPin(widget: Class<*>) {
        if (Build.VERSION.SDK_INT < 26) return

        val name = ComponentName(this, widget)
        val callback = createPendingResult(0, Intent(), 0)
        appWidgetManager.requestPinAppWidget(name, null, callback)
    }

    @SuppressLint("MissingSuperCall")
    @Suppress("OVERRIDE_DEPRECATION")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (runBlocking { appSettings.finishOnPin.first() }) finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Medium
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        colorResource(R.color.widget_background)
                    )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement =
                Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (LocalContext.current.canPinAppWidgets()) {
                RemoteAdapterContent()
                LazyCompatContent()
                AppSettings()
            } else {
                Text("Unable to pin widgets. Please place manually.")
            }
        }
    }
}

@Composable
private fun RemoteAdapterContent() {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            verticalArrangement =
                Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
        ) {
            Text(
                text = "RemoteAdapter Widgets",
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            HorizontalDivider(Modifier.padding(vertical = 15.dp))

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                PinButton(ListViewWidgetReceiver::class.java)
                PinButton(GridViewWidgetReceiver::class.java)
            }
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                PinButton(StackViewWidgetReceiver::class.java)
                PinButton(AdapterViewFlipperWidgetReceiver::class.java)
            }
        }
    }
}

@Composable
private fun LazyCompatContent() {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            verticalArrangement =
                Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
        ) {
            Text(
                text = "LazyCompat Widgets",
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            HorizontalDivider(Modifier.padding(vertical = 15.dp))

            PinButton(LazyColumnCompatWidgetReceiver::class.java)
            PinButton(LazyVerticalGridCompatWidgetReceiver::class.java)
        }
    }
}

@Composable
private fun <T> PinButton(receiverClazz: Class<T>)
        where T : GlanceAppWidgetReceiver {

    val activity = LocalActivity.current as MainActivity
    TextButton(
        onClick = { activity.requestPin(receiverClazz) },
        enabled = receiverClazz.isComponentEnabled(activity)
    ) {
        Text(
            text = receiverClazz.displayName(),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.MiddleEllipsis
        )
    }
}

@Composable
private fun AppSettings() {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val settings = LocalContext.current.run { remember { appSettings } }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 15.dp)
        ) {
            val scope = rememberCoroutineScope()
            val toggle: () -> Unit =
                { scope.launch { settings.toggleFinishOnPin() } }

            val finishOnPin by settings.finishOnPin.collectAsState(false)
            Checkbox(checked = finishOnPin, onCheckedChange = { toggle() })

            val style = MaterialTheme.typography.labelLarge
            Text(
                text = "Finish Activity after pinning",
                style = style.copy(Color.DarkGray),
                modifier = Modifier.clickable(onClick = toggle)
            )
        }
    }
}