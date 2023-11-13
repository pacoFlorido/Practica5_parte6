package net.iesochoa.pacofloridoquesada.practica5.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import net.iesochoa.pacofloridoquesada.practica5.R
import net.iesochoa.pacofloridoquesada.practica5.databinding.FragmentTareaBinding
import net.iesochoa.pacofloridoquesada.practica5.model.Tarea
import net.iesochoa.pacofloridoquesada.practica5.viewmodel.AppViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TareaFragment : Fragment() {

    private var _binding: FragmentTareaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val args: TareaFragmentArgs by navArgs()
    private val viewModel: AppViewModel by activityViewModels()
    val esNuevo by lazy { args.tarea == null }

    private fun iniciaTarea(tarea: Tarea){
        binding.spCategorias.setSelection(tarea.categoria)
        binding.spPrioridad.setSelection(tarea.prioridad)
        binding.swPagado.isChecked = tarea.pagado
        binding.rgEstado.check(
            when (tarea.estado){
                0 -> R.id.rbAbierta
                1 -> R.id.rbEnCurso
                else -> R.id.rbCerrada
            }
        )
        binding.sbHorasTrabajadas.progress = tarea.horasTrabajo
        binding.rtbValoracion.rating = tarea.valoracionCliente
        binding.tietTecnico.setText(tarea.tecnico)
        binding.etDescripcion.setText(tarea.descripcion)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Tarea ${tarea.id}"
    }

    /**
     * Muestra el mensaje de error
     */
    private fun muestraMensajeError() {
        Snackbar.make(binding.clytTarea, R.string.mensaje_error_guardar, Snackbar.LENGTH_LONG)
            // En este caso no vamos a implementar ninguna acción adicional
            .setAction("Action",null).show()
    }

    /**
     * Inicia el FabGuardar
     */
    private fun iniciarFabGuardar() {
        binding.fabGuardar.setOnClickListener{
            if (binding.tietTecnico.text.toString().isEmpty() || binding.etDescripcion.text.toString().isEmpty())
                muestraMensajeError()
            else
                guardaTarea()
        }
    }
    /**
     * Guardar una Tarea
     */
    private fun guardaTarea() {
        //recuperamos los datos
        val categoria=binding.spCategorias.selectedItemPosition
        val prioridad=binding.spPrioridad.selectedItemPosition
        val pagado=binding.swPagado.isChecked
        val estado=when (binding.rgEstado.checkedRadioButtonId) {
            R.id.rbAbierta -> 0
            R.id.rbEnCurso -> 1
            else -> 2
        }
        val horas=binding.sbHorasTrabajadas.progress
        val valoracion=binding.rtbValoracion.rating
        val tecnico=binding.tietTecnico.text.toString()
        val descripcion=binding.etDescripcion.text.toString()
        //creamos la tarea: si es nueva, generamos un id, en otro caso le asignamos su id
        val tarea = if(esNuevo)

            Tarea(categoria,prioridad,pagado,estado,horas,valoracion,tecnico,descripcion)
        else

            Tarea(args.tarea!!.id,categoria,prioridad,pagado,estado,horas,valoracion,tecnico,descripcion)
        //guardamos la tarea desde el viewmodel
        viewModel.addTarea(tarea)
        //salimos de editarFragment
        findNavController().popBackStack()
    }
    /**
     * Inicia el ScrollBar de Horas Trabajadas
     */
    private fun iniciarSbHorasTrabajadas(){
        binding.sbHorasTrabajadas.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, progreso: Int, p2: Boolean) {
                binding.tvHorasTrabajadas.text = getString(R.string.horas_tabajadas,progreso)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                /*
                Este código lo he añadido para indicar que la tarea esta cerrada si se ha
                llegado al número máximo de horas, o que esta abierta si no ha trabajado
                ninguna hora, o que esta en curso si lleva alguna hora trabajada.
                Es extra, si quieres lo puedes comentar.
                 */
                if (binding.sbHorasTrabajadas.max == seekBar?.progress){
                    binding.rgEstado.check(R.id.rbCerrada)
                } else if (0 == seekBar?.progress){
                    binding.rgEstado.check(R.id.rbAbierta)
                } else {
                    binding.rgEstado.check(R.id.rbEnCurso)
                }
            }
        })
        // Inicializamos el valor del SeekBar, horas trabajadas
        binding.sbHorasTrabajadas.progress = 0
        binding.tvHorasTrabajadas.text = getString(R.string.horas_tabajadas,0)
    }
    /**
     * Inicia el Switch de Pagado
     */
    private fun iniciarSwPagado(){
        binding.swPagado.setOnCheckedChangeListener{_, isChecked ->
            // Elegimos la imagen dependiendo de como este el switch
            val imagen = if (isChecked) R.drawable.ic_pagado
                         else R.drawable.ic_no_pagado
            // Asignamos la imagen dependiendo de como este el switch
            binding.ivPagado.setImageResource(imagen)
        }
        // Indicamos el valor por defecto
        binding.swPagado.isChecked = false
        binding.ivPagado.setImageResource(R.drawable.ic_no_pagado)
    }

    /**
     * Iniciar RadioGroup del Estado
     */
    private fun iniciarRgEstado(){
        // Creamos el evento CheckerChange para cambiar las imagenes al seleccionar
        // un RadioButton u otro
        binding.rgEstado.setOnCheckedChangeListener{_, checkedId ->
            // Hacemo un when para coger la imagen adecuada en cada caso
            val imagen = when (checkedId){
                R.id.rbAbierta -> R.drawable.ic_abierto
                R.id.rbEnCurso -> R.drawable.ic_en_curso
                else -> R.drawable.ic_cerrado
            }
            binding.ivEstado.setImageResource(imagen)

        }
        // Lo iniciamo en abierto
        binding.rgEstado.check(R.id.rbAbierta)
    }

    /**
     * Inicia el Spinner de Prioridad
     */
    private fun iniciarSpPrioridad(){
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.prioridad,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spPrioridad.adapter = adapter

            // Cuando se seleccione prioridad Alta cambiamos el color del fondo
            binding.spPrioridad.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?,v: View?,posicion: Int, id: Long) {
                    if (posicion == 2){
                        // Si el item seleccionado es "alta", que se encuentra en la tercera posicion (2 del array), se cambiará el fondo al color que hayamos escogido
                        binding.clytTarea.setBackgroundColor(requireContext().getColor(R.color.prioridad_alta))
                    } else {
                        // Si la prioridad no es "alta" quitamos el color de fondo
                        binding.clytTarea.setBackgroundColor(Color.TRANSPARENT)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // Si no hay ningún elemento seleccionado nos pondremos el fondo transparente.
                    binding.clytTarea.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }
    }

    /**
     * Inicia el Spinner de Categorias
     */
    private fun iniciarSpCategorias(){
        //Creamos el Adapter
        ArrayAdapter.createFromResource(
            requireContext(),
            // Le pasamos los datos del array que hemos creado en strings.xml
            R.array.categoria,
            // Le indicamos el primer tipo de layout
            android.R.layout.simple_spinner_item

        ).also { adapter ->
            // Asignamos el segundo layout
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Una vez creado el adapter, lo asociamos con el adapter de spCategorias
            binding.spCategorias.adapter = adapter

            /* Creamos el evento para que cuando un item de spCategorias se seleccione nos muestre el
             * mensaje  */

            binding.spCategorias.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, v: View?, posicion: Int, id: Long) {
                    // Cogemos el string del item seleccionado
                    val categoriaSeleccionada = binding.spCategorias.getItemAtPosition(posicion)
                    // Lo ponemos en el mensaje que hemos creado en strings.xml
                    val mensaje = getString(R.string.mensaje_categoria, categoriaSeleccionada)

                    // Mostramos el mensaje mediante el SnackBar
                    Snackbar.make(binding.clytTarea, mensaje, Snackbar.LENGTH_LONG)
                        // En este caso no vamos a implementar ninguna acción adicional
                        .setAction("Action",null).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // En caso de que ninguno fuera seleccionado nos mostraría este mensaje.
                    Snackbar.make(binding.clytTarea, "Ninguna categoría seleccionada", Snackbar.LENGTH_LONG)
                        .setAction("Action",null).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTareaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_TareaFragment_to_ListaFragment)
        }

         */

        iniciarSpCategorias()
        iniciarSpPrioridad()
        iniciarSwPagado()
        iniciarRgEstado()
        iniciarSbHorasTrabajadas()
        iniciarFabGuardar()

        if (esNuevo)
            (requireActivity() as AppCompatActivity).supportActionBar?.title = "Nueva tarea"
        else
            iniciaTarea(args.tarea!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}