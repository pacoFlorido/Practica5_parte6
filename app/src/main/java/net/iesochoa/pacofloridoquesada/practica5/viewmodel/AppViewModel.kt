package net.iesochoa.pacofloridoquesada.practica5.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.iesochoa.pacofloridoquesada.practica5.model.Tarea
import net.iesochoa.pacofloridoquesada.practica5.repository.Repository

class AppViewModel(application: Application): AndroidViewModel(application) {
    private val repositorio: Repository
    val tareasLiveData: LiveData<List<Tarea>>
    init {
        Repository(getApplication<Application>().applicationContext)
        //TODO
        repositorio = Repository
        tareasLiveData = repositorio.getAllTareas()
    }

    fun addTarea(tarea: Tarea)= Repository.addTarea(tarea)
    fun delTarea(tarea: Tarea)= Repository.delTarea(tarea)
}