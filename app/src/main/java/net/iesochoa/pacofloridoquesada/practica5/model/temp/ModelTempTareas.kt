package net.iesochoa.pacofloridoquesada.practica5.model.temp

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import net.iesochoa.pacofloridoquesada.practica5.model.Tarea

object ModelTempTareas {
    // Lista de las tareas
    private val tareas = ArrayList<Tarea>()
    // LivaData para poder observar en nuestras vistas los cambios de esta lista
    private val tareasLiveData = MutableLiveData<List<Tarea>>(tareas)

    private lateinit var application: Application

    // Permite iniciar el objeto como Singleton, es decir, solo existirá una instancia de
    // este objeto en nuestra aplicación.
    operator fun invoke(context: Context){
        this.application = context.applicationContext as Application
    }
}