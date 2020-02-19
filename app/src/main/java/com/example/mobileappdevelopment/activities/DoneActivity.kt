package com.example.mobileappdevelopment.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileappdevelopment.DataUtils.Coordinates
import com.example.mobileappdevelopment.DataUtils.LocUtils
import com.example.mobileappdevelopment.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DoneActivity : AppCompatActivity(), OnMapReadyCallback {
    private var dialog: AlertDialog? = null
    private var button: Button? = null
    @SuppressLint("SetTextI18n")
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_done)
        supportActionBar!!.hide()
        button = findViewById(R.id.button_done)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        val builder = AlertDialog.Builder(this@DoneActivity)
        val layoutInflater = LayoutInflater.from(this@DoneActivity)
        @SuppressLint("InflateParams") val popupDialogView = layoutInflater.inflate(R.layout.dialog_score, null)
        builder.setView(popupDialogView)
        builder.setCancelable(true)
        dialog = builder.create()
        val totalQuestions = intent.getIntExtra("questions", 0)
        val huntName = intent.getStringExtra("hunt_name")
        val sp = getSharedPreferences(huntName, Context.MODE_PRIVATE)
        val time = sp.getLong(huntName + "time", 0)
        val seconds = time / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val actualSeconds = seconds - minutes * 60
        val actualMinutes = minutes - hours * 60
        val que = popupDialogView.findViewById<TextView>(R.id.question_value)
        val hrs = popupDialogView.findViewById<TextView>(R.id.hours_value)
        val min = popupDialogView.findViewById<TextView>(R.id.minutes_value)
        val sec = popupDialogView.findViewById<TextView>(R.id.seconds_value)
        que.text = totalQuestions.toString()
        hrs.text = hours.toString()
        min.text = actualMinutes.toString()
        sec.text = actualSeconds.toString()
        val imageButton = findViewById<ImageButton>(R.id.image_button_score)
        imageButton.setOnClickListener { dialog?.show() }
        button?.setOnClickListener {
            val i = Intent(this@DoneActivity, MainActivity::class.java)
            startActivity(i)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        var teller = 1
        for (latLng in Coordinates.coordinatesList) {
            googleMap.addMarker(MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(LocUtils.resizeMapIcons(this@DoneActivity, "marker_green")))
                    .title("Marker $teller")
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
            teller++
        }
    }
}