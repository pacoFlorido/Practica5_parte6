package net.iesochoa.pacofloridoquesada.practica5.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import net.iesochoa.pacofloridoquesada.practica5.R
import net.iesochoa.pacofloridoquesada.practica5.databinding.FragmentTareaBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TareaFragment : Fragment() {

    private var _binding: FragmentTareaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}