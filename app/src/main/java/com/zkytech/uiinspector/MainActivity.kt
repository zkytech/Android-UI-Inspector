package com.zkytech.uiinspector

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElementTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    MainScreen()
                }
            }
        }
    }
}

// Element UI Colors
val ElementPrimary = Color(0xFF409EFF)
val ElementBg = Color(0xFFF5F7FA)
val ElementTextMain = Color(0xFF303133)
val ElementTextRegular = Color(0xFF606266)
val ElementTextSecondary = Color(0xFF909399)
val ElementSuccess = Color(0xFF67C23A)
val ElementWarning = Color(0xFFE6A23C)

@Composable
fun ElementTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = ElementPrimary,
            background = ElementBg,
            surface = Color.White,
            onPrimary = Color.White,
            onBackground = ElementTextMain,
            onSurface = ElementTextMain
        ),
        content = content
    )
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var overlayGranted by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    var accessibilityEnabled by remember { mutableStateOf(context.isInspectorAccessibilityEnabled()) }

    fun refreshPermissionState() {
        overlayGranted = Settings.canDrawOverlays(context)
        accessibilityEnabled = context.isInspectorAccessibilityEnabled()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshPermissionState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ElementTextMain,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = stringResource(R.string.message_inspect_ui_elements),
                fontSize = 16.sp,
                color = ElementTextRegular,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PermissionStatusRow(
                        label = stringResource(R.string.label_overlay_permission),
                        granted = overlayGranted,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PermissionStatusRow(
                        label = stringResource(R.string.label_accessibility_service),
                        granted = accessibilityEnabled,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElementPrimary),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(stringResource(R.string.action_enable_accessibility_service))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFDCDFE6)),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(stringResource(R.string.action_grant_overlay_permission), color = ElementTextRegular)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (overlayGranted && accessibilityEnabled) {
                            stringResource(R.string.message_ready_to_inspect)
                        } else {
                            stringResource(R.string.message_permission_instruction)
                        },
                        color = ElementTextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            UsageGuide(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PermissionStatusRow(
    label: String,
    granted: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    color = if (granted) ElementSuccess else ElementWarning,
                    shape = RoundedCornerShape(percent = 50)
                )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            color = ElementTextMain,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(if (granted) R.string.status_enabled else R.string.status_required),
            color = if (granted) ElementSuccess else ElementWarning,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun UsageGuide(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = stringResource(R.string.title_quick_start),
                color = ElementTextMain,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            GuideLine(step = "1", text = stringResource(R.string.guide_step_permissions))
            GuideLine(step = "2", text = stringResource(R.string.guide_step_floating_button))
            GuideLine(step = "3", text = stringResource(R.string.guide_step_copy_properties))
        }
    }
}

@Composable
private fun GuideLine(step: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = step,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .background(ElementPrimary, RoundedCornerShape(percent = 50))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, color = ElementTextRegular, fontSize = 13.sp)
    }
}

private fun android.content.Context.isInspectorAccessibilityEnabled(): Boolean {
    val expectedService = ComponentName(this, InspectorService::class.java)
    val accessibilityManager = getSystemService(AccessibilityManager::class.java)
    val managerEnabled = accessibilityManager
        ?.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        ?.any { serviceInfo ->
            val service = serviceInfo.resolveInfo.serviceInfo
            service.packageName == expectedService.packageName && service.name == expectedService.className
        } == true
    if (managerEnabled) return true

    val accessibilityEnabled = Settings.Secure.getString(
        contentResolver,
        Settings.Secure.ACCESSIBILITY_ENABLED
    ) == "1"
    if (!accessibilityEnabled) return false

    val enabledServices = Settings.Secure.getString(
        contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false

    return enabledServices.split(':').any {
        it.equals(expectedService.flattenToString(), ignoreCase = true)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ElementTheme {
        MainScreen()
    }
}
