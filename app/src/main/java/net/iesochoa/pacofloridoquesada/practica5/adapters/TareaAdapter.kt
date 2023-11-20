package net.iesochoa.pacofloridoquesada.practica5.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.iesochoa.pacofloridoquesada.practica5.R
import net.iesochoa.pacofloridoquesada.practica5.databinding.ItemTareaBinding
import net.iesochoa.pacofloridoquesada.practica5.model.Tarea

class TareaAdapter():
    RecyclerView.Adapter<TareaAdapter.TareaViewHolder>()
{
    lateinit var listaTareas: List<Tarea>
    var onTareaClickListener: OnTareaClickListener?=null

    fun setLista(lista: List<Tarea>){
        listaTareas = lista
        notifyDataSetChanged()
    }
    inner class TareaViewHolder(val binding: ItemTareaBinding)
        : RecyclerView.ViewHolder(binding.root){
        init {
            //inicio del click de icono borrar
            binding.ivBorrarTarea.setOnClickListener(){
                //recuperamos la tarea de la lista
                val tarea=listaTareas.get(this.adapterPosition)
                //llamamos al evento borrar que estará definido en el fragment
                onTareaClickListener?.onTareaBorrarClick(tarea)
            }
            //inicio del click sobre el Layout(constraintlayout)
            binding.root.setOnClickListener(){
                val tarea=listaTareas.get(this.adapterPosition)
                onTareaClickListener?.onTareaClick(tarea)
            }
            binding.ivEstadoItem.setOnClickListener(){
                val tarea = listaTareas.get(this.adapterPosition)
                onTareaClickListener?.onTareaEstadoClick(tarea)
                binding.ivEstadoItem.setImageResource(
                    when (tarea.estado) {
                        0 -> R.drawable.ic_abierto
                        1 -> R.drawable.ic_en_curso
                        else -> R.drawable.ic_cerrado
                    }
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val binding = ItemTareaBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return TareaViewHolder(binding)
    }

    override fun getItemCount(): Int = listaTareas?.size?:0

    override fun onBindViewHolder(tareaViewHolder: TareaViewHolder, pos: Int) {
        //Nos pasan la posición del item a mostrar en el viewHolder
        with(tareaViewHolder) {
            //cogemos la tarea a mostrar y rellenamos los campos del ViewHolder
            with(listaTareas!!.get(pos)) {
                binding.tvIdTarea.text = id.toString()
                binding.tvDescripcionItem.text = descripcion
                binding.tvTecnico.text = tecnico
                binding.rbValoracionItem.rating = valoracionCliente
                //mostramos el icono en función del estado
                binding.ivEstadoItem.setImageResource(
                    when (estado) {
                        0 -> R.drawable.ic_abierto
                        1 -> R.drawable.ic_en_curso
                        else -> R.drawable.ic_cerrado
                    }
                )
                //cambiamos el color de fondo si la prioridad es alta
                binding.cvItem.setBackgroundResource(
                    if (prioridad == 2)//prioridad alta
                        R.color.prioridad_alta
                    else
                        Color.TRANSPARENT
                )
            }
        }
    }
    interface OnTareaClickListener {
        fun onTareaClick(tarea: Tarea?)
        fun onTareaBorrarClick(tarea: Tarea?)
        fun onTareaEstadoClick(tarea: Tarea?)
    }
}