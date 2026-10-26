package com.duoc.aplicacionmunicipalidad.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.aplicacionmunicipalidad.viewmodel.RegisterState
import com.duoc.aplicacionmunicipalidad.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onRegisterSuccess: () -> Unit,
    onBackToLoginClick: () -> Unit = {}
) {
    // Datos del formulario
    var nombre by rememberSaveable { mutableStateOf("") }
    var rut by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var direccion by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmarPassword by rememberSaveable { mutableStateOf("") }
    var aceptaTerminos by rememberSaveable { mutableStateOf(false) }

    val registerState by viewModel.registerState.collectAsState()


    val primaryGreen = Color(0xFF00796B)

    //Cuando el registro es exitoso, navegamos
    LaunchedEffect(registerState) {
        if (registerState is RegisterState.Success) {
            onRegisterSuccess()
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //Titulos
        Text(
            text = "Municipalidad de San Joaquín",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Text(
            text = "Regístrate",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        )
        Text(
            text = "Crea tu cuenta de residente para recibir noticias y avisos de tu comuna",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        //Campos obligatorios
        CampoRegistro(
            valor = nombre,
            onCambio = { nombre = it },
            etiqueta = "Nombre completo*",
            icono = Icons.Filled.Person,
            colorFoco = primaryGreen
        )
        CampoRegistro(
            valor = rut,
            onCambio = { rut = it },
            etiqueta = "RUT*",
            icono = Icons.Filled.AccountBox,
            colorFoco = primaryGreen
        )
        CampoRegistro(
            valor = correo,
            onCambio = { correo = it },
            etiqueta = "Correo electrónico*",
            icono = Icons.Filled.Email,
            colorFoco = primaryGreen,
            tipoTeclado = KeyboardType.Email
        )

        //Campos opcionales
        CampoRegistro(
            valor = telefono,
            onCambio = { telefono = it },
            etiqueta = "Teléfono (opcional)",
            icono = Icons.Filled.Phone,
            colorFoco = primaryGreen,
            tipoTeclado = KeyboardType.Phone
        )
        CampoRegistro(
            valor = direccion,
            onCambio = { direccion = it },
            etiqueta = "Dirección o sector (opcional)",
            icono = Icons.Filled.Home,
            colorFoco = primaryGreen
        )

        //Contraseñas
        CampoRegistro(
            valor = password,
            onCambio = { password = it },
            etiqueta = "Contraseña*",
            icono = Icons.Filled.Lock,
            colorFoco = primaryGreen,
            esPassword = true,
            tipoTeclado = KeyboardType.Password
        )
        CampoRegistro(
            valor = confirmarPassword,
            onCambio = { confirmarPassword = it },
            etiqueta = "Confirma contraseña*",
            icono = Icons.Filled.Lock,
            colorFoco = primaryGreen,
            esPassword = true,
            tipoTeclado = KeyboardType.Password,
            ultimoCampo = true
        )

        //Terminos y condiciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = aceptaTerminos,
                onCheckedChange = { aceptaTerminos = it },
                colors = CheckboxDefaults.colors(checkedColor = primaryGreen)
            )
            Text(
                text = "Acepto los términos y condiciones de uso",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        //Mensaje de error solo en caso de error
        if (registerState is RegisterState.Error) {
            Text(
                text = (registerState as RegisterState.Error).message,
                color = Color.Red,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

       //Carga del boton segun el estado
        if (registerState is RegisterState.Loading) {
            CircularProgressIndicator(color = primaryGreen)
        } else {
            Button(
                onClick = {
                    viewModel.register(
                        nombre, rut, correo, telefono, direccion,
                        password, confirmarPassword, aceptaTerminos
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
            ) {
                Text(
                    text = "Registrarme",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        //Volver al login
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "¿Ya tienes una cuenta?",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
        TextButton(onClick = onBackToLoginClick) {
            Text(
                text = "Inicia sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = primaryGreen
            )
        }
    }
}


@Composable
private fun CampoRegistro(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    colorFoco: Color,
    esPassword: Boolean = false,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    ultimoCampo: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = etiqueta) },
        visualTransformation = if (esPassword) PasswordVisualTransformation()
        else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = tipoTeclado,
            imeAction = if (ultimoCampo) ImeAction.Done else ImeAction.Next
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorFoco,
            focusedLabelColor = colorFoco
        ),
        singleLine = true
    )
}
