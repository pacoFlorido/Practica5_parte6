package net.iesochoa.pacofloridoquesada.practica5.model.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.iesochoa.pacofloridoquesada.practica5.model.Tarea

@Dao
interface TareasDAO {
    // Función que añade una tarea o sustituye la misma si se modifica una existente
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTarea(tarea: Tarea)
    // Función que elimina una tarea
    @Delete
    suspend fun delTarea(tarea: Tarea)

    // Métodos que obtienen las tareas dependiendo de que filtros esten activos.
    @Query("SELECT * FROM tareas ")
    fun getAllTareas(): LiveData<List<Tarea>>
    @Query("SELECT * FROM tareas WHERE pagado = :soloSinPagar")
    fun getTareasFiltroSinPagar(soloSinPagar: Boolean): LiveData<List<Tarea>>
    @Query("SELECT * FROM tareas WHERE estado = :estado")
    fun getTareasFiltroEstado(estado: Int): LiveData<List<Tarea>>
    @Query("SELECT * FROM tareas WHERE (pagado = :soloSinPagar) AND (estado = :estado)")
    fun getTareasFiltroSinPagarEstado(soloSinPagar: Boolean, estado: Int): LiveData<List<Tarea>>
    @Query("SELECT * FROM tareas WHERE prioridad = :prioridad")
    fun getTareasFiltroPrioridad(prioridad: Int): LiveData<List<Tarea>>
    @Query("SELECT * FROM tareas WHERE (prioridad = :prioridad) AND (pagado = :soloSinPagar)")
    fun getTareasFiltroPrioridadSinPagar(prioridad: Int, soloSinPagar: Boolean): LiveData<List<Tarea>>
    @Query("SELECT * FROM tareas WHERE (prioridad = :prioridad) AND (estado = :estado)")
    fun getTareasFiltroPrioridadEstado(prioridad: Int, estado: Int): LiveData<List<Tarea>>
    @Query("SELECT * FROM tareas WHERE (prioridad = :prioridad) AND (estado = :estado) AND (pagado = :soloSinPagar)")
    fun getTareasFiltroPrioridadEstadoSinPagar(prioridad: Int, estado: Int, soloSinPagar: Boolean): LiveData<List<Tarea>>
}