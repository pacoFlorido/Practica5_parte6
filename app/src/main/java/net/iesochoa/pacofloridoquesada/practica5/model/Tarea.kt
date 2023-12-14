package net.iesochoa.pacofloridoquesada.practica5.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "tareas")
@Parcelize
data class Tarea(
    // Constructor con ID
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var categoria: Int,
    var prioridad: Int,
    var pagado: Boolean,
    var estado: Int,
    var horasTrabajo: Int,
    var valoracionCliente: Float,
    var tecnico: String,
    var descripcion: String
): Parcelable
{
    // Segundo constructor que generá id automático
    constructor(
        categoria: Int,
        prioridad: Int,
        pagado: Boolean,
        estado: Int,
        horasTrabajo: Int,
        valoracionCliente: Float,
        tecnico: String,
        descripcion: String
        ): this(null,categoria,prioridad,pagado,
        estado,horasTrabajo,valoracionCliente,tecnico,descripcion){}
    companion object {
        var idContador = 1L
        private fun generateId(): Long {
            return idContador++
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other is Tarea) && (this.id == other?.id)
    }


}
