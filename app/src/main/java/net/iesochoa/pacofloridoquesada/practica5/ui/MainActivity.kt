package net.iesochoa.pacofloridoquesada.practica5.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceManager
import net.iesochoa.pacofloridoquesada.practica5.R
import net.iesochoa.pacofloridoquesada.practica5.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        val PREF_NOMBRE = "nombre"
        val PREF_COLOR_PRIORIDAD = "color_prioridad"
        val PREF_AVISO_NUEVAS = "aviso_nueva"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        compruebaAviso()

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    fun compruebaAviso(){
        val aviso = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(PREF_AVISO_NUEVAS,false)
        if (aviso) {
            binding.ivAviso.visibility = View.VISIBLE
        } else {
            binding.ivAviso.visibility = View.INVISIBLE
        }
    }

    fun actionPrueba():Boolean{
        Toast.makeText(this,"Prueba de menú",Toast.LENGTH_SHORT).show()
        return true
    }
    private fun actionSettings(): Boolean {
        findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.settingsFragment)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_prueba -> actionPrueba()
            R.id.action_settings -> actionSettings()
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onResume() {
        super.onResume()

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
    }
    override fun onPause() {
        super.onPause()

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        compruebaAviso()
    }
}