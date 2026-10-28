package com.duoc.aplicacionmunicipalidad.model


import java.text.SimpleDateFormat
import java.util.Locale

// Una etapa de la línea de tiempo
data class Etapa(
    val nombre: String,
    val detalle: String,
    val completada: Boolean,
    val actual: Boolean
)

private val NOMBRES_ETAPAS = listOf(
    "Registrado", "Validado", "Asignado a unidad", "En atención", "Resuelto", "Cerrado"
)
private val RESPONSABLES = listOf(
    "Residente", "Operador", "Supervisor", "Equipo en terreno", "Equipo en terreno", "Administrador"
)
private val PENDIENTES = listOf(
    "", "Pendiente de validación del operador", "Pendiente de asignación a una unidad",
    "Pendiente de atención en terreno", "Pendiente de informe técnico", "Pendiente de validación final"
)
// Minutos que pasan desde el registro hasta cada etapa (datos de ejemplo)
private val DESFASES_MIN = listOf(0L, 15L, 30L, 870L, 1500L, 1560L)

// Posición de la etapa actual según el estado del requerimiento
fun indiceEtapaActual(estado: String): Int = when (estado) {
    "En atención" -> 3
    "Resuelto" -> 4
    else -> 0               // "Recibido"
}

private fun fechaEtapa(r: Requerimiento, indice: Int, formato: String): String =
    SimpleDateFormat(formato, Locale("es", "CL"))
        .format(r.fechaMillis + DESFASES_MIN[indice] * 60_000L)
        .replace(".", "")

fun construirEtapas(r: Requerimiento): List<Etapa> {
    val actual = indiceEtapaActual(r.estado)
    return NOMBRES_ETAPAS.mapIndexed { i, nombre ->
        val detalle = if (i <= actual) {
            fechaEtapa(r, i, "d MMM · HH:mm") + " · " + RESPONSABLES[i]
        } else {
            PENDIENTES[i]
        }
        Etapa(nombre, detalle, completada = i < actual, actual = i == actual)
    }
}

// "16 oct 2026, 09:10"
fun ultimaActualizacion(r: Requerimiento): String =
    fechaEtapa(r, indiceEtapaActual(r.estado), "d MMM yyyy, HH:mm")

// "15 oct 2026 · 18:40"
fun fechaRegistro(r: Requerimiento): String = fechaEtapa(r, 0, "d MMM yyyy · HH:mm")

fun tituloEstado(estado: String): String = when (estado) {
    "En atención" -> "Tu caso está siendo atendido"
    "Resuelto" -> "Tu caso fue resuelto"
    else -> "Recibimos tu requerimiento"
}

fun mensajeEstado(estado: String): String = when (estado) {
    "En atención" -> "El equipo en terreno está trabajando en tu requerimiento."
    "Resuelto" -> "La unidad responsable terminó el trabajo. Estamos validando el cierre del caso."
    else -> "Un operador lo revisará y validará a la brevedad."
}

// Unidad municipal responsable según la categoría (nombres de ejemplo)
fun unidadDe(categoria: String): String = when (categoria) {
    "Alumbrado" -> "Mantención de Alumbrado"
    "Seguridad" -> "Seguridad Ciudadana"
    "Espacio público" -> "Aseo y Ornato"
    "Tránsito" -> "Dirección de Tránsito"
    else -> "Por asignar"
}
// Opciones del formulario de gestión
val UNIDADES = listOf(
    "Mantención de Alumbrado", "Seguridad Ciudadana", "Aseo y Ornato", "Dirección de Tránsito"
)
val ROLES_DERIVACION = listOf("Operador", "Supervisor", "Administrador")

// Unidad asignada por el funcionario, o la que corresponde según la categoría
fun unidadResponsable(r: Requerimiento): String = r.unidad.ifBlank { unidadDe(r.categoria) }

fun siguienteEtapa(estado: String): String = when (estado) {
    "Recibido" -> "Siguiente etapa: En atención. Valida el caso y asígnalo a una unidad."
    "En atención" -> "Siguiente etapa: Resuelto. Adjunta el resultado de la intervención antes del cierre."
    else -> "Siguiente etapa: Cierre. Queda pendiente la validación final."
}
