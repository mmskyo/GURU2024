package com.example.guru2024

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_map)

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize Google Maps
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Check and request location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            showCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        // 사용자가 지도에서 위치를 클릭하면 해당 위치를 선택
        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear() // 기존 마커 삭제
            googleMap.addMarker(MarkerOptions().position(latLng).title("선택한 위치"))

            // 위도, 경도를 주소로 변환
            getAddressFromLatLng(latLng) { locationString ->
                // 선택된 위치를 HomeFragment로 전달
                val resultIntent = Intent()
                resultIntent.putExtra("LOCATION", locationString) // 위치 문자열 전달
                setResult(Activity.RESULT_OK, resultIntent)
                finish() // 현재 액티비티 종료
            }
        }
    }

    private fun showCurrentLocation() {
        // Replace with dynamic location if needed
        val myLocation = LatLng(37.5665, 126.9780) // Example: Seoul
        googleMap.addMarker(MarkerOptions().position(myLocation).title("현재 위치"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
    }

    // 위도, 경도를 주소 문자열로 변환 (API 33 이상 지원)
    private fun getAddressFromLatLng(latLng: LatLng, callback: (String) -> Unit) {
        val geocoder = Geocoder(this, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // API 33 이상 (비동기 방식)
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) { addresses ->
                val address = addresses?.firstOrNull()?.getAddressLine(0) ?: "주소를 찾을 수 없음"
                callback(address)
            }
        } else {
            // API 32 이하 (동기 방식)
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                val address = addresses?.firstOrNull()?.getAddressLine(0) ?: "주소를 찾을 수 없음"
                callback(address)
            } catch (e: Exception) {
                Log.e("MapActivity", "Geocoder 오류: ${e.message}")
                callback("주소 변환 실패")
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}