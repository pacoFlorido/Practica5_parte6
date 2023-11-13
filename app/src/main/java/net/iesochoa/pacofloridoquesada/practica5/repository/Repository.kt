package net.iesochoa.pacofloridoquesada.practica5.repository

import android.app.Application
import android.content.Context
import net.iesochoa.pacofloridoquesada.practica5.model.Tarea
import net.iesochoa.pacofloridoquesada.practica5.model.temp.ModelTempTareas

object Repository {
    private lateinit var modelTareas: ModelTempTareas
    private lateinit var application: Application

    operator fun invoke(context: Context){
        this.application = context.applicationContext as Application
        ModelTempTareas(application)
        modelTareas = ModelTempTareas
    }

    fun addTarea(tarea: Tarea)= modelTareas.addTarea(tarea)
    fun delTarea(tarea: Tarea)= modelTareas.delTarea(tarea)
    fun getAllTareas()= modelTareas.getAllTareas()
    fun getTareasFiltroSinPagar (soloSinPagar: Boolean) = modelTareas.getTareasFiltroSinPagar(soloSinPagar)
}