package net.iesochoa.pacofloridoquesada.practica5.ui

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import net.iesochoa.pacofloridoquesada.practica5.R
import net.iesochoa.pacofloridoquesada.practica5.databinding.FragmentFotoBinding


class FotoFragment : Fragment() {
    private var _binding: FragmentFotoBinding? = null
    private val binding get() = _binding!!
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

    /**
     * Método que inicia la cámara
     */
    private fun startCamera() {
        Toast.makeText(requireContext(),
            "Camara iniciada…",
            Toast.LENGTH_SHORT).show()
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
    }
}