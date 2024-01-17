package net.iesochoa.pacofloridoquesada.practica5.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import net.iesochoa.pacofloridoquesada.practica5.R
import net.iesochoa.pacofloridoquesada.practica5.databinding.FragmentImageBinding

class ImageFragment : Fragment() {
    //  Recuperamos los argumentos pasados
    val args: ImageFragmentArgs by navArgs()

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Asigno la uri de la Imagen al ImageView de este Fragment.
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.foto)
        binding.ivFoto.setImageURI(args.uriFotoTarea.toUri())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}