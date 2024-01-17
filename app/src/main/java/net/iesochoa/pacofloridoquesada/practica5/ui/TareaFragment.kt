package net.iesochoa.pacofloridoquesada.practica5.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import net.iesochoa.pacofloridoquesada.practica5.R
import net.iesochoa.pacofloridoquesada.practica5.databinding.FragmentTareaBinding
import net.iesochoa.pacofloridoquesada.practica5.model.Tarea
import net.iesochoa.pacofloridoquesada.practica5.viewmodel.AppViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

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

    val esNuevo by lazy { args.tarea == null } // Si la tarea no esta en la lista es porque es nueva

    // Uri foto de la tarea
    var uriFoto = ""

    private val PERMISOS_REQUERIDOS=when {
        //no se la causa pero en el emulador no solicita este permiso en la versión 34
        // Build.VERSION.SDK_INT >= 34 ->
        //Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        Build.VERSION.SDK_INT >= 33 -> Manifest.permission.READ_MEDIA_IMAGES
        else -> Manifest.permission.READ_EXTERNAL_STORAGE
    }

    /**
     * Función que comprueba los permisos.
     */
    fun permisosAceptados() = ContextCompat.checkSelfPermission(
        requireContext(),
        PERMISOS_REQUERIDOS
    ) == PackageManager.PERMISSION_GRANTED

    // Petición de foto de la galería Versión <33
    private val solicitudFotoGalleryMenorV33 = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            //uri de la foto elegida
            val uri = result.data?.data
            val uriCopia = saveBitmapImage(loadFromUri(uri)!!)
            //mostramos la foto
            binding.ivFotoTarea.setImageURI(uriCopia)
            //guardamos la uri
            uriFoto = uriCopia?.toString() ?: ""
        }
    }

    // Petición de foto de la galería version >=33
    private val solicitudFotoGalleryV33 = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            //uri de la foto elegida
            // val uri = result.data?.data
            val uriCopia = saveBitmapImage(loadFromUri(uri)!!)
            //mostramos la foto
            binding.ivFotoTarea.setImageURI(uriCopia)
            //guardamos la uri
            uriFoto = uriCopia?.toString() ?: ""
        }
    }

    /**
     * Método que busca la foto mediante la URI de la misma
     */
    private fun buscarFoto() {
        if (Build.VERSION.SDK_INT >= 33)
            solicitudFotoGalleryV33.launch(PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly))
        else {
            val intent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            solicitudFotoGalleryMenorV33.launch(intent)
        }
    }

    /**
     * Método que muestra al usuario el mensaje para informar sobre los permisos
     * que tiene que aceptar para que funcione la asignación de la foto a una tarea.
     */
    fun explicarPermisos() {
        AlertDialog.Builder(requireContext())
            .setTitle(android.R.string.dialog_alert_title)
            .setMessage(getString(R.string.es_necesario_el_permiso_de_lectura_de_fichero_para_mostrar_una_foto_desea_aceptar_los_permisos))
        //acción si pulsa si
        .setPositiveButton(android.R.string.ok) { v, _ ->
            //Solicitamos los permisos de nuevo
            solicitudPermisosLauncher.launch(PERMISOS_REQUERIDOS)
            //cerramos el dialogo
            v.dismiss()
        }
            //accion si pulsa no
            .setNegativeButton(android.R.string.cancel) { v, _ -> v.dismiss() }
            .setCancelable(false)
            .create()
            .show()
    }

    private val solicitudPermisosLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission has been granted.
                buscarFoto()
            } else {
                // Permission request was denied.
                explicarPermisos()
            }
        }

    fun iniciaIvBuscarFoto() {
        binding.ivBuscarFoto.setOnClickListener() {
            when {
                //La versión 34 el usuario puede dar permiso a fotos individualmente
                //el sistema gestiona los permisos
                Build.VERSION.SDK_INT >= 34->buscarFoto()
                //para otras versiones si tenemos los permisos
                permisosAceptados() -> buscarFoto()
                //no tenemos los permisos : los solicitamos
                else -> solicitudPermisosLauncher.launch(PERMISOS_REQUERIDOS)
            }
        }
    }

    /**
     * Iniciamos la tarea que ya existe
     */
    private fun iniciaTarea(tarea: Tarea) {
        binding.spCategorias.setSelection(tarea.categoria)
        binding.spPrioridad.setSelection(tarea.prioridad)
        binding.swPagado.isChecked = tarea.pagado
        binding.rgEstado.check(
            when (tarea.estado) {
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
        if (!tarea.fotoUri.isNullOrEmpty())
            binding.ivFotoTarea.setImageURI(tarea.fotoUri.toUri())
        else if (!uriFoto.isNullOrEmpty())
            binding.ivFotoTarea.setImageURI(uriFoto.toUri())
    }

    private fun iniciaTecnico() {
        //recuperamos las preferencias
        val sharedPreferences =

            PreferenceManager.getDefaultSharedPreferences(requireContext())
        //recuperamos el nombre del usuario
        val tecnico = sharedPreferences.getString(MainActivity.PREF_NOMBRE, "")
        //lo asignamos
        binding.tietTecnico.setText(tecnico)
    }

    /**
     * Muestra el mensaje de error
     */
    private fun muestraMensajeError() {
        Snackbar.make(binding.clytTarea, R.string.mensaje_error_guardar, Snackbar.LENGTH_LONG)
            // En este caso no vamos a implementar ninguna acción adicional
            .setAction("Action", null).show()
    }

    /**
     * Inicia el FabGuardar
     */
    private fun iniciarFabGuardar() {
        binding.fabGuardar.setOnClickListener {
            if (binding.tietTecnico.text.toString()
                    .isEmpty() || binding.etDescripcion.text.toString().isEmpty()
            )
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
        val categoria = binding.spCategorias.selectedItemPosition
        val prioridad = binding.spPrioridad.selectedItemPosition
        val pagado = binding.swPagado.isChecked
        val estado = when (binding.rgEstado.checkedRadioButtonId) {
            R.id.rbAbierta -> 0
            R.id.rbEnCurso -> 1
            else -> 2
        }
        val horas = binding.sbHorasTrabajadas.progress
        val valoracion = binding.rtbValoracion.rating
        val tecnico = binding.tietTecnico.text.toString()
        val descripcion = binding.etDescripcion.text.toString()
        //creamos la tarea: si es nueva, generamos un id, en otro caso le asignamos su id
        val tarea = if (esNuevo)
            Tarea(categoria, prioridad, pagado, estado, horas, valoracion, tecnico, descripcion, uriFoto)
        else
            Tarea(args.tarea!!.id, categoria, prioridad, pagado, estado, horas, valoracion, tecnico, descripcion, uriFoto)
        //guardamos la tarea desde el viewmodel
        viewModel.addTarea(tarea)
        //salimos de editarFragment
        findNavController().popBackStack()
    }

    /**
     * Inicia el ScrollBar de Horas Trabajadas
     */
    private fun iniciarSbHorasTrabajadas() {
        binding.sbHorasTrabajadas.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progreso: Int, p2: Boolean) {
                binding.tvHorasTrabajadas.text = getString(R.string.horas_tabajadas, progreso)
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
                if (binding.sbHorasTrabajadas.max == seekBar?.progress) {
                    binding.rgEstado.check(R.id.rbCerrada)
                } else if (0 == seekBar?.progress) {
                    binding.rgEstado.check(R.id.rbAbierta)
                } else {
                    binding.rgEstado.check(R.id.rbEnCurso)
                }
            }
        })
        // Inicializamos el valor del SeekBar, horas trabajadas
        binding.sbHorasTrabajadas.progress = 0
        binding.tvHorasTrabajadas.text = getString(R.string.horas_tabajadas, 0)
    }

    /**
     * Inicia el Switch de Pagado
     */
    private fun iniciarSwPagado() {
        binding.swPagado.setOnCheckedChangeListener { _, isChecked ->
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
    private fun iniciarRgEstado() {
        // Creamos el evento CheckerChange para cambiar las imagenes al seleccionar
        // un RadioButton u otro
        binding.rgEstado.setOnCheckedChangeListener { _, checkedId ->
            // Hacemos un when para coger la imagen adecuada en cada caso
            val imagen = when (checkedId) {
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
    private fun iniciarSpPrioridad() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.prioridad,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spPrioridad.adapter = adapter

            // Cuando se seleccione prioridad Alta cambiamos el color del fondo
            binding.spPrioridad.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        v: View?,
                        posicion: Int,
                        id: Long
                    ) {
                        if (posicion == 2) {
                            val
                                    colorPorDefecto=resources.getStringArray(R.array.color_values)[0]
                            //recuperamos el color actual
                            val color=
                                PreferenceManager.getDefaultSharedPreferences(requireContext())
                                    .getString(MainActivity.PREF_COLOR_PRIORIDAD,colorPorDefecto)

                            // Si el item seleccionado es "alta", que se encuentra en la tercera posicion (2 del array), se cambiará el fondo al color que hayamos escogido
                            binding.clytTarea.setBackgroundColor(Color.parseColor(color))
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
    private fun iniciarSpCategorias() {
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

            binding.spCategorias.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        v: View?,
                        posicion: Int,
                        id: Long
                    ) {
                        // Cogemos el string del item seleccionado
                        val categoriaSeleccionada = binding.spCategorias.getItemAtPosition(posicion)
                        // Lo ponemos en el mensaje que hemos creado en strings.xml
                        val mensaje = getString(R.string.mensaje_categoria, categoriaSeleccionada)

                        // Mostramos el mensaje mediante el SnackBar
                        Snackbar.make(binding.clytTarea, mensaje, Snackbar.LENGTH_LONG)
                            // En este caso no vamos a implementar ninguna acción adicional
                            .setAction("Action", null).show()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // En caso de que ninguno fuera seleccionado nos mostraría este mensaje.
                        Snackbar.make(
                            binding.clytTarea,
                            "Ninguna categoría seleccionada",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("Action", null).show()
                    }
                }
        }
    }

    private val TAG = "Practica5"
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private fun saveBitmapImage(bitmap: Bitmap): Uri? {
        val timestamp = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        var uri: Uri? = null
        //Tell the media scanner about the new file so that it is immediately available to the user.
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, timestamp)
        //mayor o igual a version 29
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
            values.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "Pictures/" + getString(R.string.app_name)
            )
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            uri = requireContext().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
            if (uri != null) {
                try {
                    val outputStream = requireContext().contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        try {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                            outputStream.close()
                        } catch (e: Exception) {
                            Log.e(TAG, "saveBitmapImage: ", e)
                        }
                    }
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    requireContext().contentResolver.update(uri, values, null, null)
                    // Toast.makeText(requireContext(), "Saved...", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e(TAG, "saveBitmapImage: ", e)
                }
            }
        } else {//no me funciona bien en versiones inferiores a la 29(Android 10)
            val imageFileFolder = File(
                Environment.getExternalStorageDirectory()
                    .toString() + '/' + getString(R.string.app_name)
            )
            if (!imageFileFolder.exists()) {
                imageFileFolder.mkdirs()
            }
            val mImageName = "$timestamp.png"
            val imageFile = File(imageFileFolder, mImageName)
            try {
                val outputStream: OutputStream = FileOutputStream(imageFile)
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {
                    Log.e(TAG, "saveBitmapImage: ", e)
                }
                values.put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                requireContext().contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )
                uri = imageFile.toUri()
                // Toast.makeText(requireContext(), "Saved...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "saveBitmapImage: ", e)
            }
        }
        return uri
    }

    fun loadFromUri(photoUri: Uri?): Bitmap? {
        var image: Bitmap? = null
        try {
            // check version of Android on device
            image = if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                val source = ImageDecoder.createSource(
                    requireContext().contentResolver,
                    photoUri!!
                )
                ImageDecoder.decodeBitmap(source)
            } else {
                // support older versions of Android by using getBitmap
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver,
                    photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    fun mostrarImagenFragment() {
        binding.ivFotoTarea.setOnClickListener{
            val action = TareaFragmentDirections.actionImagen(uriFoto)
            findNavController().navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        iniciarSwPagado()
        iniciarRgEstado()
        iniciarSbHorasTrabajadas()
        iniciarFabGuardar()
        iniciaIvBuscarFoto()

        binding.ivFotoTarea.setOnClickListener{
            val action: NavDirections
            if (!uriFoto.isNullOrEmpty())
                action = TareaFragmentDirections.actionImagen(uriFoto)
            else
                action = TareaFragmentDirections.actionImagen(args.tarea!!.fotoUri)
            findNavController().navigate(action)
        }

        if (esNuevo) {
            // Si la tarea es nueva pondremos de titulo "Nueva tarea" y si no pondremos "Tarea" + el id de esa tarea
            (requireActivity() as AppCompatActivity).supportActionBar?.title = "Nueva tarea"
            iniciaTecnico()
        } else
            iniciaTarea(args.tarea!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}