package com.cplaygr.sample.jetpackdatastoresample

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import com.cplaygr.sample.jetpackdatastoresample.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "_MainActivity"
    }

    private lateinit var activityMainBinding: ActivityMainBinding

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

    lateinit var userManager: UserManager
    var age = 0
    var fname = ""
    var lname = ""
    var gender = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        userManager = UserManager(dataStore)

        buttonSave()

        observeData()
    }

    private fun observeData() {
        userManager.userAgeFlow.asLiveData().observe(this, {
            if (it != null) {
                age = it
                activityMainBinding.tvAge.text = it.toString()
            }
        })

        userManager.userFirstNameFlow.asLiveData().observe(this, {
            if (it != null) {
                Log.d(TAG, "observeData() called fname: $it")
                fname = it
                activityMainBinding.tvFname.text = it.toString()
            }
        })

        //Updates lastname
        userManager.userLastNameFlow.asLiveData().observe(this, {
            if (it != null) {
                lname = it
                activityMainBinding.tvLname.text = it
            }
        })

        //Updates gender
        userManager.userGenderFlow.asLiveData().observe(this, {
            if (it != null) {
                gender = if (it) "Male" else "Female"
                activityMainBinding.tvGender.text = gender
            }
        })
    }

    private fun buttonSave() {

        //Gets the user input and saves it
        activityMainBinding.btnSave.setOnClickListener {
            fname = activityMainBinding.etFname.text.toString()
            lname = activityMainBinding.etLname.text.toString()
            age = activityMainBinding.etAge.text.toString().toInt()
            val isMale = activityMainBinding.switchGender.isChecked

            //Stores the values
            GlobalScope.launch {
                userManager.storeUser(age, fname, lname, isMale)
            }
        }
    }
}