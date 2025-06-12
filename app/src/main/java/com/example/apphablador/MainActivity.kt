package com.example.apphablador

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextField
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.apphablador.ui.theme.AppHabladorTheme
import java.util.*

class MainActivity : ComponentActivity() {

    private var tts: TextToSpeech? = null
    private var selectedLanguage = Locale("en")
    private var selectedVoice: Voice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val langResult = tts?.setLanguage(selectedLanguage)
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show()
                }

                val voices = tts?.voices?.toList() ?: listOf()
                setContent {
                    AppHabladorTheme {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            Column(modifier = Modifier.padding(innerPadding)) {
                                var textToSpeak by remember { mutableStateOf("Hello, how are you?") }
                                val languageList = listOf(Locale("en", "US"), Locale("es", "ES"), Locale("fr", "FR"))

                                Column(modifier = Modifier.padding(16.dp)) {

                                    Text(
                                        text = "Text to Speech App",
                                        style = MaterialTheme.typography.headlineLarge,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )


                                    TextField(
                                        value = textToSpeak,
                                        onValueChange = { textToSpeak = it },
                                        label = { Text("Enter text to speak") },
                                        modifier = Modifier.fillMaxWidth().height(120.dp)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))


                                    Text("Select Language:")
                                    LazyColumn {
                                        items(languageList) { language ->
                                            TextButton(onClick = {
                                                selectedLanguage = language
                                                tts?.setLanguage(language)
                                            }) {
                                                Text(language.displayName)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))


                                    Text("Select Voice:")
                                    if (voices.isNotEmpty()) {
                                        LazyColumn(modifier = Modifier.height(200.dp)) {
                                            items(voices) { voice ->
                                                TextButton(onClick = {
                                                    selectedVoice = voice
                                                    tts?.voice = voice
                                                }) {
                                                    Text(voice.name)
                                                }
                                            }
                                        }
                                    } else {
                                        Text("No voices available.")
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(onClick = {
                                        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
                                    }) {
                                        Text("Play Audio")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Log.e("TextToSpeech", "Initialization failed")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.stop()
        tts?.shutdown()
    }
}
