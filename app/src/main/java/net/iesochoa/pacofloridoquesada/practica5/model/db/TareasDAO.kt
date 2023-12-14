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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTarea(tarea: Tarea)
    @Delete
    suspend fun delTarea(tarea: Tarea)

    @Query("SELECT * FROM tareas ")
    fun getAllTareas(): LiveData<List<Tarea>>
    @Query("SELECT * FROM tareas WHERE pagado = :soloSinPagar")
    fun getTareasFiltroSinPagar(soloSinPagar: Boolean): LiveData<List<Tarea>>
    @Query("SELECT * FROM tareas WHERE estado = :estado")
    fun getTareasFiltroEstado(estado: Int) : LiveData<List<Tarea>>
    @Query("SELECT * FROM tareas WHERE (pagado = :soloSinPagar) AND (estado = :estado)")
    fun getTareasFiltroSinPagarEstado(soloSinPagar: Boolean, estado: Int)
}