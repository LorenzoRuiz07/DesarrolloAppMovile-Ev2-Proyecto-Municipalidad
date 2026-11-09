package com.duoc.aplicacionmunicipalidad.repository

import com.duoc.aplicacionmunicipalidad.model.Usuario
import com.duoc.aplicacionmunicipalidad.model.UsuarioDao

class UsuarioRepository(private val dao: UsuarioDao) {

    suspend fun inicializarUsuariosPorDefecto() {
        if (dao.contarUsuarios() == 0) {
            val usuariosDefecto = listOf(
                Usuario(
                    rut = "12.345.678-5",
                    nombre = "María Pérez Soto",
                    correo = "maria.perez@correo.cl",
                    telefono = "+56 9 1234 5678",
                    direccion = "Carmen 1234, San Joaquín",
                    password = "1234",
                    tipoPerfil = "Residente"
                ),
                Usuario(
                    rut = "11.111.111-1",
                    nombre = "Operador Municipal",
                    correo = "operador@sanjoaquin.cl",
                    telefono = "+56 9 1111 2222",
                    direccion = "Av. Santa Rosa 2600",
                    password = "1234",
                    tipoPerfil = "Operador"
                ),
                Usuario(
                    rut = "22.222.222-2",
                    nombre = "Supervisor Municipal",
                    correo = "supervisor@sanjoaquin.cl",
                    telefono = "+56 9 2222 3333",
                    direccion = "Av. Santa Rosa 2600",
                    password = "1234",
                    tipoPerfil = "Supervisor"
                ),
                Usuario(
                    rut = "33.333.333-3",
                    nombre = "Administrador Sistema",
                    correo = "admin@sanjoaquin.cl",
                    telefono = "+56 9 3333 4444",
                    direccion = "Av. Santa Rosa 2600",
                    password = "1234",
                    tipoPerfil = "Administrador"
                )
            )
            for (u in usuariosDefecto) {
                dao.guardar(u)
            }
        }
    }

    suspend fun buscarUsuario(identificador: String): Usuario? {
        inicializarUsuariosPorDefecto()
        val query = identificador.trim()
        val busquedaDirecta = dao.buscarPorIdentificador(query)
        if (busquedaDirecta != null) return busquedaDirecta

        val limpio = query.replace(".", "").replace("-", "").lowercase()
        val todos = dao.obtenerTodos()
        return todos.firstOrNull { u ->
            val rutLimpio = u.rut.replace(".", "").replace("-", "").lowercase()
            rutLimpio == limpio ||
                    u.correo.lowercase() == query.lowercase() ||
                    (query.lowercase() == "residente" && u.tipoPerfil == "Residente") ||
                    (query.lowercase() == "operador" && u.tipoPerfil == "Operador") ||
                    (query.lowercase() == "supervisor" && u.tipoPerfil == "Supervisor") ||
                    (query.lowercase() == "admin" && u.tipoPerfil == "Administrador")
        }
    }

    suspend fun obtenerPorRut(rut: String): Usuario? {
        return dao.obtenerPorRut(rut)
    }

    suspend fun guardarUsuario(usuario: Usuario) {
        dao.guardar(usuario)
    }

    suspend fun actualizarPerfilUsuario(rutOriginal: String, nuevoUsuario: Usuario) {
        if (rutOriginal.isNotBlank() && rutOriginal != nuevoUsuario.rut) {
            dao.borrarPorRut(rutOriginal)
        }
        dao.guardar(nuevoUsuario)
    }
}
