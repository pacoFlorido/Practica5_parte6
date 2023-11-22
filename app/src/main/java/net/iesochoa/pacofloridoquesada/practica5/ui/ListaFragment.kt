package net.iesochoa.pacofloridoquesada.practica5.ui

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.iesochoa.pacofloridoquesada.practica5.R
import net.iesochoa.pacofloridoquesada.practica5.adapters.TareaAdapter
import net.iesochoa.pacofloridoquesada.practica5.databinding.FragmentListaBinding
import net.iesochoa.pacofloridoquesada.practica5.model.Tarea
import net.iesochoa.pacofloridoquesada.practica5.viewmodel.AppViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ListaFragment : Fragment() {

    private var _binding: FragmentListaBinding? = null

    private val binding get() = _binding!!

    private val viewModel: AppViewModel by activityViewModels()
    lateinit var tareasAdapter: TareaAdapter

    /**
     * Función que borra una tarea mostrando un dialogo de confirmación antes
     */
    private fun borrarTarea(tarea:Tarea){
        // Dialogo que permite al usuario confirmar el borrado de la tarea o cancelar la acción
        AlertDialog.Builder(activity as Context)
            .setTitle(android.R.string.dialog_alert_title)
            .setMessage(getString(R.string.desea_borrar_la_tarea, tarea.id))
            // Acción si pulsa si
            .setPositiveButton(android.R.string.ok){v,_->
                // Borramos la tarea
                viewModel.delTarea(tarea)
                // Cerramos el dialogo
                v.dismiss()
            }
            // Accion si pulsa no
            .setNegativeButton(android.R.string.cancel){v,_->v.dismiss()}
            .setCancelable(false)
            .create()
            .show()
    }

    /**
     * Función que inicia las acciones que tendremos en esta activity
     */
    private fun iniciaCRUD(){
        binding.fabAAdirTarea.setOnClickListener{
            val action = ListaFragmentDirections.actionEditar(null)
            findNavController().navigate(action)
        }
        tareasAdapter.onTareaClickListener = object: TareaAdapter.OnTareaClickListener {
            override fun onTareaClick(tarea: Tarea?) {
                // Cuando se pulsa en un sitio de la tarea que no sea la papelera ni el estado,
                // se abrira la tarea
                val action = ListaFragmentDirections.actionEditar(tarea)
                findNavController().navigate(action)
            }

            override fun onTareaBorrarClick(tarea: Tarea?) {
                // Cuando pulsamos sobre la papelera, llamamos al método que borrará la tarea
                borrarTarea(tarea!!)
            }

            override fun onTareaEstadoClick(tarea: Tarea?) {
                // Cuando clicamos sobre la imagen del estado de la tarea,
                // el estado de la tarea cambiará  al siguiente .
                if (tarea!!.estado == 2){
                    tarea.estado = 0
                } else {
                    tarea.estado++
                }
            }
        }
    }

    /**
     * Función que inicia la acción de deslizar para eliminar
     */
    private fun iniciaSwiped(){
        // Creamos el evento del Swiper para detectar cuando el usuario desliza un item
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or
                        ItemTouchHelper.RIGHT) {
                // Si tenemos que actuar cuando se mueve un item
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                                      direction: Int) {
                    // Obtenemos la posición de la tarea a partir del viewholder
                    val tareaDelete=tareasAdapter.listaTareas?.get(viewHolder.adapterPosition)
                    // Borramos la tarea.
                    if (tareaDelete != null) {
                        val posicion = viewHolder.adapterPosition
                        // Mostramos el diálogo para saber si el usuario quiere eliminar o no la tarea
                        AlertDialog.Builder(activity as Context)
                            .setTitle(android.R.string.dialog_alert_title)
                            .setMessage(getString(R.string.desea_borrar_la_tarea, tareaDelete.id))
                            // Acción si pulsa si
                            .setPositiveButton(android.R.string.ok){v,_->
                                // Borramos la tarea
                                viewModel.delTarea(tareaDelete)
                                // Cerramos el dialogo
                                v.dismiss()
                            }
                            // Accion si pulsa no
                            .setNegativeButton(android.R.string.cancel){v,_->v.dismiss()}
                            .setCancelable(false)
                            .create()
                            .show()
                        tareasAdapter.notifyItemChanged(posicion)
                    }
                }
            }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        // Asignamos el evento al RecyclerView
        itemTouchHelper.attachToRecyclerView(binding.rvTareas)
    }

    /**
     * Funcion que inicia el RecycledView con las tareas creadas
     */
    private fun iniciaRecycledView() {
        // Creamos el adaptador
        tareasAdapter = TareaAdapter()

        with(binding.rvTareas) {
            // Obtenemos la orientación actual del movil
            val orientation=resources.configuration.orientation
            layoutManager =if(orientation== Configuration.ORIENTATION_PORTRAIT)
            // Si está en Vertical: Lista de tareas con una colummna
                LinearLayoutManager(activity)
            else
                // Si está en Horizontal: Lista de tareas con dos columnas
                GridLayoutManager(activity,2)
            adapter = tareasAdapter
        }
        // Iniciamos el Swiped en ambos casos
        iniciaSwiped()
    }

    /**
     * Método que inicia ambos filtros
     */
    private fun iniciaFiltros() {
        binding.rgFiltroEstados.setOnCheckedChangeListener() {_, checkedId->
           when (checkedId) {
               R.id.rbFiltroAbierta -> viewModel.setEstado(0)
               R.id.rbFiltroEnCurso -> viewModel.setEstado(1)
               R.id.rbFIltroCerrada -> viewModel.setEstado(2)
               else -> viewModel.setEstado(3)
           }
        }
        binding.swSinPagar.setOnCheckedChangeListener(){_, isChecked ->
            viewModel.setSoloSinPagar(isChecked)
        }
    }

    /**
     * Método que actualiza la lista por si se ha añadido alguna tarea nueva sin RecicledView
     *
    private fun actualizaLista(lista: List<Tarea>?) {
        var listaString=""
        lista?.forEach(){
            listaString="$listaString ${it.id}-${it.tecnico}-${it.descripcion}-${if(it.pagado) "pagado" else
                "no pagado"}\n"
        }
    }
    */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentListaBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Iniciamos el RecicledView
        iniciaRecycledView()
        // Iniciamos los Filtros
        iniciaFiltros()
        // Iniciamos el CRUD
        iniciaCRUD()
        // Cargamos las tareas en el RecicledView
        viewModel.tareasLiveData.observe(viewLifecycleOwner, Observer<List<Tarea>>{lista ->
            tareasAdapter.setLista(lista)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}