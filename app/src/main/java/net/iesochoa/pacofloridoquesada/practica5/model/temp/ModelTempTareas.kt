package net.iesochoa.pacofloridoquesada.practica5.model.temp

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.iesochoa.pacofloridoquesada.practica5.model.Tarea
import kotlin.random.Random

object ModelTempTareas {
    // Lista de las tareas.
    private val tareas = ArrayList<Tarea>()
    // LivaData para poder observar en nuestras vistas los cambios de esta lista.
    private val tareasLiveData = MutableLiveData<List<Tarea>>(tareas)
    // Necesario para acceder a los datos.
    private lateinit var application: Application

    /*
     * Permite iniciar el objeto como Singleton, es decir, solo existirá una instancia de
     *  este objeto en nuestra aplicación.
     */
    operator fun invoke(context: Context){
        this.application = context.applicationContext as Application
        iniciaPruebaTareas()
    }
    // Devuelve un LiveData para que no se pueda modificar en capas superiores.
    fun getAllTareas(): LiveData<List<Tarea>> {
        tareasLiveData.value = tareas
        return tareasLiveData
    }

    /**
     * Esta función añadirá una tarea si no existe, y si ya existe la sustituirá.
     * Posteriormente actualizará el LiveData para poder visualizar los cambios.
     */
    fun addTarea(tarea: Tarea) {
        // Devuelve -1 si no se encuentra la tarea.
        val pos = tareas.indexOf(tarea)
        if (pos < 0){
            // Si no existe la añade
            tareas.add(tarea)
        } else {
            // Si existe la sustituye
            tareas.set(pos, tarea)
        }
        tareasLiveData.value = tareas
    }

    fun delTarea(tarea: Tarea){
        tareas.remove(tarea)
        tareasLiveData.value = tareas
    }

    fun iniciaPruebaTareas() {
        val tecnicos = listOf(
            "Pepe Gotero",
            "Sacarino Pómez",
            "Mortadelo Fernández",
            "Filemón López",
            "Zipi Climent",
            "Zape Gómez"
        )
        lateinit var tarea: Tarea
        (1..10).forEach({
            tarea = Tarea(
                (0..4).random(),
                (0..2).random(),
                Random.nextBoolean(),
                (0..2).random(),
                (0..30).random(),
                (0..5).random().toFloat(),
                tecnicos.random(),
                "tarea $it realizada por el técnico \nLorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris consequat ligula et vehicula mattis. Etiam tristique ornare lacinia. Vestibulum lacus magna, dignissim et tempor id, convallis sed augue"
            )
            tareas.add(tarea)
        })
        //actualizamos el LiveData
        tareasLiveData.value = tareas
    }
    fun getTareasFiltroSinPagar(soloSinPagar: Boolean): LiveData<List<Tarea>>{
        tareasLiveData.value = if (soloSinPagar)
            tareas.filter { !it.pagado } as ArrayList<Tarea>
        else
            tareas
        return tareasLiveData
    }
    fun getTareasFiltroEstado(estado: Int): LiveData<List<Tarea>>{
        tareasLiveData.value = when (estado) {
            3 -> tareas
            else -> tareas.filter {it.estado == estado} as ArrayList<Tarea>
        }
        return tareasLiveData
    }
}