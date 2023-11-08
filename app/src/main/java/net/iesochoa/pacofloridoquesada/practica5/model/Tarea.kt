package net.iesochoa.pacofloridoquesada.practica5.model

data class Tarea(
    // Constructor con ID
    var id: Long? = null,
    var categoria: Int,
    var prioridad: Int,
    var pagado: Boolean,
    var estado: Int,
    var horasTrabajo: Int,
    var valoracionCliente: Float,
    var tecnico: String,
    var descripcion: String
)
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
        ): this(
        generateId(),
        categoria,
        prioridad,
        pagado,
        estado,
        horasTrabajo,
        valoracionCliente,
        tecnico,
        descripcion){}
    companion object {
        var idContador = 1L
        private fun generateId(): Long {
            return idContador++;
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other is Tarea) && (this.id == other?.id)
    }


}
