package com.example.tomar_foto_compose.ui.theme


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tomar_foto_compose.CameraViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewCapturaFoto(name: String, modifier: Modifier = Modifier) {


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        //  CABECERA CON TOOLBAR
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,

                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Capturar Fotos",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF00695C))
        )
        //cuerpo
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val cameraViewModel: CameraViewModel = viewModel()
            CameraScreen(viewModel = cameraViewModel)
        }

        //pie de pagina
        val horaActual = HoraActualView()


        Text(
            text = "© 2025 UNP Informática • $horaActual • Piura, Perú",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = Color.Red
        )
    }



}

@Composable
fun HoraActualView(): String {
    var horaActual by remember { mutableStateOf(obtenerHoraActual()) }

    // Actualiza la hora cada segundo
// Actualiza la hora cada segundo
    LaunchedEffect(Unit) {
        while (true) {
            horaActual = obtenerHoraActual()
            delay(1000) // 1 segundo
        }
    }

    return horaActual
}
fun obtenerHoraActual(): String {
    val formato = SimpleDateFormat("hh:mm:ss a", Locale("es", "PE"))
    return formato.format(Date())
}
