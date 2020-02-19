package com.example.mobileappdevelopment.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mobileappdevelopment.DataUtils.Cache
import com.example.mobileappdevelopment.DataUtils.Coordinates
import com.example.mobileappdevelopment.DataUtils.DataHunt
import com.example.mobileappdevelopment.DataUtils.LocUtils
import com.example.mobileappdevelopment.Library.QuestionLibrary
import com.example.mobileappdevelopment.Model.Hunt
import com.example.mobileappdevelopment.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.util.*

class CreateHuntActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var selectedLatLng: LatLng? = null
    private var dialog: AlertDialog? = null
    private var selectLocationButton: Button? = null
    private var saveHunt: Button? = null
    private var cancel: Button? = null
    private var add: Button? = null
    private var question: EditText? = null
    private var answer1: EditText? = null
    private var answer2: EditText? = null
    private var answer3: EditText? = null
    private var radioGroup: RadioGroup? = null
    private var myPos: LatLng? = null
    private var panel: View? = null
    private var thumbView: View? = null
    private var progressBar: ProgressBar? = null
    private var seekBar: SeekBar? = null
    private var huntMap: MutableMap<String, Any>? = null
    private var cr: CollectionReference? = null
    @SuppressLint("InflateParams")
    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_create_hunt)
        supportActionBar!!.hide()
        // Setting up map fragment
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        // Connect to firebase
        val fb = FirebaseFirestore.getInstance()
        // Stores locationProvider in var
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // Gets collection to store data
        cr = fb.collection("Scavenger_Hunts")
        // Creates a map where data will be stores into. This map will be stored into the FireBase
        huntMap = HashMap()
        // Assign all layout elements
        assignVars()
        // Sets up splash screen
        splash()
        // Clears all the lists. So no data will be used from previous activities
        clearLists()
    }

    fun splash() {
        saveHunt!!.visibility = View.GONE
        panel!!.visibility = View.VISIBLE
        progressBar!!.visibility = View.VISIBLE
        selectLocationButton!!.visibility = View.GONE
        val handler = Handler()
        handler.postDelayed({
            panel!!.visibility = View.GONE
            progressBar!!.visibility = View.GONE
            selectLocationButton!!.visibility = View.VISIBLE
        }, 2000)
    }

    @SuppressLint("InflateParams")
    fun assignVars() {
        saveHunt = findViewById(R.id.start_hunt)
        panel = findViewById(R.id.loading_panel)
        progressBar = findViewById(R.id.loading_circle)
        selectLocationButton = findViewById(R.id.select_location_button)
        val builder = AlertDialog.Builder(this@CreateHuntActivity)
        val layoutInflater = LayoutInflater.from(this@CreateHuntActivity)
        @SuppressLint("InflateParams") val popupDialogView = layoutInflater.inflate(R.layout.dialog_create_question, null)
        builder.setView(popupDialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        seekBar = popupDialogView.findViewById(R.id.seekbar)
        cancel = popupDialogView.findViewById(R.id.cancel_loc)
        add = popupDialogView.findViewById(R.id.add_new_loc)
        question = popupDialogView.findViewById(R.id.new_question)
        answer1 = popupDialogView.findViewById(R.id.edit_field_1)
        answer2 = popupDialogView.findViewById(R.id.edit_field_2)
        answer3 = popupDialogView.findViewById(R.id.edit_field_3)
        radioGroup = popupDialogView.findViewById(R.id.radio_group)
        selectedLatLng = null
        thumbView = LayoutInflater.from(popupDialogView.context).inflate(R.layout.seekbar_layout_thumb, null, false)
    }

    fun clearLists() {
        Coordinates.coordinatesList.clear()
        QuestionLibrary.radius.clear()
        QuestionLibrary.questions.clear()
        QuestionLibrary.correctAnswers.clear()
        QuestionLibrary.choices3.clear()
        QuestionLibrary.choices2.clear()
        QuestionLibrary.choices1.clear()
    }

    override fun onStart() {
        super.onStart()
        if (Cache.account == null) {
            val intent = Intent(this@CreateHuntActivity, MainActivity::class.java)
            Toast.makeText(this, "You must be signed into a google account to continue", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fusedLocationProviderClient!!.lastLocation.addOnSuccessListener(this) { location ->
            try {
                myPos = LatLng(location.latitude, location.longitude)
                mMap!!.addMarker(MarkerOptions()
                        .position(myPos!!)
                        .icon(BitmapDescriptorFactory.fromBitmap(LocUtils.resizeMapIcons(this@CreateHuntActivity, "marker_red")))
                        .title("My position"))
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 20f))
            } catch (e: NullPointerException) {
                val handler = Handler()
                handler.postDelayed({ Toast.makeText(this@CreateHuntActivity, "Cannot track location", Toast.LENGTH_SHORT).show() }, 3000)
            }
        }
        mMap!!.setOnMapClickListener { latLng ->
            if (ActivityCompat.checkSelfPermission(this@CreateHuntActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.v("JarecTest", "Map tapped on")
                mMap!!.clear()
                mMap!!.addMarker(MarkerOptions()
                        .position(myPos!!)
                        .icon(BitmapDescriptorFactory.fromBitmap(LocUtils.resizeMapIcons(this@CreateHuntActivity, "marker_red")))
                        .title("My position"))
                for (chosenlatLng in Coordinates.coordinatesList) {
                    mMap!!.addMarker(MarkerOptions()
                            .position(chosenlatLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(LocUtils.resizeMapIcons(this@CreateHuntActivity, "marker_green")))
                            .title("Tapped location"))
                }
                mMap!!.addMarker(MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(LocUtils.resizeMapIcons(this@CreateHuntActivity, "marker_blue")))
                        .title("Tapped location"))
                selectedLatLng = latLng
            } else {
                Toast.makeText(this@CreateHuntActivity, "You need to enable permissions to display your location!", Toast.LENGTH_SHORT).show()
                val i = Intent(this@CreateHuntActivity, MainActivity::class.java)
                startActivity(i)
            }
        }
        selectLocationButton!!.setOnClickListener {
            if (selectedLatLng != null) {
                dialog!!.show()
                question!!.requestFocus()
            } else {
                Toast.makeText(this@CreateHuntActivity, "Please tap a location", Toast.LENGTH_SHORT).show()
            }
        }
        seekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                thumbView!!.visibility = View.VISIBLE
                seekBar.thumb = getThumb(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        cancel!!.setOnClickListener { dialog!!.dismiss() }
        add!!.setOnClickListener {
            if (question!!.text.toString().isNotEmpty() &&
                    answer1!!.text.toString().isNotEmpty() &&
                    answer2!!.text.toString().isNotEmpty() &&
                    answer3!!.text.toString().isNotEmpty() && getCorrectAnswer(radioGroup!!.checkedRadioButtonId) != null) {
                Coordinates.addCoordinates(selectedLatLng)
                QuestionLibrary.questions.add(question!!.text.toString())
                QuestionLibrary.choices1.add(answer1!!.text.toString())
                QuestionLibrary.choices2.add(answer2!!.text.toString())
                QuestionLibrary.choices3.add(answer3!!.text.toString())
                QuestionLibrary.correctAnswers.add(this.getCorrectAnswer(radioGroup!!.checkedRadioButtonId)!!)
                QuestionLibrary.radius.add(seekBar!!.progress)
                question!!.setText("")
                answer1!!.setText("")
                answer2!!.setText("")
                answer3!!.setText("")
                radioGroup!!.clearCheck()
                seekBar!!.progress = 0
                mMap!!.clear()
                mMap!!.addMarker(MarkerOptions()
                        .position(myPos!!)
                        .icon(BitmapDescriptorFactory.fromBitmap(LocUtils.resizeMapIcons(this@CreateHuntActivity, "marker_red")))
                        .title("My position"))
                for (latLng in Coordinates.coordinatesList) {
                    mMap!!.addMarker(MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(LocUtils.resizeMapIcons(this@CreateHuntActivity, "marker_green")))
                            .title("Tapped location"))
                }
                saveHunt!!.visibility = View.VISIBLE
                Toast.makeText(this@CreateHuntActivity, "Location added", Toast.LENGTH_SHORT).show()
                dialog!!.dismiss()
            } else {
                if (question!!.text.toString().isEmpty()) {
                    Toast.makeText(this@CreateHuntActivity, "1", Toast.LENGTH_SHORT).show()
                } else if (answer1!!.text.toString().isEmpty()) {
                    Toast.makeText(this@CreateHuntActivity, "2", Toast.LENGTH_SHORT).show()
                } else if (answer2!!.text.toString().isEmpty()) {
                    Toast.makeText(this@CreateHuntActivity, "3", Toast.LENGTH_SHORT).show()
                } else if (answer3!!.text.toString().isEmpty()) {
                    Toast.makeText(this@CreateHuntActivity, "4", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@CreateHuntActivity, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
        saveHunt!!.setOnClickListener {
            val hunt = Hunt()
            hunt.answer1 = QuestionLibrary.choices1
            hunt.answer2 = QuestionLibrary.choices2
            hunt.answer3 = QuestionLibrary.choices3
            hunt.coordinates = Coordinates.coordinatesList
            hunt.correctAnswer = QuestionLibrary.correctAnswers
            hunt.radius = QuestionLibrary.radius
            hunt.questions = QuestionLibrary.questions
            hunt.title = DataHunt.titleHunt
            hunt.author = Cache.account?.displayName
            hunt.huntCode = DataHunt.titleHunt + Cache.account?.id
            hunt.isPrivate = Cache.isPrivate
            hunt.authorID = Cache.account?.id
            val gson = Gson()
            val huntString = gson.toJson(hunt)
            huntMap!!["Author"] = hunt.author.toString()
            huntMap!!["Title"] = hunt.title.toString()
            huntMap!!["HuntFile"] = huntString
            huntMap!!["Status"] = Cache.isPrivate
            cr!!.document(hunt.huntCode!!).set(huntMap!!)
            Toast.makeText(this@CreateHuntActivity, "Hunt '" + hunt.title + "' is saved", Toast.LENGTH_SHORT).show()
            Cache.listUpdated = true
            val i = Intent(this@CreateHuntActivity, MainActivity::class.java)
            startActivity(i)
        }
    }

    private fun getCorrectAnswer(radioResult: Int): String? {
        when (radioResult) {
            1 -> return answer1!!.text.toString()
            2 -> return answer2!!.text.toString()
            3 -> return answer3!!.text.toString()
        }
        return null
    }

    @SuppressLint("SetTextI18n")
    fun getThumb(progress: Int): Drawable {
        (thumbView!!.findViewById<View>(R.id.tvProgress) as TextView).text = progress.toString() + ""
        thumbView!!.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(thumbView!!.measuredWidth, thumbView!!.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        thumbView!!.layout(0, 0, thumbView!!.measuredWidth, thumbView!!.measuredHeight)
        thumbView!!.draw(canvas)
        return BitmapDrawable(resources, bitmap)
    }
}