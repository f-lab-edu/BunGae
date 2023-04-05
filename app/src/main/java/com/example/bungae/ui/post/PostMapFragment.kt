package com.example.bungae.ui.post

import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.bungae.databinding.FragmentPostMapBinding
import com.example.bungae.ui.map.LocationProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class PostMapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentPostMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mMap: GoogleMap

    private var mapFragment: SupportMapFragment? = null

    private val postViewModel by lazy {
        ViewModelProvider(requireActivity()).get(PostViewModel::class.java)
    }
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

        _binding = FragmentPostMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        requestMultiplePermission.launch(permissionList)

        mapFragment = childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment?

        mapFragment?.getMapAsync(this)

        binding.linearlayoutSelectAddress.setOnClickListener {
            val address: Address?
            mMap.let {
                address = getAddress(
                    it.cameraPosition.target.latitude,
                    it.cameraPosition.target.longitude
                )
            }
//            setFragmentResult("requestKey", bundleOf("address" to address?.getAddressLine(0)))
            postViewModel.sendCoordinates(address)
        }

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        getMyLocation()

        binding.fabCurrentLocation.setOnClickListener {
            getMyLocation()
        }

        binding.imageSearchMap.setOnClickListener {
            if (binding.editSearchMap.text.toString().isBlank()) {
                Toast.makeText(context, "주소를 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                val location = searchLocation(binding.editSearchMap.text.toString())
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            location.latitude,
                            location.longitude
                        ), 16f
                    )
                )
                setMarker()
            }
        }
    }

    private fun getMyLocation() {
        val locationProvider = LocationProvider(requireContext())
        val latitude = locationProvider.getLocationLatitude()
        val longitude = locationProvider.getLocationLongitude()
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), 16f))
        setMarker()
    }

    private fun searchLocation(address: String): Location {
        return try {
            Geocoder(requireContext(), Locale.getDefault()).getFromLocationName(address, 1)?.let {
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

    private fun getAddress(latitude: Double, longitude: Double): Address? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        val addresses: List<Address>? = try {
            //Geocoder 객체를 이용하여 위도와 경도로부터 리스트를 가져옴
            geocoder.getFromLocation(latitude, longitude, 7)
        } catch (ioException: IOException) {
            Toast.makeText(context, "지오코더 서비스 사용불가합니다.", Toast.LENGTH_LONG).show()
            return null
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(context, "잘못된 위도, 경도 입니다.", Toast.LENGTH_LONG).show()
            return null
        }

        //에러는 아니지만 주소가 발견되지 않은 경우
        if (addresses == null || addresses.size == 0) {
            Toast.makeText(context, "주소과 발견되지 않았습니다.", Toast.LENGTH_LONG).show()
            return null
        }
        return addresses[0]
    }

    private fun setMarker() {
        mMap.let {
            it.clear()
            val markerOptions = MarkerOptions()
            markerOptions.position(it.cameraPosition.target)
            markerOptions.title("현재 위치")
            val marker = it.addMarker(markerOptions)

            it.setOnCameraMoveListener {
                marker?.let { marker ->
                    marker.position = it.cameraPosition.target
                }
            }
        }
    }
}