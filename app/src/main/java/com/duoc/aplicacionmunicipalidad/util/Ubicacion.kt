package com.duoc.aplicacionmunicipalidad.util

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

/**
 * Pide al GPS del teléfono la ubicación actual.
 * Se debe llamar solo después de que el usuario aceptó el permiso de ubicación.
 */
@SuppressLint("MissingPermission")
fun obtenerUbicacionActual(
    contexto: Context,
    alObtener: (latitud: Double, longitud: Double) -> Unit,
    alFallar: () -> Unit
) {
    LocationServices.getFusedLocationProviderClient(contexto)
        .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
        .addOnSuccessListener { ubicacion ->
            if (ubicacion != null) alObtener(ubicacion.latitude, ubicacion.longitude) else alFallar()
        }
        .addOnFailureListener { alFallar() }
}
