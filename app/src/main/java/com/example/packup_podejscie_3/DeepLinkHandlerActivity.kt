package com.example.packup_podejscie_3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
// --- Kluczowe importy dla Supabase Auth ---
import io.github.jan.supabase.auth.auth // To jest rozszerzenie 'auth' dla SupabaseClient
// Dwa poniższe importy powinny sprawić, że funkcje będą widoczne na supabaseClient.auth
import io.github.jan.supabase.auth.parseSessionFromUrl // Dla metody parseSessionFromUrl
import io.github.jan.supabase.auth.Auth // Importuje główny obiekt Auth, co często udostępnia metody
// Jeśli Auth nie działa, można spróbować także:
// import io.github.jan.supabase.auth.auth // Dla dostępu do obiektu auth

import kotlinx.coroutines.launch
import javax.inject.Inject

// Importy dla Twojego Theme i SignInSuccessScreen
import com.example.packup_podejscie_3.ui.theme.PackUp_podejscie_3Theme


@AndroidEntryPoint
class DeepLinkHandlerActivity : ComponentActivity() {

    @Inject
    lateinit var supabaseClient: SupabaseClient

    private val emailState = mutableStateOf("")
    private val createdAtState = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleSupabaseDeepLinkInternal(intent)

        addOnNewIntentListener { newIntent ->
            Log.d("DeepLinkHandler", "Received new intent via addOnNewIntentListener: $newIntent")
            handleSupabaseDeepLinkInternal(newIntent)
        }

        setContent {
            val navController = rememberNavController()

            PackUp_podejscie_3Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SignInSuccessScreen(
                        modifier = Modifier.padding(20.dp),
                        navController = navController,
                        email = emailState.value,
                        createdAt = createdAtState.value,
                        onClick = { navigateToMainApp() }
                    )
                }
            }
        }
    }

    private fun handleSupabaseDeepLinkInternal(intent: Intent?) {
        val data = intent?.data
        if (data != null) {
            Log.d("DeepLinkHandler", "Processing deep link: $data")
            lifecycleScope.launch {
                try {
                    val urlString = data.toString()
                    val session = supabaseClient.auth.parseSessionFromUrl(urlString)
                    // Zamiast importSession i currentSessionOrNull, które sprawiają problemy
                    // Sprawdźmy, czy samo parsowanie i ewentualne użycie session wystarczy,
                    // lub czy jest inna, bardziej bezpośrednia metoda importowania.
                    // Najczęściej parseSessionFromUrl i tak powinien zapisać sesję.
                    // Jeśli nadal brakuje sesji, wtedy musimy znaleźć poprawną metodę importSession.

                    // Najprościej, po prostu użyjemy tego co dostaliśmy z parseSessionFromUrl
                    // i zakładamy, że to wystarczy do utrzymania sesji przez klienta
                    // (co w wielu przypadkach tak jest, gdy parseSessionFromUrl jest wywoływany na kliencie)
                    val user = session?.user // Sesja może być nullable
                    emailState.value = user?.email ?: "Brak emaila"
                    createdAtState.value = user?.createdAt?.toString() ?: "Brak daty utworzenia"


                    Log.d("DeepLinkHandler", "Deep link handled successfully. User email: ${emailState.value}")

                } catch (e: Exception) {
                    Log.e("DeepLinkHandler", "Error handling deep link: ${e.message}", e)
                    emailState.value = "Błąd autentykacji"
                    createdAtState.value = "Spróbuj ponownie"
                }
            }
        } else {
            Log.w("DeepLinkHandler", "DeepLinkHandlerActivity launched without a deep link. Finishing.")
            navigateToMainApp()
        }
    }

    private fun navigateToMainApp() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}

@Composable
fun SignInSuccessScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    email: String,
    createdAt: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Zalogowano pomyślnie!", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Email: $email", modifier = Modifier.padding(top = 8.dp))
        Text(text = "Konto utworzono: $createdAt", modifier = Modifier.padding(top = 4.dp))
        Button(
            onClick = onClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Przejdź do aplikacji")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInSuccessScreenPreview() {
    PackUp_podejscie_3Theme {
        SignInSuccessScreen(
            email = "test@example.com",
            createdAt = "2024-01-01T12:00:00Z",
            onClick = {},
            navController = rememberNavController()
        )
    }
}