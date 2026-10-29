package com.duoc.aplicacionmunicipalidad.model

import androidx.room.*
//consultas
@Dao
interface SesionDao {
    @Query("SELECT * FROM sesion WHERE id = 1")
    suspend fun obtener(): Sesion?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(sesion: Sesion)

    @Query("DELETE FROM sesion")
    suspend fun borrar()
}