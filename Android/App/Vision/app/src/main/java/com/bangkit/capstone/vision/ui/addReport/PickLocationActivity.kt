package com.bangkit.capstone.vision.ui.addReport

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.capstone.vision.BuildConfig
import com.bangkit.capstone.vision.R
import com.bangkit.capstone.vision.api.DistanceApi
import com.bangkit.capstone.vision.api.DistanceConfig
import com.bangkit.capstone.vision.api.DistanceResponse
import com.bangkit.capstone.vision.databinding.ActivityPickLocationBinding
import com.bangkit.capstone.vision.model.LocationModel
import com.bangkit.capstone.vision.utils.AppExecutors
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.util.CollectionUtils
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.InetAddress


class PickLocationActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnCameraIdleListener, LocationListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    companion object {
        private var EXTRA_LOCATION = "extra_location"
        private var EXTRA_DISTANCE = "extra_distance"
        private var VALIDATE_CODE = "validate"
        private var REPORT_CODE = "report"
        private var SNACKBAR_SUCCESS_CODE = "snackbar_success"
        private var SNACKBAR_ERROR_CODE = "snackbar_error"
    }

    //my location
    private lateinit var client: FusedLocationProviderClient

    //search location
    private lateinit var mLastLocation: Location
    private var mCurrLocationMarker: Marker? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private lateinit var mLocationRequest: LocationRequest

    private lateinit var mMap: GoogleMap

    private lateinit var locationModel: LocationModel

    private lateinit var activityPickLocationBinding: ActivityPickLocationBinding

    private val appExecutors: AppExecutors = AppExecutors()

    private var latLng: LatLng = LatLng(-6.7674946, 107.7044076)

    private var userLatLng: LatLng? = null

    private lateinit var progress: AlertDialog

    private val distanceData = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPickLocationBinding = ActivityPickLocationBinding.inflate(layoutInflater)
        val view = activityPickLocationBinding.root
        setContentView(view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.location)

        checkPermission()
        client = LocationServices.getFusedLocationProviderClient(this)
        client.lastLocation

        activityPickLocationBinding.mapView.onCreate(savedInstanceState)
        MapsInitializer.initialize(this)
        activityPickLocationBinding.mapView.getMapAsync(this)

        locationModel = LocationModel()

        activityPickLocationBinding.btnMyLocation.setOnClickListener {
            getCurrentLocation()
        }

        activityPickLocationBinding.btnDone.setOnClickListener {
            setDistance()
            getDistance().observe(this, {
                val location = Intent(this, AddReportFragment::class.java)
                location.putExtra(EXTRA_LOCATION, locationModel)
                location.putExtra(EXTRA_DISTANCE, it)
                setResult(Activity.RESULT_OK, location)
                finish()
            })
        }

        checkNetwork()
        checkLocation()
        getCurrentLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Declare and Clear
        mMap = googleMap
        mMap.clear()

        // On Camera Position Idle
        mMap.setOnCameraIdleListener(this)
        mMap.uiSettings.isScrollGesturesEnabled = false
        mMap.uiSettings.isZoomGesturesEnabled = false
        mMap.uiSettings.setAllGesturesEnabled(false)

        buildGoogleApiClient()
    }

    override fun onLocationChanged(location: Location) {
        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11f))

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.getFusedLocationProviderClient(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val search = menu.findItem(R.id.search)
        search.isVisible = true

        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.queryHint = resources.getString(R.string.search_address)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchLocation(query.trim())
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    fun searchLocation(query: String) {
        val geoCoder = Geocoder(this)
        try {
            val addressList = geoCoder.getFromLocationName(query, 1)
            if (addressList.isNotEmpty()) {
                val address = addressList[0]
                val latLng = LatLng(address.latitude, address.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f))
            } else {
                snackBar(
                    activityPickLocationBinding.root,
                    getString(R.string.search_location_failed),
                    SNACKBAR_ERROR_CODE
                ).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun isNetworkConnected(mContext: Context): Boolean {
        val connectivityManager: ConnectivityManager =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetwork != null && connectivityManager.activeNetworkInfo?.isConnected!!
    }

    private fun isInternetConnected(): Boolean {
        val google = InetAddress.getByName("google.com")
        return !google.equals("")
    }

    private fun checkNetwork() {
        appExecutors.networkIO().execute {
            if (isNetworkConnected(this)) {
                if (!isInternetConnected()) {
                    appExecutors.mainThread().execute {
                        val alertDialog = AlertDialog.Builder(this)
                        alertDialog.setCancelable(true)
                        alertDialog.setTitle(getString(R.string.internet_required))
                        alertDialog.setMessage(getString(R.string.connect_internet))
                        alertDialog.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            checkNetwork()
                            checkLocation()
                            dialog.dismiss()
                            mMap.uiSettings.isScrollGesturesEnabled = false
                            mMap.uiSettings.isZoomGesturesEnabled = false
                            mMap.uiSettings.setAllGesturesEnabled(false)
                        }
                        alertDialog.show()
                    }
                }
            } else {
                appExecutors.mainThread().execute {
                    val alertDialog = AlertDialog.Builder(this)
                    alertDialog.setCancelable(false)
                    alertDialog.setTitle(getString(R.string.connection_required))
                    alertDialog.setMessage(getString(R.string.enable_connection))
                    alertDialog.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                        checkNetwork()
                        dialog.dismiss()
                        mMap.uiSettings.isScrollGesturesEnabled = false
                        mMap.uiSettings.isZoomGesturesEnabled = false
                    }
                    alertDialog.show()
                }
            }
        }
    }

    private fun checkLocation() {
        if (!isLocationEnabled(this)) {
            appExecutors.mainThread().execute {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setCancelable(false)
                alertDialog.setTitle(getString(R.string.location_required))
                alertDialog.setMessage(getString(R.string.enable_location))
                alertDialog.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    checkNetwork()
                    checkLocation()
                    dialog.dismiss()
                }
                if (window.decorView.rootView.isShown) {
                    alertDialog.show()
                }
            }
        } else {
            appExecutors.mainThread().execute {
                getCurrentLocation()
            }
        }
    }

    private fun isLocationEnabled(mContext: Context): Boolean {
        val lm = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }
    }

    private fun getCurrentLocation() {
        checkPermission()
        activityPickLocationBinding.btnDone.isEnabled = false
        if (isLocationEnabled(this)) {
            if (mGoogleApiClient != null) {
                val request = LocationRequest()
                request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                client.requestLocationUpdates(request, object : LocationCallback() {
                    override fun onLocationResult(location: LocationResult) {
                        if (location != null) {
                            userLatLng = LatLng(
                                location.lastLocation.latitude,
                                location.lastLocation.longitude
                            )
                            mMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    userLatLng!!,
                                    15.0f
                                )
                            )
                            mMap.uiSettings.isScrollGesturesEnabled = false
                            mMap.uiSettings.isZoomGesturesEnabled = false
                            mMap.uiSettings.setAllGesturesEnabled(false)
                        }
                    }
                }, null).addOnFailureListener {
                    appExecutors.mainThread().execute {
                        val alertDialog = AlertDialog.Builder(this)
                        alertDialog.setCancelable(false)
                        alertDialog.setTitle(getString(R.string.connection_required))
                        alertDialog.setMessage(
                            "${getString(R.string.ensure_good_connection)}\n${
                                getString(
                                    R.string.please_onoff_location
                                )
                            }"
                        )
                        alertDialog.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            dialog.dismiss()
                            getCurrentLocation()
                        }
                        alertDialog.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                            dialog.dismiss()
                            finish()
                        }
                        alertDialog.show()
                    }
                }
            }
        } else {
            appExecutors.mainThread().execute {
                snackBar(
                    activityPickLocationBinding.root,
                    getString(R.string.enable_location),
                    SNACKBAR_ERROR_CODE
                ).show()
            }
        }
    }

    override fun onConnected(bundle: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 30000
        mLocationRequest.fastestInterval = 10000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LocationServices.getFusedLocationProviderClient(this)
        }
    }

    override fun onCameraIdle() {
        mMap.cameraPosition.let {
            latLng = mMap.cameraPosition.target
            var address = ""
            var adminArea = ""
            var subAdminArea = ""
            var locality = ""
            var subLocality = ""
            var locLatitude: String
            var locLongitude: String
            try {
                val geoCoder = Geocoder(this)
                val addresses = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (!CollectionUtils.isEmpty(addresses)) {
                    val fetch = addresses[0]
                    if (fetch.maxAddressLineIndex > -1) {
                        Log.d("tag", "Get location men $fetch")
                        address = fetch.getAddressLine(0)
                        fetch.adminArea?.let {
                            adminArea = it
                        }
                        fetch.subAdminArea?.let {
                            subAdminArea = it
                        }
                        fetch.locality?.let {
                            locality = it
                        }
                        fetch.subLocality?.let {
                            subLocality = it
                        }
                        fetch.latitude.let {
                            locLatitude = it.toString()
                        }
                        fetch.longitude.let {
                            locLongitude = it.toString()
                        }
                        locationModel.apply {
                            locationAddress = address
                            locationAdmin = adminArea
                            locationSubAdmin = subAdminArea
                            locationLocality = locality
                            locationSubLocality = subLocality
                            locationLatitude = locLatitude
                            locationLongitude = locLongitude
                        }
                    }
                    activityPickLocationBinding.addressEditText.setText(address)
                }
                if (userLatLng != null) {
                    activityPickLocationBinding.btnDone.isEnabled = true
                    mMap.uiSettings.isScrollGesturesEnabled = true
                    mMap.uiSettings.isZoomGesturesEnabled = true
                    mMap.uiSettings.setAllGesturesEnabled(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                checkNetwork()
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setCancelable(false)
                alertDialog.setTitle(getString(R.string.failed_get_location))
                alertDialog.setMessage(getString(R.string.ensure_good_connection))
                alertDialog.setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                }
                alertDialog.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                alertDialog.show()
                activityPickLocationBinding.btnDone.isEnabled = false
            }
        }
    }

    private fun snackBar(view: View, message: String, status: String): Snackbar {
        val layout = activityPickLocationBinding.clLocationValid
        val snackBar = Snackbar.make(
            layout, message, Snackbar.LENGTH_LONG
        )
        val viewSnackBar = snackBar.view
        val params: FrameLayout.LayoutParams =
            view.layoutParams as FrameLayout.LayoutParams
        params.width = FrameLayout.LayoutParams.MATCH_PARENT
        view.layoutParams = params
        layout.bringToFront()
        if (status == SNACKBAR_SUCCESS_CODE) {
            viewSnackBar.setBackgroundColor(
                Color.TRANSPARENT
            )
            layout.background =
                ResourcesCompat.getDrawable(resources, R.drawable.all_corner_ten_radius_green, null)
        } else if (status == SNACKBAR_ERROR_CODE) {
            viewSnackBar.setBackgroundColor(
                Color.TRANSPARENT
            )
            layout.background =
                ResourcesCompat.getDrawable(resources, R.drawable.all_corner_ten_radius_red, null)
        }
        return snackBar
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        activityPickLocationBinding.mapView.onResume()
        super.onResume()
    }

    override fun onPause() {
        activityPickLocationBinding.mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        activityPickLocationBinding.mapView.onDestroy()
        super.onDestroy()

    }

    override fun onLowMemory() {
        activityPickLocationBinding.mapView.onLowMemory()
        super.onLowMemory()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}

    override fun onConnectionSuspended(i: Int) {}

    private fun setDistance() {
        val client = DistanceConfig.instance?.retrofit?.create(DistanceApi::class.java)
        val userAddress = "${userLatLng!!.latitude},${userLatLng!!.longitude}"
        val roadAddress = "${locationModel.locationLatitude},${locationModel.locationLongitude}"
        val call =
            client!!.getDistance(
                userAddress,
                roadAddress,
                BuildConfig.API_KEY
            )
        call.enqueue(object : Callback<DistanceResponse?> {
            override fun onResponse(
                call: Call<DistanceResponse?>?,
                response: Response<DistanceResponse?>
            ) {
                if (response.body() != null) {
                    distanceData.postValue(
                        response.body()!!.rows.elementAt(0).elements.elementAt(
                            0
                        ).distance.text
                    )
                }
            }

            override fun onFailure(call: Call<DistanceResponse?>?, t: Throwable?) {}
        })
    }

    private fun getDistance(): LiveData<String> {
        if (distanceData.value == null) {
            setDistance()
        }
        return distanceData
    }
}