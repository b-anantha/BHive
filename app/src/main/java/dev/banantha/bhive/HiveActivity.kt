package dev.banantha.bhive

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.content_hive.*

class HiveActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hive)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = this.findViewById(R.id.fab)
        fab.setOnClickListener { refreshHome()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    println("getInstanceId failed")
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                println(token)
            })

        refreshHome()
    }

    private fun refreshHome() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val theColony = sharedPreferences.getString("web_service", "")
        val cipher = sharedPreferences.getString("cipher", "")
        val activity = "/bHive"
        val url = "$theColony$cipher$activity"

        val queue = Volley.newRequestQueue(this)

        val jsonRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener
            {
                response ->
                try
                {
                    textView_sync_time.text = response["device_time"].toString()
                    textView_hostname_value.text = response["hostname"].toString()
                    textView_cpu_temp_value.text = response["cpu_temp"].toString()
                    textView_cpu_freq_value.text = response["cpu_freq"].toString()
                    textView_wifi_strength_value.text = response["wifi_strength"].toString()
                }
                catch (t: Throwable)
                {
                    val toast = Toast.makeText(applicationContext, "Error Parsing Data!", Toast.LENGTH_SHORT)
                    toast.show()
                }
            },
            Response.ErrorListener
            {
                textView_hostname_value.text = "N/A"
                textView_cpu_temp_value.text = "N/A"
                textView_cpu_freq_value.text = "N/A"
                textView_wifi_strength_value.text = "N/A"
            })

        queue.add(jsonRequest)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {

            }
            R.id.nav_keeper -> {
                val keeper = Intent(this, KeeperActivity::class.java)
                startActivity(keeper)
            }
            R.id.nav_climate -> {
                val climate = Intent(this, ClimateActivity::class.java)
                startActivity(climate)
            }
            R.id.nav_settings -> {
                val settings = Intent(this, SettingsActivity::class.java)
                startActivity(settings)
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}