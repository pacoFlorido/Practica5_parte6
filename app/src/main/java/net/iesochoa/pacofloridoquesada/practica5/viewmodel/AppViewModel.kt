package net.iesochoa.pacofloridoquesada.practica5.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import net.iesochoa.pacofloridoquesada.practica5.model.Tarea
import net.iesochoa.pacofloridoquesada.practica5.repository.Repository

class AppViewModel(application: Application): AndroidViewModel(application) {
    private val repositorio: Repository
    val tareasLiveData: LiveData<List<Tarea>>
    private val soloSinPagarLiveData = MutableLiveData<Boolean>(false)
    private val estadoLiveData = MutableLiveData<Int>(3)
    init {
        Repository(getApplication<Application>().applicationContext)
        repositorio = Repository
        //tareasLiveData = soloSinPagarLiveData.switchMap { soloSinPagar -> Repository.getTareasFiltroSinPagar(soloSinPagar) }
        tareasLiveData = estadoLiveData.switchMap { estado -> Repository.getTareasFiltroEstado(estado) }
    }

    fun addTarea(tarea: Tarea)= Repository.addTarea(tarea)
    fun delTarea(tarea: Tarea)= Repository.delTarea(tarea)
    fun setSoloSinPagar (soloSinPagar: Boolean) {soloSinPagarLiveData.value = soloSinPagar}
    fun setEstado (estado: Int) {estadoLiveData.value = estado}
}