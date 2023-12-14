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
    val PRIORIDAD = "PRIORIDAD"
    private val filtrosLiveData by lazy {
        // Creamos un MutableLiveData de los tres datos por los que tenemos que filtrar
        val mutableMap = mutableMapOf<String, Any?>(
            SOLO_SIN_PAGAR to false,
            ESTADO to 3,
            PRIORIDAD to 3
        )
        MutableLiveData (mutableMap)
    }
    init {
        Repository(getApplication<Application>().applicationContext)
        repositorio = Repository
        tareasLiveData=filtrosLiveData.switchMap{ mapFiltro ->
            val aplicarSinPagar = mapFiltro!![SOLO_SIN_PAGAR] as Boolean
            val estado = mapFiltro[ESTADO] as Int
            val prioridad = mapFiltro[PRIORIDAD] as Int
            //Devuelve el resultado del when
            when {//trae toda la lista de tareas
                (!aplicarSinPagar && (estado == 3) && (prioridad == 3)) ->
                    repositorio.getAllTareas()
                //Sólo filtra por ESTADO
                (!aplicarSinPagar && (estado != 3) && (prioridad == 3)) ->
                    repositorio.getTareasFiltroEstado(estado)
                //Sólo filtra SINPAGAR
                (aplicarSinPagar && (estado == 3) && (prioridad == 3)) ->
                    repositorio.getTareasFiltroSinPagar(aplicarSinPagar)
                //Sólo filtra PRIORIDAD
                (!aplicarSinPagar && (estado == 3) && (prioridad != 3)) ->
                    repositorio.getTareasFiltrosPrioridad(prioridad)
                //Filtra por ESTADO y SINPAGAR
                (aplicarSinPagar && (estado != 3) && (prioridad == 3)) ->
                    repositorio.getTareasFiltrosSinPagarEstado(aplicarSinPagar, estado)
                //Filtra por PRIORIDAD y ESTADO
                (!aplicarSinPagar && (estado != 3) && (prioridad != 3)) ->
                    repositorio.getTareasFiltroPrioridadEstado(prioridad, estado)
                //Filtra por PRIORIDAD y SINPAGAR
                (aplicarSinPagar && (estado == 3) && (prioridad != 3)) ->
                    repositorio.getTareasFiltroPrioridadSinPagar(prioridad, aplicarSinPagar)
                //Filtra por TODOS
                else ->
                    repositorio.getTareasFiltroPrioridadEstadoSinPagar(prioridad,estado,aplicarSinPagar)
            }
        }
    }
    // Método que añade una tarea
    fun addTarea(tarea: Tarea)= viewModelScope.launch(){
        Repository.addTarea(tarea)
    }
    // Método que alimina una tarea
    fun delTarea(tarea: Tarea)= viewModelScope.launch(Dispatchers.IO){
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
    // Método que comprueba que opción está seleccionada para poder filtrar
    fun setPrioridad (prioridad: Int) {
        val mapa = filtrosLiveData.value
        mapa!![PRIORIDAD] = prioridad
        filtrosLiveData.value = mapa
    }
}