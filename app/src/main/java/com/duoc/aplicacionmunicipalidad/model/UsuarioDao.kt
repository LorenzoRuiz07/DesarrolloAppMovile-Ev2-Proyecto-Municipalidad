package com.duoc.aplicacionmunicipalidad.model

import androidx.room.*

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuario WHERE LOWER(rut) = LOWER(:identificador) OR LOWER(correo) = LOWER(:identificador) OR LOWER(nombre) = LOWER(:identificador) LIMIT 1")
    suspend fun buscarPorIdentificador(identificador: String): Usuario?

    @Query("SELECT * FROM usuario WHERE rut = :rut LIMIT 1")
    suspend fun obtenerPorRut(rut: String): Usuario?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(usuario: Usuario)

    @Update
    suspend fun actualizar(usuario: Usuario)

    @Query("DELETE FROM usuario WHERE rut = :rut")
    suspend fun borrarPorRut(rut: String)

    @Query("SELECT COUNT(*) FROM usuario")
    suspend fun contarUsuarios(): Int

    @Query("SELECT * FROM usuario")
    suspend fun obtenerTodos(): List<Usuario>
}
