package com.duoc.aplicacionmunicipalidad

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Los ViewModel lanzan corrutinas en el hilo principal de Android, que no existe en las pruebas.
 * Esta regla lo reemplaza por un dispatcher de prueba que ejecuta todo de inmediato.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ReglaDispatcherPrincipal(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
