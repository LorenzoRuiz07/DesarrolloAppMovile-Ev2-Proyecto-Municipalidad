package com.duoc.aplicacionmunicipalidad.repository


import com.duoc.aplicacionmunicipalidad.model.Requerimiento
import java.util.Calendar

//Revisar datos+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
class RequerimientoRepository {

    fun obtenerRequerimientos(): List<Requerimiento> = listOf(
        Requerimiento(
            id = "SJ-2026-0148",
            titulo = "Luminaria apagada en plaza Valdivieso",
            lugar = "Plaza Valdivieso",
            sector = "Carmen",
            categoria = "Alumbrado",
            prioridad = "Media",
            estado = "En atención",
            fechaMillis = fecha(15, 18, 40)
        ),
        Requerimiento(
            id = "SJ-2026-0147",
            titulo = "Ruidos molestos y riña en la vía pública",
            lugar = "Av. Departamental",
            sector = "Santa Elena",
            categoria = "Seguridad",
            prioridad = "Alta",
            estado = "Recibido",
            fechaMillis = fecha(15, 16, 5)
        ),
        Requerimiento(
            id = "SJ-2026-0142",
            titulo = "Semáforo sin funcionar en cruce principal",
            lugar = "Vicuña Mackenna con Carmen",
            sector = "Carmen",
            categoria = "Tránsito",
            prioridad = "Alta",
            estado = "En atención",
            fechaMillis = fecha(14, 9, 20)
        ),
        Requerimiento(
            id = "SJ-2026-0136",
            titulo = "Basura acumulada en área verde",
            lugar = "Plaza Los Presidentes",
            sector = "Población Sur",
            categoria = "Espacio público",
            prioridad = "Baja",
            estado = "Resuelto",
            fechaMillis = fecha(12, 11, 30)
        )
    )

    private fun fecha(dia: Int, hora: Int, minuto: Int): Long =
        Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, dia, hora, minuto, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
}