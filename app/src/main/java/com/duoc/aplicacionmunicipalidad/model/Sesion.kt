package com.duoc.aplicacionmunicipalidad.model

import androidx.room.Entity
import androidx.room.PrimaryKey
//la tabla
@Entity(tableName = "sesion")
data class Sesion(
    @PrimaryKey val id: Int = 1,
    val perfil: String
)