\# Seguridad comunal – Municipalidad de San Joaquín



App Android (Kotlin, Jetpack Compose, MVVM) para reportar y dar seguimiento a requerimientos de seguridad comunal.



\## Integrantes

\- Antonia Inostroza

\- Millaray Peñaloza

\- Lorenzo Ruiz

\- Equipo AMSJ DSY1105



\## Funcionalidades

\- Inicio de sesión y registro de usuarios

\- Crear, ver, seguir, editar y eliminar requerimientos (CRUD completo)

\- Gestión de requerimientos por operador, supervisor y administrador

\- Perfil de usuario con foto

\- Puntos de seguridad (Carabineros, Bomberos, Salud) desde API externa

\- Recursos nativos: cámara y galería



\## Endpoints

\*\*Microservicio propio (Spring Boot + MySQL)\*\*, repositorio `municipalidad-api`:

\- POST /auth/login · POST /auth/registro

\- GET/PUT /usuarios/{rut}

\- GET/POST /requerimientos · GET /requerimientos/{id}

\- PUT /requerimientos/{id}/gestion · DELETE /requerimientos/{id}



\*\*API externa (Xano):\*\*

\- GET https://x8ki-letl-twmt.n7.xano.io/api:mvyYdc1k/puntos\_seguridad



\## Cómo ejecutar

1\. Backend: `docker compose up --build` (puerto 8080).

2\. Abrir el proyecto en Android Studio y ejecutar en el emulador (usa 10.0.2.2:8080).

3\. Usuario de prueba: 12.345.678-5 / 123456.

4\. APK firmado: `.\\gradlew.bat assembleRelease` con `keystore/municipalidad.jks` (alias municipalidad).

