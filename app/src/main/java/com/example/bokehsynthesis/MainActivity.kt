package com.example.bokehsynthesis

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bokehsynthesis.ui.theme.BokehSynthesisTheme

data class CameraSensor(
    val id: String,
    val facing: String,
    val isFlashSupported: Boolean,
    val minimumFocusDistance: Float?
)

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.fetchAllCameraSensors()
            viewModel.initCamera()
        } else {
            Toast.makeText(this, "Camera access is required.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BokehSynthesisTheme {
                val sensors by viewModel.availableSensors.collectAsState()

                Scaffold { paddingValues ->
                    SensorListScreen(
                        sensors = sensors,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }

        permissionLauncher.launch(Manifest.permission.CAMERA)
    }
}

@Composable
fun SensorListScreen(sensors: List<CameraSensor>, modifier: Modifier) {
    if (sensors.isEmpty()) {
        Text(text = "Searching for cameras...", modifier = modifier.padding(16.dp))
    } else {
        LazyColumn(modifier = modifier.fillMaxSize().padding(16.dp)) {
            items(sensors) { sensor ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Camera ID: ${sensor.id}", style = MaterialTheme.typography.titleLarge)
                        Text(text = "Facing: ${sensor.facing}")
                        Text(text = "Has Flash: ${sensor.isFlashSupported}")

                        val focusText = if (sensor.minimumFocusDistance == null || sensor.minimumFocusDistance == 0f) {
                            "Fixed Focus or No Information"
                        } else {
                            "%.2f meters".format(1f / sensor.minimumFocusDistance)
                        }
                        Text(text = "Min Focus: $focusText")
                    }
                }
            }
        }
    }
}