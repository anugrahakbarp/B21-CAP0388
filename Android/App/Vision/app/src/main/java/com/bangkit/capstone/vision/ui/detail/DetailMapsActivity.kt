package com.bangkit.capstone.vision.ui.detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.vision.R
import com.bangkit.capstone.vision.model.ReportEntity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class DetailMapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap

    private lateinit var db: FirebaseFirestore

    private var report: ReportEntity? = null

    companion object {
        const val EXTRA_REPORT = "extra_report"
    }

    private var latLng: LatLng = LatLng(-6.7674946, 107.7044076)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_maps)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.location)

        db = FirebaseFirestore.getInstance()

        report = intent.getParcelableExtra(EXTRA_REPORT)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun addMarkerMap(lat: Double, long: Double, address: String, note: String) {
        mMap.addMarker(
            MarkerOptions().position(LatLng(lat, long))
                .title(address)
                .snippet(note)
                .draggable(true)
        )
    }

    private fun animateCameraMap(lat: Double, long: Double, zoom: Float) {
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(lat, long), zoom)
        )
    }

    private fun setInfoWindow() {
        mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                val view = layoutInflater.inflate(R.layout.item_marker, null)
                view.layoutParams =
                    RelativeLayout.LayoutParams(
                        500,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    )
                val tvReport = view.findViewById<TextView>(R.id.locationAddress)
                val tvNote = view.findViewById<TextView>(R.id.note)
                tvReport.text = marker.title
                tvNote.text = marker.snippet
                return view
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (report != null) {
            addMarkerMap(
                report!!.latitude,
                report!!.longitude,
                report!!.address,
                report!!.note.toString()
            )
            animateCameraMap(report!!.latitude, report!!.longitude, 17.5f)
            setInfoWindow()
        } else {
            animateCameraMap(latLng.latitude, latLng.longitude, 20.5f)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onInfoWindowClick(marker: Marker) {
        Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show()
    }

}