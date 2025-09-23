package com.example.tomar_foto_compose.ui.theme

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.tomar_foto_compose.CameraViewModel
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(viewModel: CameraViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    Column {
        // Cabecera
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

            //Cuerpo
            AndroidView(factory = { ctx ->
                PreviewView(ctx).also { previewView ->
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val capture = ImageCapture.Builder().build()
                        imageCapture = capture

                        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, capture
                        )
                    }, ContextCompat.getMainExecutor(ctx))
                }
            }, modifier = Modifier.size(250.dp))

            Spacer(modifier = Modifier.size(40.dp))

            Button(onClick = {
                // Crear nombre único para la foto
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "foto_$timeStamp.jpg"
                
                // Crear archivo temporal
                val tempFile = File(context.cacheDir, fileName)
                val outputOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()

                imageCapture?.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            try {
                                // Guardar la foto en la galería
                                savePhotoToGallery(context, tempFile, fileName)
                                
                                // Agregar al ViewModel para mostrar en la lista
                                val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
                                viewModel.addPhoto(bitmap)
                                
                                // Mostrar mensaje de éxito
                                Toast.makeText(context, "Foto guardada en: Pictures/MisCapturasApp/$fileName", Toast.LENGTH_LONG).show()
                                
                                // Eliminar archivo temporal
                                tempFile.delete()
                            } catch (e: Exception) {
                                Log.e("Camera", "Error guardando la foto: ${e.message}")
                                Toast.makeText(context, "Error al guardar la foto", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Log.e("Camera", "Error capturando la foto: ${exception.message}")
                            Toast.makeText(context, "Error al capturar la foto", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }) {
                Text("Capturar Foto")
            }

            LazyColumn {
                items(viewModel.photos) { photo ->
                    Image(
                        bitmap = photo.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
        }
        
        //pie de pagina
        Text(
            text = "© 2025 UNP Informática • Piura, Perú",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = Color.Red
        )
    }
}

/**
 * Función para guardar la foto en la galería del dispositivo
 * Crea automáticamente la carpeta "MisCapturasApp" en Pictures
 */
private fun savePhotoToGallery(context: Context, photoFile: File, fileName: String) {
    val resolver: ContentResolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MisCapturasApp")
    }

    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    
    imageUri?.let { uri ->
        try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                FileInputStream(photoFile).use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Log.d("Camera", "Foto guardada exitosamente en la galería: $fileName")
        } catch (e: IOException) {
            Log.e("Camera", "Error al escribir la foto en la galería: ${e.message}")
            throw e
        }
    } ?: throw IOException("No se pudo crear la entrada en MediaStore")
}