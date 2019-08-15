package dev.banantha.bhive

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley

import kotlinx.android.synthetic.main.activity_keeper.*
import kotlinx.android.synthetic.main.content_keeper.*
import org.json.JSONArray

class KeeperActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keeper)

        fab.setOnClickListener { refreshKeeper()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        refreshKeeper()
    }

    private fun refreshKeeper() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val theColony = sharedPreferences.getString("web_service", "")
        val cipher = sharedPreferences.getString("cipher", "")
        val activity = "/Keeper"
        val url = "$theColony$cipher$activity"

        val queue = Volley.newRequestQueue(this)

        val jsonRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener
            {
                response ->
                try
                {
                    assignData(response)
                }
                catch (t: Throwable)
                {
                    val toast = Toast.makeText(applicationContext, "Error Parsing Data!", Toast.LENGTH_SHORT)
                    toast.show()
                }
            },
            Response.ErrorListener
            {
                //TODO
            })

        queue.add(jsonRequest)
    }

    private fun assignData(response: JSONArray) {
        for (i in 0 until response.length()) {
            when (response.getJSONObject(i).getString("port_name")) {
                "front_door" ->
                {
                    textView_front_door_time.text = response.getJSONObject(i).getString("server_time")
                    textView_front_door_state.text = response.getJSONObject(i).getString("port_state")
                }
                "garage_door" ->
                {
                    textView_garage_door_time.text = response.getJSONObject(i).getString("server_time")
                    textView_garage_door_state.text = response.getJSONObject(i).getString("port_state")
                }
                "back_door" ->
                {
                    textView_back_door_time.text = response.getJSONObject(i).getString("server_time")
                    textView_back_door_state.text = response.getJSONObject(i).getString("port_state")
                }
                "study_window" ->
                {
                    textView_study_window_time.text = response.getJSONObject(i).getString("server_time")
                    textView_study_window_state.text = response.getJSONObject(i).getString("port_state")
                }
                "bedroom_window" ->
                {
                    textView_bedroom_window_time.text = response.getJSONObject(i).getString("server_time")
                    textView_bedroom_window_state.text = response.getJSONObject(i).getString("port_state")
                }
                "kitchen_window" ->
                {
                    textView_kitchen_window_time.text = response.getJSONObject(i).getString("server_time")
                    textView_kitchen_window_state.text = response.getJSONObject(i).getString("port_state")
                }
                "lounge_window" ->
                {
                    textView_lounge_window_time.text = response.getJSONObject(i).getString("server_time")
                    textView_lounge_window_state.text = response.getJSONObject(i).getString("port_state")
                }
                "bay_window" ->
                {
                    textView_bay_window_time.text = response.getJSONObject(i).getString("server_time")
                    textView_bay_window_state.text = response.getJSONObject(i).getString("port_state")
                }
                "master_window" ->
                {
                    textView_master_window_time.text = response.getJSONObject(i).getString("server_time")
                    textView_master_window_state.text = response.getJSONObject(i).getString("port_state")
                }
                "bathroom_window" ->
                {
                    textView_bathroom_window_time.text = response.getJSONObject(i).getString("server_time")
                    textView_bathroom_window_state.text = response.getJSONObject(i).getString("port_state")
                }
            }
        }
    }
}
