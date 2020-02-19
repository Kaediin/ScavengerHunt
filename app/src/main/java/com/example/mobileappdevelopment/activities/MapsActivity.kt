package com.example.mobileappdevelopment.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mobileappdevelopment.DataUtils.Coordinates
import com.example.mobileappdevelopment.DataUtils.LocUtils
import com.example.mobileappdevelopment.Library.QuestionLibrary
import com.example.mobileappdevelopment.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.text.DecimalFormat
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private var dialog: AlertDialog? = null
    private var mMap: GoogleMap? = null
    private var googleApiClient: GoogleApiClient? = null
    private var progress_number = 0
    private var time: Long = 0
    private var dialogActive = false
    private var mQuestionView: TextView? = null
    private var mAnswer: String? = null
    private var hunt_name: String? = null
    private var mButtonChoice1: Button? = null
    private var mButtonChoice2: Button? = null
    private var mButtonChoice3: Button? = null
    private var start_time: Long = 0
    private var sp: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        Objects.requireNonNull(supportActionBar)!!.hide()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        googleApiClient = GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build()
        hunt_name = intent.getStringExtra("hunt_name")
        sp = getSharedPreferences(hunt_name, Context.MODE_PRIVATE)
        val progress = sp?.getInt(hunt_name, 0)
        if (progress == 0) {
            start_time = Calendar.getInstance().timeInMillis
            editor = sp?.edit()
            editor?.putLong(hunt_name + "start_time", start_time)
            editor?.apply()
        } else {
            start_time = sp!!.getLong(hunt_name + "start_time", Calendar.getInstance().timeInMillis)
        }
        progress_number = progress!!
        time = 1000
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    override fun onStart() {
        super.onStart()
        if (googleApiClient != null) {
            googleApiClient!!.connect()
        }
    }

    override fun onPause() {
        super.onPause()
        // stop location updates
        if (googleApiClient != null && googleApiClient!!.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            googleApiClient!!.disconnect()
        }
    }

    override fun onConnected(bundle: Bundle?) {
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        val location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        if (location != null) {
            setTVDistance(location)
            setupMap(location)
        }
        startLocationUpdates()
    }

    private fun setupMap(location: Location) {
        val currentLoc = LatLng(location.latitude, location.longitude)
        mMap!!.addMarker(MarkerOptions()
                .position(currentLoc)
                .icon(BitmapDescriptorFactory.fromBitmap(LocUtils.resizeMapIcons(this@MapsActivity, "marker_red")))
                .title("Marker in your current location"))
        loadCoordinates(progress_number)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15f))
    }

    private fun loadCoordinates(numberAscending: Int) {
        val coordinates = Coordinates.coordinatesList
        mMap!!.addMarker(MarkerOptions()
                .position(coordinates[numberAscending])
                .icon(BitmapDescriptorFactory.fromBitmap(LocUtils.resizeMapIcons(this@MapsActivity, "marker_green")))
                .title("Coordinate $numberAscending"))
        mMap!!.addCircle(CircleOptions().center(coordinates[numberAscending]).radius(QuestionLibrary.radius[numberAscending].toDouble()))
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = time
        locationRequest.fastestInterval = time
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display your location!", Toast.LENGTH_SHORT).show()
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (googleApiClient != null) {
            googleApiClient!!.connect()
        }
    }

    override fun onLocationChanged(location: Location) {
        if (!dialogActive) {
            if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap!!.clear()
                val currentLoc = LatLng(location.latitude, location.longitude)
                loadCoordinates(progress_number)
                mMap!!.addMarker(MarkerOptions()
                        .position(currentLoc)
                        .icon(BitmapDescriptorFactory.fromBitmap(LocUtils.resizeMapIcons(this@MapsActivity, "marker_red")))
                        .title("Marker in your current location"))
                val distanceInKM = setTVDistance(location)
                if (distanceInKM < QuestionLibrary.radius[progress_number]) {
                    dialogActive = true
                    val builder = AlertDialog.Builder(this)
                    val layoutInflater = LayoutInflater.from(this)
                    @SuppressLint("InflateParams") val popupDialogView = layoutInflater.inflate(R.layout.dialog_question, null)
                    builder.setView(popupDialogView)
                    builder.setCancelable(false)
                    dialog = builder.create()
                    dialog?.show()
                    mQuestionView = popupDialogView.findViewById(R.id.question)
                    mButtonChoice1 = popupDialogView.findViewById(R.id.button_1)
                    mButtonChoice2 = popupDialogView.findViewById(R.id.button_2)
                    mButtonChoice3 = popupDialogView.findViewById(R.id.button_3)
                    updateQuestions(progress_number)
                    val handler = Handler()
                    if (mButtonChoice1 != null) {
                        mButtonChoice1!!.setOnClickListener {
                            if (mButtonChoice1!!.text == mAnswer) {
                                mButtonChoice1!!.backgroundTintList = this@MapsActivity.resources.getColorStateList(R.color.green)
                                handler.postDelayed({
                                    progress_number++
                                    next_level()
                                    isDone(progress_number)
                                }, 1000)
                            } else {
                                mButtonChoice1!!.backgroundTintList = this@MapsActivity.resources.getColorStateList(R.color.red)
                            }
                        }
                    }
                    if (mButtonChoice2 != null) {
                        mButtonChoice2!!.setOnClickListener {
                            if (mButtonChoice2!!.text == mAnswer) {
                                mButtonChoice2!!.backgroundTintList = this@MapsActivity.resources.getColorStateList(R.color.green)
                                handler.postDelayed({
                                    progress_number++
                                    next_level()
                                    isDone(progress_number)
                                }, 1000)
                            } else {
                                mButtonChoice2!!.backgroundTintList = this@MapsActivity.resources.getColorStateList(R.color.red)
                            }
                        }
                    }
                    if (mButtonChoice3 != null) {
                        mButtonChoice3!!.setOnClickListener {
                            if (mButtonChoice3!!.text == mAnswer) {
                                mButtonChoice3!!.backgroundTintList = this@MapsActivity.resources.getColorStateList(R.color.green)
                                handler.postDelayed({
                                    progress_number++
                                    next_level()
                                    isDone(progress_number)
                                }, 1000)
                            } else {
                                mButtonChoice3!!.backgroundTintList = this@MapsActivity.resources.getColorStateList(R.color.red)
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "You need to enable permissions to display your location!", Toast.LENGTH_SHORT).show()
                val i = Intent(this@MapsActivity, MainActivity::class.java)
                startActivity(i)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTVDistance(location: Location): Double {
        val locCheck = Location(LocationManager.GPS_PROVIDER)
        locCheck.latitude = Coordinates.coordinatesList[progress_number].latitude
        locCheck.longitude = Coordinates.coordinatesList[progress_number].longitude
        val distanceInKM = location.distanceTo(locCheck).toDouble()
        val distanceTV = findViewById<TextView>(R.id.distanceText)
        val distanceRounded = DecimalFormat("###")
        distanceTV.text = distanceRounded.format(distanceInKM) + " M"
        return distanceInKM
    }

    fun next_level() {
        dialog!!.dismiss()
        dialogActive = false
        editor = sp!!.edit()
        editor?.putInt(hunt_name, progress_number)
        editor?.apply()
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    fun isDone(numberAscending: Int) {
        if (numberAscending >= QuestionLibrary.questions.size) {
            val endTime = Calendar.getInstance().timeInMillis
            val time = endTime - start_time
            editor = sp!!.edit()
            editor?.putInt(hunt_name, 0)
            editor?.putLong(hunt_name + "time", time)
            editor?.apply()
            val i = Intent(this@MapsActivity, DoneActivity::class.java)
            i.putExtra("hunt_name", hunt_name)
            i.putExtra("questions", numberAscending)
            startActivity(i)
        }
    }

    private fun updateQuestions(mQuestionNumber: Int) {
        mQuestionView!!.text = QuestionLibrary.questions[mQuestionNumber]
        mButtonChoice1!!.text = QuestionLibrary.choices1[mQuestionNumber]
        mButtonChoice2!!.text = QuestionLibrary.choices2[mQuestionNumber]
        mButtonChoice3!!.text = QuestionLibrary.choices3[mQuestionNumber]
        mAnswer = QuestionLibrary.correctAnswers[mQuestionNumber]
    }
}