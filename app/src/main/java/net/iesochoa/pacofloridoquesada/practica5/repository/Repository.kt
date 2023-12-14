package net.iesochoa.pacofloridoquesada.practica5.repository

import android.app.Application
import android.content.Context
import net.iesochoa.pacofloridoquesada.practica5.model.Tarea
import net.iesochoa.pacofloridoquesada.practica5.model.db.TareasDAO
import net.iesochoa.pacofloridoquesada.practica5.model.db.TareasDataBase
import net.iesochoa.pacofloridoquesada.practica5.model.temp.ModelTempTareas

object Repository {
    // Instancia del Modelo
    private lateinit var modelTareas: TareasDAO
    // Constext neccesario para recuperar datos
    private lateinit var application: Application

    operator fun invoke(context: Context){
        this.application = context.applicationContext as Application
        //ModelTempTareas(application)
        //modelTareas = ModelTempTareas
        // Iniciamos la BD
        modelTareas = TareasDataBase.getDatabase(application).tareasDao()
    }

    suspend fun addTarea(tarea: Tarea)= modelTareas.addTarea(tarea)
    suspend fun delTarea(tarea: Tarea)= modelTareas.delTarea(tarea)
    fun getAllTareas()= modelTareas.getAllTareas()
    fun getTareasFiltroSinPagar (soloSinPagar: Boolean) = modelTareas.getTareasFiltroSinPagar(soloSinPagar)
    fun getTareasFiltroEstado (estado: Int) = modelTareas.getTareasFiltroEstado(estado)
    fun getTareasFiltrosSinPagarEstado (soloSinPagar: Boolean, estado: Int) = modelTareas.getTareasFiltroSinPagarEstado(soloSinPagar,estado)
    fun getTareasFiltrosPrioridad(prioridad: Int) = modelTareas.getTareasFiltroPrioridad(prioridad)
    fun getTareasFiltroPrioridadSinPagar(prioridad: Int, soloSinPagar: Boolean) = modelTareas.getTareasFiltroPrioridadSinPagar(prioridad, soloSinPagar)
    fun getTareasFiltroPrioridadEstado(prioridad: Int, estado: Int) = modelTareas.getTareasFiltroPrioridadEstado(prioridad, estado)
    fun getTareasFiltroPrioridadEstadoSinPagar(prioridad: Int, estado: Int, soloSinPagar: Boolean) = modelTareas.getTareasFiltroPrioridadEstadoSinPagar(prioridad, estado, soloSinPagar)
}