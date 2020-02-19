package com.example.mobileappdevelopment.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mobileappdevelopment.DataUtils.Cache
import com.example.mobileappdevelopment.DataUtils.DataHunt
import com.example.mobileappdevelopment.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private var manager: LocationManager? = null
    private var titleHunt: EditText? = null
    private var privateCheckbox: CheckBox? = null
    private var saveTitleHunt: Button? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var dialog: AlertDialog? = null
    private var account: GoogleSignInAccount? = null
    private var fb: FirebaseFirestore? = null
    private var canShowDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val builder = AlertDialog.Builder(this@MainActivity)
        val layoutInflater = LayoutInflater.from(this@MainActivity)
        @SuppressLint("InflateParams") val popupDialogView = layoutInflater.inflate(R.layout.title_select_dialog, null)
        builder.setView(popupDialogView)
        dialog = builder.create()
        fb = FirebaseFirestore.getInstance()
        saveTitleHunt = popupDialogView.findViewById(R.id.button_save_hunt_title)
        titleHunt = popupDialogView.findViewById(R.id.edit_text_hunt_title)
        privateCheckbox = popupDialogView.findViewById(R.id.checkbox_status)
        val createNewButton = findViewById<Button>(R.id.button_create_new)
        val gotoSelected = findViewById<Button>(R.id.button_start_selected)
        manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        Cache.query = fb!!.collection("Scavenger_Hunts").get()

        createNewButton.setOnClickListener {
            canShowDialog = true
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (account == null) {
                    signIn()
                } else {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                }
            } else {
                if (manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    dialog?.show()
                    saveTitleHunt?.setOnClickListener { setTitleHunt() }
                } else {
                    buildAlertMessageNoGps()
                }
            }
        }

        gotoSelected.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (account == null) {
                    signIn()
                } else {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                }
            } else {
                if (manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val i = Intent(this@MainActivity, ChooseHuntActivity::class.java)
                    startActivity(i)
                } else {
                    buildAlertMessageNoGps()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null) {
            signIn()
        } else {
            Cache.account = account
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, 800)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 800) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            Toast.makeText(this, "Welcome " + account?.displayName, Toast.LENGTH_SHORT).show()
            val intent = intent
            finish()
            startActivity(intent)
        } catch (e: ApiException) { // The ApiException status code indicates the detailed failure reason.
// Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("DebugFirebase", "" + e.cause)
            Toast.makeText(this, "Sign in rejected. You must be signed in to use these services", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) && canShowDialog) {
                    dialog!!.show()
                } else if (manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) && !canShowDialog) {
                    val i = Intent(this@MainActivity, ChooseHuntActivity::class.java)
                    startActivity(i)
                } else {
                    buildAlertMessageNoGps()
                }
            }
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    private fun setTitleHunt() {
        if (titleHunt!!.text.toString().isEmpty()) {
            Toast.makeText(this, "Title cant be null", Toast.LENGTH_SHORT).show()
        } else {
            val title = titleHunt!!.text.toString()
            val huntCode = title + account!!.id
            var isUnique = true
            for (hunt in Cache.allHunts) {
                if (hunt.huntCode == huntCode) {
                    isUnique = false
                }
            }
            if (isUnique) {
                Cache.isPrivate = privateCheckbox!!.isChecked
                DataHunt.titleHunt = title
                val i = Intent(this@MainActivity, CreateHuntActivity::class.java)
                startActivity(i)
            } else {
                Toast.makeText(this, "Title is already occupied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}