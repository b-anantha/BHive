package dev.banantha.bhive

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.activity_climate.*
import kotlinx.android.synthetic.main.content_climate.*
import org.json.JSONArray

class ClimateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_climate)

        val hour = editText_hours.text

        fab.setOnClickListener {
            getData(hour)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getData(hour)
    }

    private fun getData(hour: Editable) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val theColony = sharedPreferences.getString("web_service", "")
        val cipher = sharedPreferences.getString("cipher", "")
        val activity = "/Climate/"
        val url = "$theColony$cipher$activity$hour"

        val queue = Volley.newRequestQueue(this)

        val jsonRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            Response.Listener
            {
                response ->
                try
                {
                    plotChart(response)
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

    private fun plotChart(response: JSONArray) {
        var climateChart: LineChart? = null
        climateChart = findViewById(R.id.chartClimate)
        climateChart.setDrawGridBackground(false)
        climateChart.description.isEnabled = false
        climateChart.setDrawBorders(true)

        climateChart.axisLeft.isEnabled = true
        climateChart.axisRight.isEnabled = false
        climateChart.xAxis.setDrawAxisLine(false)
        climateChart.xAxis.setDrawGridLines(true)

        climateChart.setTouchEnabled(true)
        climateChart.isHighlightPerDragEnabled = true
        climateChart.isDragEnabled = true
        climateChart.setScaleEnabled(true)
        climateChart.setPinchZoom(true)
        climateChart.isDoubleTapToZoomEnabled = false

        val legend = climateChart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.form = Legend.LegendForm.LINE
        legend.setDrawInside(false)

        val serverTime = ArrayList<String>()
        val heatTemp = ArrayList<Entry>()
        val coolTemp = ArrayList<Entry>()
        val indoorTemp = ArrayList<Entry>()
        val indoorHumid = ArrayList<Entry>()
        val outdoorTemp = ArrayList<Entry>()
        val outdoorHumid = ArrayList<Entry>()

        for (i in 0 until response.length()) {
            serverTime.add(response.getJSONObject(i).getString("server_time"))
            heatTemp.add(Entry(i.toFloat(), response.getJSONObject(i).getString("heat_setpoint").toFloat()))
            coolTemp.add(Entry(i.toFloat(), response.getJSONObject(i).getString("cool_setpoint").toFloat()))
            indoorTemp.add(Entry(i.toFloat(), response.getJSONObject(i).getString("indoor_temp").toFloat()))
            indoorHumid.add(Entry(i.toFloat(), response.getJSONObject(i).getString("indoor_humidity").toFloat()))
            outdoorTemp.add(Entry(i.toFloat(), response.getJSONObject(i).getString("outdoor_temp").toFloat()))
            outdoorHumid.add(Entry(i.toFloat(), response.getJSONObject(i).getString("outdoor_humidity").toFloat()))
        }

        val formatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return serverTime[value.toInt()]
            }
        }

        val xAxis = climateChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelRotationAngle = -45F
        xAxis.granularity = 1f // minimum axis-step (interval) is 1
        xAxis.valueFormatter = formatter

        val tempHeat = LineDataSet(heatTemp, "Heat")
        val tempCool = LineDataSet(coolTemp, "Cool")
        val tempIn = LineDataSet(indoorTemp, "Temp In")
        val humidIn = LineDataSet(indoorHumid, "Humid In")
        val tempOut = LineDataSet(outdoorTemp, "Temp Out")
        val humidOut = LineDataSet(outdoorHumid, "Humid Out")

        tempHeat.lineWidth = 1.5f
        tempHeat.color = Color.RED
        tempHeat.setDrawCircles(false)
        tempHeat.enableDashedLine(10f,6f,0f)
        tempHeat.highLightColor = Color.BLACK

        tempCool.lineWidth = 1.5f
        tempCool.color = Color.BLUE
        tempCool.setDrawCircles(false)
        tempCool.enableDashedLine(10f,6f,0f)
        tempCool.highLightColor = Color.BLACK

        tempIn.lineWidth = 1.5f
        tempIn.color = Color.BLACK
        tempIn.setDrawCircles(false)
        tempIn.highLightColor = Color.BLACK

        humidIn.lineWidth = 1.5f
        humidIn.color = Color.DKGRAY
        humidIn.setDrawCircles(false)
        humidIn.enableDashedLine(4f,5f,0f)
        humidIn.highLightColor = Color.BLACK

        tempOut.lineWidth = 1.5f
        tempOut.color = Color.parseColor("#1B5E20")
        tempOut.setDrawCircles(false)
        tempOut.highLightColor = Color.BLACK

        humidOut.lineWidth = 1.5f
        humidOut.color = Color.parseColor("#33691E")
        humidOut.setDrawCircles(false)
        humidOut.enableDashedLine(4f,5f,0f)
        humidOut.highLightColor = Color.BLACK

        val dataSets = java.util.ArrayList<ILineDataSet>()

        dataSets.add(tempHeat)
        dataSets.add(tempCool)
        dataSets.add(tempIn)
        dataSets.add(humidIn)
        dataSets.add(tempOut)
        dataSets.add(humidOut)

        val data = LineData(dataSets)
        climateChart!!.data = data
        climateChart.invalidate()
    }
}
