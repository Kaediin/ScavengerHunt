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
import com.example.mobileappdevelopment.utils.Cache
import com.example.mobileappdevelopment.utils.Coordinates
import com.example.mobileappdevelopment.utils.DataHunt
import com.example.mobileappdevelopment.utils.LocUtils
import com.example.mobileappdevelopment.library.QuestionLibrary
import com.example.mobileappdevelopment.model.Hunt
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
import kotlinx.android.synthetic.main.activity_create_hunt.*
import kotlinx.android.synthetic.main.dialog_create_question.*
import kotlinx.android.synthetic.main.dialog_create_question.view.*
import java.util.*

class CreateHuntActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var selectedLatLng: LatLng? = null
    private var dialog: AlertDialog? = null
    private var popupView: View? = null

    private var myPos: LatLng? = null
    private var thumbView: View? = null
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
        start_hunt!!.visibility = View.GONE
        loading_panel!!.visibility = View.VISIBLE
        loading_circle!!.visibility = View.VISIBLE
        select_location_button!!.visibility = View.GONE
        val handler = Handler()
        handler.postDelayed({
            loading_panel!!.visibility = View.GONE
            loading_circle!!.visibility = View.GONE
            select_location_button!!.visibility = View.VISIBLE
        }, 2000)
    }

    @SuppressLint("InflateParams")
    fun assignVars() {
        val builder = AlertDialog.Builder(this@CreateHuntActivity)
        val layoutInflater = LayoutInflater.from(this@CreateHuntActivity)
        @SuppressLint("InflateParams") val popupDialogView = layoutInflater.inflate(R.layout.dialog_create_question, null)
        popupView = popupDialogView
        builder.setView(popupDialogView)
        builder.setCancelable(false)
        dialog = builder.create()

        selectedLatLng = null
        thumbView = LayoutInflater.from(popupDialogView.context).inflate(R.layout.seekbar_layout_thumb, null, false)
    }

    fun clearLists() {
        Coordinates.coordinates.clear()
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
                for (chosenlatLng in Coordinates.coordinates) {
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
        select_location_button!!.setOnClickListener {
            if (selectedLatLng != null) {
                dialog!!.show()
                popupView?.new_question?.requestFocus()
            } else {
                Toast.makeText(this@CreateHuntActivity, "Please tap a location", Toast.LENGTH_SHORT).show()
            }
        }
        popupView?.seekbar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                thumbView!!.visibility = View.VISIBLE
                seekBar.thumb = getThumb(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        popupView?.cancel_loc?.setOnClickListener { dialog!!.dismiss() }
        popupView?.add_new_loc?.setOnClickListener {
            if (popupView?.new_question!!.text.toString().isNotEmpty() &&
                    popupView?.edit_field_1!!.text.toString().isNotEmpty() &&
                    popupView?.edit_field_2!!.text.toString().isNotEmpty() &&
                    popupView?.edit_field_3!!.text.toString().isNotEmpty() && getCorrectAnswer(popupView?.radio_group!!.checkedRadioButtonId) != null) {
                Coordinates.addCoordinates(selectedLatLng)
                QuestionLibrary.questions.add(popupView?.new_question!!.text.toString())
                QuestionLibrary.choices1.add(popupView?.edit_field_1!!.text.toString())
                QuestionLibrary.choices2.add(popupView?.edit_field_2!!.text.toString())
                QuestionLibrary.choices3.add(popupView?.edit_field_3!!.text.toString())
                QuestionLibrary.correctAnswers.add(this.getCorrectAnswer(popupView?.radio_group!!.checkedRadioButtonId)!!)
                QuestionLibrary.radius.add(popupView?.seekbar!!.progress)
                popupView?.new_question?.setText("")
                popupView?.edit_field_1!!.setText("")
                popupView?.edit_field_2!!.setText("")
                popupView?.edit_field_3!!.setText("")
                popupView?.radio_group!!.clearCheck()
                popupView?.seekbar!!.progress = 0
                mMap!!.clear()
                mMap!!.addMarker(MarkerOptions()
                        .position(myPos!!)
                        .icon(BitmapDescriptorFactory.fromBitmap(LocUtils.resizeMapIcons(this@CreateHuntActivity, "marker_red")))
                        .title("My position"))
                for (latLng in Coordinates.coordinates) {
                    mMap!!.addMarker(MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(LocUtils.resizeMapIcons(this@CreateHuntActivity, "marker_green")))
                            .title("Tapped location"))
                }
                start_hunt!!.visibility = View.VISIBLE
                Toast.makeText(this@CreateHuntActivity, "Location added", Toast.LENGTH_SHORT).show()
                dialog!!.dismiss()
            } else {
                if (popupView?.new_question!!.text.toString().isEmpty()) {
                    Toast.makeText(this@CreateHuntActivity, "1", Toast.LENGTH_SHORT).show()
                } else if (popupView?.edit_field_1!!.text.toString().isEmpty()) {
                    Toast.makeText(this@CreateHuntActivity, "2", Toast.LENGTH_SHORT).show()
                } else if (popupView?.edit_field_2!!.text.toString().isEmpty()) {
                    Toast.makeText(this@CreateHuntActivity, "3", Toast.LENGTH_SHORT).show()
                } else if (popupView?.edit_field_3!!.text.toString().isEmpty()) {
                    Toast.makeText(this@CreateHuntActivity, "4", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@CreateHuntActivity, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
        start_hunt!!.setOnClickListener {
            val hunt = Hunt()
            hunt.answer1 = QuestionLibrary.choices1
            hunt.answer2 = QuestionLibrary.choices2
            hunt.answer3 = QuestionLibrary.choices3
            hunt.coordinates = Coordinates.coordinates
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
            1 -> return popupView?.edit_field_1!!.text.toString()
            2 -> return popupView?.edit_field_2!!.text.toString()
            3 -> return popupView?.edit_field_3!!.text.toString()
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