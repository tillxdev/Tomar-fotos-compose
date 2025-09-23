→ AndroidManifest.xml
=====================
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:tools="http://schemas.android.com/tools">
<uses-feature android:name="android.hardware.camera" android:required="false" />
<uses-feature android:name="android.hardware.camera.front" android:required="false" />
<uses-permission android:name="android.permission.CAMERA" />


    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Tomar_Foto_Compose">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.Tomar_Foto_Compose">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
=============================
→ CameraScreen.kt
=============================
package com.example.tomar_foto_compose.ui.theme

import android.graphics.BitmapFactory
import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.tomar_foto_compose.CameraViewModel
import kotlinx.coroutines.delay
import java.io.File
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

            val file = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
            val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

            imageCapture?.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        viewModel.addPhoto(bitmap)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("Camera", "Error: ${exception.message}")
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
        //val horaActual = HoraActualView()


        Text(
           // text = "© 2025 UNP Informática • $horaActual • Piura, Perú",
            text = "© 2025 UNP Informática •  Piura, Perú",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = Color.Red
        )
}

}
private fun Int.asImageBitmap(): ImageBitmap {
return TODO("Provide the return value")
}



=====================
CameraViewModel
===================
package com.example.tomar_foto_compose

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {
private val _photos = mutableStateListOf<Bitmap>()
val photos: List<Bitmap> get() = _photos

    fun addPhoto(photo: Bitmap) {
        _photos.add(photo)
    }
}

==============
Color.kt
===============
package com.example.tomar_foto_compose.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
==============
Plantilla.kt
============
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
========================
Theme.kt
========================
package com.example.tomar_foto_compose.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
primary = Purple80,
secondary = PurpleGrey80,
tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
primary = Purple40,
secondary = PurpleGrey40,
tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun Tomar_Foto_ComposeTheme(
darkTheme: Boolean = isSystemInDarkTheme(),
// Dynamic color is available on Android 12+
dynamicColor: Boolean = true,
content: @Composable () -> Unit
) {
val colorScheme = when {
dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
val context = LocalContext.current
if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
}

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
======================
Type.kt
=================
package com.example.tomar_foto_compose.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
bodyLarge = TextStyle(
fontFamily = FontFamily.Default,
fontWeight = FontWeight.Normal,
fontSize = 16.sp,
lineHeight = 24.sp,
letterSpacing = 0.5.sp
)
/* Other default text styles to override
titleLarge = TextStyle(
fontFamily = FontFamily.Default,
fontWeight = FontWeight.Normal,
fontSize = 22.sp,
lineHeight = 28.sp,
letterSpacing = 0.sp
),
labelSmall = TextStyle(
fontFamily = FontFamily.Default,
fontWeight = FontWeight.Medium,
fontSize = 11.sp,
lineHeight = 16.sp,
letterSpacing = 0.5.sp
)
*/
)
=====================
MainActivity
====================
package com.example.tomar_foto_compose

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tomar_foto_compose.ui.theme.CameraScreen


class MainActivity : ComponentActivity() {
override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 0)
        }

        val viewModel = CameraViewModel()
        setContent {

            CameraScreen(viewModel)
        }
    }

}


==================
build.gradle
===============
plugins {
alias(libs.plugins.android.application)
alias(libs.plugins.kotlin.android)
alias(libs.plugins.kotlin.compose)
}



android {
namespace 'com.example.tomar_foto_compose'
compileSdk 36

    defaultConfig {
        applicationId "com.example.tomar_foto_compose"
        minSdk 24
        targetSdk 36
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = '11'
    }
    buildFeatures {
        compose true
    }
}

dependencies {
implementation libs.androidx.core.ktx
implementation libs.androidx.lifecycle.runtime.ktx
implementation libs.androidx.activity.compose
implementation platform(libs.androidx.compose.bom)
implementation libs.androidx.ui
implementation libs.androidx.ui.graphics
implementation libs.androidx.ui.tooling.preview
implementation libs.androidx.material3

    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.ui)
    implementation(libs.androidx.lifecycle.viewmodel.compose)


    testImplementation libs.junit
    androidTestImplementation libs.androidx.junit
    androidTestImplementation libs.androidx.espresso.core
    androidTestImplementation platform(libs.androidx.compose.bom)
    androidTestImplementation libs.androidx.ui.test.junit4
    debugImplementation libs.androidx.ui.tooling
    debugImplementation libs.androidx.ui.test.manifest
}
======================
ic_launcher_background.xml
======================
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
android:width="108dp"
android:height="108dp"
android:viewportWidth="108"
android:viewportHeight="108">
<path
android:fillColor="#3DDC84"
android:pathData="M0,0h108v108h-108z" />
<path
android:fillColor="#00000000"
android:pathData="M9,0L9,108"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M19,0L19,108"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M29,0L29,108"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M39,0L39,108"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M49,0L49,108"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M59,0L59,108"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M69,0L69,108"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M79,0L79,108"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M89,0L89,108"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M99,0L99,108"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M0,9L108,9"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M0,19L108,19"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M0,29L108,29"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M0,39L108,39"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M0,49L108,49"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M0,59L108,59"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M0,69L108,69"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M0,79L108,79"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M0,89L108,89"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M0,99L108,99"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M19,29L89,29"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M19,39L89,39"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M19,49L89,49"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M19,59L89,59"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M19,69L89,69"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M19,79L89,79"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M29,19L29,89"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M39,19L39,89"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M49,19L49,89"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M59,19L59,89"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M69,19L69,89"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
<path
android:fillColor="#00000000"
android:pathData="M79,19L79,89"
android:strokeWidth="0.8"
android:strokeColor="#33FFFFFF" />
</vector>

======================
ic_launcher_foreground.xml
======================
<vector xmlns:android="http://schemas.android.com/apk/res/android"
xmlns:aapt="http://schemas.android.com/aapt"
android:width="108dp"
android:height="108dp"
android:viewportWidth="108"
android:viewportHeight="108">
<path android:pathData="M31,63.928c0,0 6.4,-11 12.1,-13.1c7.2,-2.6 26,-1.4 26,-1.4l38.1,38.1L107,108.928l-32,-1L31,63.928z">
<aapt:attr name="android:fillColor">
<gradient
android:endX="85.84757"
android:endY="92.4963"
android:startX="42.9492"
android:startY="49.59793"
android:type="linear">
<item
android:color="#44000000"
android:offset="0.0" />
<item
android:color="#00000000"
android:offset="1.0" />
</gradient>
</aapt:attr>
</path>
<path
android:fillColor="#FFFFFF"
android:fillType="nonZero"
android:pathData="M65.3,45.828l3.8,-6.6c0.2,-0.4 0.1,-0.9 -0.3,-1.1c-0.4,-0.2 -0.9,-0.1 -1.1,0.3l-3.9,6.7c-6.3,-2.8 -13.4,-2.8 -19.7,0l-3.9,-6.7c-0.2,-0.4 -0.7,-0.5 -1.1,-0.3C38.8,38.328 38.7,38.828 38.9,39.228l3.8,6.6C36.2,49.428 31.7,56.028 31,63.928h46C76.3,56.028 71.8,49.428 65.3,45.828zM43.4,57.328c-0.8,0 -1.5,-0.5 -1.8,-1.2c-0.3,-0.7 -0.1,-1.5 0.4,-2.1c0.5,-0.5 1.4,-0.7 2.1,-0.4c0.7,0.3 1.2,1 1.2,1.8C45.3,56.528 44.5,57.328 43.4,57.328L43.4,57.328zM64.6,57.328c-0.8,0 -1.5,-0.5 -1.8,-1.2s-0.1,-1.5 0.4,-2.1c0.5,-0.5 1.4,-0.7 2.1,-0.4c0.7,0.3 1.2,1 1.2,1.8C66.5,56.528 65.6,57.328 64.6,57.328L64.6,57.328z"
android:strokeWidth="1"
android:strokeColor="#00000000" />
</vector>
======================

======================

======================

======================

======================

======================

======================

======================

======================

======================

======================

======================



