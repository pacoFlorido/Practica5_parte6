package net.iesochoa.pacofloridoquesada.practica5.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.iesochoa.pacofloridoquesada.practica5.model.Tarea
import net.iesochoa.pacofloridoquesada.practica5.repository.Repository

class AppViewModel(application: Application): AndroidViewModel(application) {
    private val repositorio: Repository
    val tareasLiveData: LiveData<List<Tarea>>
    val SOLO_SIN_PAGAR = "SOLO_SIN_PAGAR"
    val ESTADO = "ESTADO"
    private val filtrosLiveData by lazy {
        // Creamos un MutableLiveData de los dos datos por los que tenemos que filtrar
        val mutableMap = mutableMapOf<String, Any?>(
            SOLO_SIN_PAGAR to false,
            ESTADO to 3
        )
        MutableLiveData (mutableMap)
    }
    init {
        Repository(getApplication<Application>().applicationContext)
        repositorio = Repository
        tareasLiveData=filtrosLiveData.switchMap{ mapFiltro ->
            val aplicarSinPagar = mapFiltro!![SOLO_SIN_PAGAR] as Boolean
            val estado = mapFiltro[ESTADO] as Int
            //Devuelve el resultado del when
            when {//trae toda la lista de tareas
                (!aplicarSinPagar && (estado == 3)) ->
                    repositorio.getAllTareas()
                //Sólo filtra por ESTADO
                (!aplicarSinPagar && (estado != 3)) ->
                    repositorio.getTareasFiltroEstado(estado)
                //Sólo filtra SINPAGAR
                (aplicarSinPagar && (estado == 3)) ->
                    repositorio.getTareasFiltroSinPagar(aplicarSinPagar)//Filtra por ambos
                else ->
                    repositorio.getTareasFiltrosSinPagarEstado(aplicarSinPagar, estado)
            }
        }
    }
    // Método que añade una tarea
    fun addTarea(tarea: Tarea)= Repository.addTarea(tarea)
    // Método que alimina una tarea
    fun delTarea(tarea: Tarea)= viewModelScope.launch(Dispatchers.IO) {
        Repository.delTarea(tarea)
    }
    // Método que comprueba que opción esta seleccionada para poder filtrar
    fun setSoloSinPagar (soloSinPagar: Boolean) {
        val mapa = filtrosLiveData.value
        mapa!![SOLO_SIN_PAGAR] = soloSinPagar
        filtrosLiveData.value = mapa
    }
    // Método que comprueba que opción esta seleccionada para poder filtrar
    fun setEstado (estado: Int) {
        val mapa = filtrosLiveData.value
        mapa!![ESTADO] = estado
        filtrosLiveData.value = mapa
    }
}