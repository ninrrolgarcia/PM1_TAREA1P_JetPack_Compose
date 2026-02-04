package com.example.pm1_tarea1p_jetpack_compose

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.pm1_tarea1p_jetpack_compose.Configuration.SQLiteConnection
import com.example.pm1_tarea1p_jetpack_compose.Configuration.Transactions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Usamos el tema por defecto de tu proyecto
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                PantallaFormulario()
            }
        }
    }
}

@Composable
fun PantallaFormulario() {
    val context = LocalContext.current

    // ESTADOS (Lo que antes eran los IDs del XML)
    var names by remember { mutableStateOf("") }
    var surnames by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Launchers para Cámara (Mantenlos igual)
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> if (bitmap != null) photoBitmap = bitmap }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted -> if (isGranted) takePictureLauncher.launch() }

    // El contenedor principal (Reemplaza al ConstraintLayout)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. ImageView (photo)
        if (photoBitmap != null) {
            Image(
                bitmap = photoBitmap!!.asImageBitmap(),
                contentDescription = "Foto",
                modifier = Modifier.size(150.dp).padding(top = 20.dp)
            )
        } else {
            // Un icono temporal si no hay foto
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(150.dp).padding(top = 20.dp)
            )
        }

        // 2. Button (btnPhoto)
        Button(
            onClick = {
                val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) takePictureLauncher.launch()
                else requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Text("Tomar Foto")
        }

        // 3. EditTexts (names, surnames, age, email)
        OutlinedTextField(
            value = names,
            onValueChange = { names = it },
            label = { Text("Nombres") },
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        )

        OutlinedTextField(
            value = surnames,
            onValueChange = { surnames = it },
            label = { Text("Apellidos") },
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
        )

        OutlinedTextField(
            value = age,
            onValueChange = { nuevoTexto ->
                // VALIDACIÓN: Solo actualiza el estado si el texto son números
                // "it.all { it.isDigit() }" verifica que cada caracter sea un número
                if (nuevoTexto.all { it.isDigit() }) {
                    age = nuevoTexto
                }
            },
            label = { Text("Edad") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            // Esto abre el teclado numérico automáticamente
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 4. Button (btnAgregar)
        Button(
            onClick = {
                saveToDatabase(context, names, surnames, age, email) {
                    // Limpiar campos después de guardar
                    names = ""; surnames = ""; age = ""; email = ""; photoBitmap = null
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Guardar Registro")
        }
    }
}

// Lógica de SQLite (Separada para orden)
fun saveToDatabase(context: Context, n: String, s: String, a: String, e: String, onComplete: () -> Unit) {
    try {
        val dbHelper = SQLiteConnection(context, Transactions.dbname, null, Transactions.dbversion)
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(Transactions.nombres, n)
            put(Transactions.apellidos, s)
            put(Transactions.edad, a)
            put(Transactions.correo, e)
            put(Transactions.foto, "")
        }

        val res = db.insert(Transactions.tbpersons, Transactions.id, values)
        if (res > 0) {
            Toast.makeText(context, "Insertado con éxito ID: $res", Toast.LENGTH_LONG).show()
            onComplete()
        }
        db.close()
    } catch (ex: Exception) {
        Toast.makeText(context, "Error: ${ex.message}", Toast.LENGTH_LONG).show()
    }
}