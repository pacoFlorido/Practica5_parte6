package net.iesochoa.pacofloridoquesada.practica5.ui

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
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
class ListaFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

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
                binding.rgFiltroEstados.check(R.id.rbFiltroTodos)
                // Cerramos el dialogo
                v.dismiss()
            }
            // Accion si pulsa no
            .setNegativeButton(android.R.string.cancel){v,_->v.dismiss()}
            .setCancelable(false)
            .create()
            .show()
    }
    fun obtenColorPreferencias():Int{
        //cogemos el primer color si no hay ninguno seleccionado
        val
                colorPorDefecto=resources.getStringArray(R.array.color_values)[0]
        //recuperamos el color actual
        val color=
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getString(MainActivity.PREF_COLOR_PRIORIDAD,colorPorDefecto)
        return Color.parseColor(color)
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
                //Cambio de imagen en tarea
                if (tarea != null) {
                    // cambiamos el estado
                    val tareaActu = tarea.copy(estado = (tarea.estado + 1) % 3)
                    //la sustituimos
                    viewModel.addTarea(tareaActu)
                    binding.rgFiltroEstados.check(R.id.rbFiltroTodos)
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
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
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
                                binding.rgFiltroEstados.check(R.id.rbFiltroTodos)
                                // Cerramos el dialogo
                                v.dismiss()
                            }
                            // Accion si pulsa no
                            .setNegativeButton(android.R.string.cancel){v,_->
                                tareasAdapter.notifyItemChanged(posicion)
                                v.dismiss()
                            }
                            .setCancelable(false)
                            .create()
                            .show()
                    }
                }
            }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        // Asignamos el evento al RecyclerView
        itemTouchHelper.attachToRecyclerView(binding.rvTareas)
    }

    private fun compruebaAviso(){
        val aviso = PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getBoolean(MainActivity.PREF_AVISO_NUEVAS,false)
    }

    /**
     * Funcion que inicia el RecycledView con las tareas creadas
     */
    private fun iniciaRecycledView() {
        // Creamos el adaptador
        tareasAdapter = TareaAdapter()
        tareasAdapter.colorPrioridadAlta = obtenColorPreferencias()
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
     * Método que inicia todos los filtros
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

        /**
         * Iniciamos el Spinner para filtrar
         */
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.filtro_prioridad,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spFiltroPrioridad.adapter = adapter

            binding.spFiltroPrioridad.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, v: View?, posicion: Int, id: Long) {
                    when (posicion){
                        0 -> viewModel.setPrioridad(posicion)
                        1 -> viewModel.setPrioridad(posicion)
                        2 -> viewModel.setPrioridad(posicion)
                        3 -> viewModel.setPrioridad(posicion)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
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
    override fun onResume() {
        super.onResume()

        PreferenceManager.getDefaultSharedPreferences(requireContext()).registerOnSharedPreferenceChangeListener(this)
    }
    override fun onPause() {
        super.onPause()

        PreferenceManager.getDefaultSharedPreferences(requireContext()).unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences:
                                           SharedPreferences?, key: String?) {
        if (key == MainActivity.PREF_COLOR_PRIORIDAD) {
            //si cambia el color, actualizamos la lista
            tareasAdapter.actualizaRecyclerColor(obtenColorPreferencias())
        }
    }

}