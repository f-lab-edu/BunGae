package com.example.bungae.ui.map

import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.bungae.databinding.FragmentMapBinding
import com.example.bungae.ui.mypage.PostListViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap

    private val mapViewModel: MapViewModel by viewModels()

    private var mapFragment: SupportMapFragment? = null

    private val permissionList = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
    )

    private val requestMultiplePermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            result.forEach {
                if (!it.value) {
                    Toast.makeText(context, "${it.key}권한 허용 필요", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        requestMultiplePermission.launch(permissionList)

        mapFragment = childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment?

        mapFragment?.getMapAsync(this)

        mapViewModel.getItemList()

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        binding.fabCurrentLocation.setOnClickListener {
            getMyLocation()
        }

        mapViewModel.itemList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            var location: Location
            for (i in it) {
                location = searchLocation(i.address)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 16f))
                setMarker(i.title)
                getMyLocation()
            }
        })

        binding.imageSearchMap.setOnClickListener {
            if (binding.editSearchMap.text.toString().isBlank()) {
                Toast.makeText(context, "주소를 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
               val location = searchLocation(binding.editSearchMap.text.toString())
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 16f))
                setMarker(binding.editSearchMap.text.toString())
            }
        }


    }

    private fun getMyLocation() {
        val locationProvider = LocationProvider(requireContext())
        val latitude = locationProvider.getLocationLatitude()
        val longitude = locationProvider.getLocationLongitude()
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 16f))
        setMarker("현재 위치")
    }

    private fun searchLocation(address: String) : Location {
        return try {
            Geocoder(requireContext(), Locale.KOREA).getFromLocationName(address, 1)?.let {
                Location("").apply {
                    latitude = it[0].latitude
                    longitude = it[0].longitude
                }
            } ?: Location("").apply {
                latitude = 0.0
                longitude = 0.0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            searchLocation(address)
        }
    }

    private fun setMarker(title: String) {
        mMap.let {
            val markerOptions = MarkerOptions()
            markerOptions.position(it.cameraPosition.target)
            markerOptions.title(title)
            val marker = it.addMarker(markerOptions)

            it.setOnCameraMoveListener {
                marker?.let { marker ->
                    marker.position = it.cameraPosition.target
                }
            }
        }
    }
}