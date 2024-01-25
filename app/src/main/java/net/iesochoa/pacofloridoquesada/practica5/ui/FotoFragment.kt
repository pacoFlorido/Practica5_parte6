package net.iesochoa.pacofloridoquesada.practica5.ui

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import net.iesochoa.pacofloridoquesada.practica5.R
import net.iesochoa.pacofloridoquesada.practica5.databinding.FragmentFotoBinding
import java.util.Locale


class FotoFragment : Fragment() {
    private var _binding: FragmentFotoBinding? = null
    private val binding get() = _binding!!
    private var imageCapture: ImageCapture? = null
    private var uriFoto: Uri?=null

    companion object {
        private const val TAG = "Practica5_CameraX"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }



    //Array con los permisos necesarios
    private val PERMISOS_REQUERIDOS =
        mutableListOf (
            Manifest.permission.CAMERA
        ).apply {
            //si la versión de Android es menor o igual a la 9 pedimos el permiso de escritura
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

    /**
     * Método que comprueba si tenemos los permiso
     */
    private fun allPermissionsGranted() = PERMISOS_REQUERIDOS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) ==
                PackageManager.PERMISSION_GRANTED
    }

    // Permite lanzar la solicitud de permisos al sistema operativo y
    // actuar según el usuario
    // los acepte o no
    val solicitudPermisosLauncher = registerForActivityResult(
        //realizamos una solicitud de multiples permisos
        ActivityResultContracts.RequestMultiplePermissions()
    ) { isGranted: Map<String, Boolean> ->
        if (allPermissionsGranted()) {
            //Si tenemos los permisos, iniciamos la cámara
            startCamera()
        } else {
            // Si no tenemos los permisos. Explicamos al usuario
            explicarPermisos()
        }
    }

    fun explicarPermisos() {
        AlertDialog.Builder(requireContext())
            .setTitle(android.R.string.dialog_alert_title)
            .setMessage(getString(R.string.son_necesarios_los_permisos_para_hacer_una_foto_desea_aceptar_los_permisos))
        //acción si pulsa si
        .setPositiveButton(android.R.string.ok) { v, _ ->
            //Solicitamos los permisos de nuevo
            solicitudPermisosLauncher.launch(PERMISOS_REQUERIDOS)
            //cerramos el dialogo
            v.dismiss()
        }
            //accion si pulsa no
            .setNegativeButton(android.R.string.cancel) { v, _ ->
                v.dismiss()
                //cerramos el fragment

                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    /**
     * Método que inicia la cámara
     */
    private fun startCamera() {
        //Se usa para vincular el ciclo de vida de las cámaras al
        //propietario del ciclo de vida.
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext())
        //Agrega un elemento Runnable como primer argumento
        cameraProviderFuture.addListener({
            // Esto se usa para vincular el ciclo de vida de nuestra
            //cámara al LifecycleOwner dentro del proceso de la aplicación
            val cameraProvider: ProcessCameraProvider =
                cameraProviderFuture.get()
            //Inicializa nuestro objeto Preview,
            // llama a su compilación, obtén un proveedor de plataforma
            //desde el visor y,
            // luego, configúralo en la vista previa.
            val preview = Preview.Builder()
                .build()
                .also {

                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        //segundo argumento
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return
        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        // val name = "practica5_1"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            //funiona en versiones superiores a Android 9
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" +
                        getString(R.string.app_name))
            }
        }
        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireActivity().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()
        // Set up image capture listener, which is triggered after photo has
                // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}",
                        exc)
                }
                override fun onImageSaved(output:
                                          ImageCapture.OutputFileResults) {

                    val msg = "Photo capture succeeded:${output.savedUri}"
                    Toast.makeText(requireContext(), msg,
                        Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    binding.ivMuestra.setImageURI(output.savedUri)
                    uriFoto=output.savedUri
                }
            }
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            solicitudPermisosLauncher.launch(PERMISOS_REQUERIDOS)
        }

        binding.btCapturaFoto.setOnClickListener{
            takePhoto()
        }
    }
}