package net.iesochoa.pacofloridoquesada.practica5.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import net.iesochoa.pacofloridoquesada.practica5.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        // Buscamos la preferencia
        val buildVersion: Preference? = findPreference("buildVersion")
        // Definimos la accion para la preferencia
        buildVersion?.setOnPreferenceClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://portal.edu.gva.es/03013224/")
                )
            )
            // Hay que devolver booleano para indicar si se acepta el cambio o no
            false
        }

        // Buscamos la preferencia
        val telefonoContacto: Preference? = findPreference("telefonoContacto")
        // Definimos la accion para la preferencia
        telefonoContacto?.setOnPreferenceClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:966912260")
                )
            )
            // Hay que devolver booleano para indicar si se acepta el cambio o no
            false
        }
    }
}