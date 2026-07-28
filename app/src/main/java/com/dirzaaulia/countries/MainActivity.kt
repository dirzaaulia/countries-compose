package com.dirzaaulia.countries

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dirzaaulia.countries.ui.theme.CountriesTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CountriesTheme {
                val context = LocalContext.current
                val scope = rememberCoroutineScope()
                
                var countries by remember { mutableStateOf<List<Country>?>(null) }
                var selectedCountryId by remember { mutableStateOf<String?>(null) }
                
                val globeState = rememberGlobeState()
                
                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        try {
                            countries = GlobeRepository(context).loadCountries()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                        if (countries == null) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        } else {
                            GlobeView(
                                countries = countries!!,
                                selectedCountryId = selectedCountryId,
                                onCountrySelected = { selectedCountryId = it },
                                state = globeState
                            )
                            
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                selectedCountryId?.let { id ->
                                    val name = countries?.find { it.id == id }?.name ?: "Unknown"
                                    Text(text = "Selected: $name", color = Color.Black)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                
                                Button(onClick = {
                                    val randomCountry = countries?.randomOrNull()
                                    if (randomCountry != null) {
                                        selectedCountryId = randomCountry.id
                                        scope.launch {
                                            globeState.flyTo(
                                                targetLat = randomCountry.center.lat.toFloat(),
                                                targetLng = -randomCountry.center.lng.toFloat(),
                                                targetZoom = randomCountry.zoomLevel
                                            )
                                        }
                                    }
                                }) {
                                    Text("Highlight Random Country")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
