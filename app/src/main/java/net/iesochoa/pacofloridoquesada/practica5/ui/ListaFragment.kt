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

    private fun borrarTarea(tarea:Tarea){
        AlertDialog.Builder(activity as Context)
            .setTitle(android.R.string.dialog_alert_title)
            //recuerda: todo el texto en string.xml
            .setMessage(getString(R.string.desea_borrar_la_tarea, tarea.id))
            //acción si pulsa si
            .setPositiveButton(android.R.string.ok){v,_->
                //borramos la tarea
                viewModel.delTarea(tarea)
                //cerramos el dialogo
                v.dismiss()
            }
            //accion si pulsa no
            .setNegativeButton(android.R.string.cancel){v,_->v.dismiss()}
            .setCancelable(false)
            .create()
            .show()
    }

    private fun iniciaCRUD(){
        binding.fabAAdirTarea.setOnClickListener{
            val action = ListaFragmentDirections.actionEditar(null)
            findNavController().navigate(action)
        }
        tareasAdapter.onTareaClickListener = object: TareaAdapter.OnTareaClickListener {
            override fun onTareaClick(tarea: Tarea?) {
                val action = ListaFragmentDirections.actionEditar(tarea)
                findNavController().navigate(action)
            }

            override fun onTareaBorrarClick(tarea: Tarea?) {
                borrarTarea(tarea!!)
            }

            override fun onTareaEstadoClick(tarea: Tarea?) {
                if (tarea!!.estado == 2){
                    tarea.estado = 0
                } else {
                    tarea.estado++
                }
            }
        }
    }

    private fun iniciaSwiped(){
        //creamos el evento del Swiper para detectar cuando el usuario desliza un item
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or
                        ItemTouchHelper.RIGHT) {
                //si tenemos que actuar cuando se mueve un item
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                                      direction: Int) {
                    //obtenemos la posición de la tarea a partir del viewholder
                    val tareaDelete=tareasAdapter.listaTareas?.get(viewHolder.adapterPosition)
                    //borramos la tarea. Falta preguntar al usuario si desea borrarla
                    if (tareaDelete != null) {
                         viewModel.delTarea(tareaDelete)

                    }
                }
            }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        //asignamos el evento al RecyclerView
        itemTouchHelper.attachToRecyclerView(binding.rvTareas)
    }


    private fun iniciaRecycledView() {
        //creamos el adaptador
        tareasAdapter = TareaAdapter()

        with(binding.rvTareas) {
            //Creamos el layoutManager
            //layoutManager = LinearLayoutManager(activity)
            //le asignamos el adaptador

            val orientation=resources.configuration.orientation
            layoutManager =if(orientation== Configuration.ORIENTATION_PORTRAIT)
            //Vertical: lista con una colummna
                LinearLayoutManager(activity)
            else//Horizontal: lista con dos columnas
                GridLayoutManager(activity,2)
            adapter = tareasAdapter
        }
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
     * Método que actualiza la lista por si se ha añadido alguna tarea nueva
     */
    private fun actualizaLista(lista: List<Tarea>?) {
        var listaString=""
        lista?.forEach(){
            listaString="$listaString ${it.id}-${it.tecnico}-${it.descripcion}-${if(it.pagado) "pagado" else
                "no pagado"}\n"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentListaBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        iniciaRecycledView()
        iniciaFiltros()
        iniciaCRUD()
        viewModel.tareasLiveData.observe(viewLifecycleOwner, Observer<List<Tarea>>{lista ->
            tareasAdapter.setLista(lista)
        })
        // Indicamos la accion que realizará el boton FabAñadirTarea

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}