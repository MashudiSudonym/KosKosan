package c.m.koskosan.ui.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import c.m.koskosan.R
import c.m.koskosan.ui.detail.DetailActivity
import c.m.koskosan.util.*
import c.m.koskosan.vo.ResponseState
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapsFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var loadingAnimationView: LottieAnimationView? = null
    private var errorAnimationView: LottieAnimationView? = null
    private val mapsViewModel: MapsViewModel by viewModel()
    private var deviceLocationLatitude: Double? = 0.0
    private var deviceLocationLongitude: Double? = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize view
        loadingAnimationView = requireActivity().findViewById(R.id.anim_loading)
        errorAnimationView = requireActivity().findViewById(R.id.anim_error)

        // Initialize fused location provider
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        // get device coordinate
        getLastLocation()

        // get locations data
        mapsViewModel.getLocations().observe(viewLifecycleOwner, { response ->
            if (response != null) when (response) {
                is ResponseState.Error -> showErrorStateView() // Error state
                is ResponseState.Loading -> showLoadingStateView() // loading state
                is ResponseState.Success -> {
                    // on success state
                    showSuccessStateView()

                    // show maps
                    val mapFragment =
                        childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

                    mapFragment?.getMapAsync { googleMap ->
                        // setup ui and camera position of map
                        googleMap.run {
                            mapType = GoogleMap.MAP_TYPE_NORMAL

                            if (checkPermission()) {
                                if (isLocationEnabled()) {
                                    isMyLocationEnabled = true
                                } else {
                                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                    startActivity(intent)
                                }
                            } else {
                                requireActivity().requestPermission()
                            }

                            isMyLocationEnabled = true
                            uiSettings.isCompassEnabled = true
                            uiSettings.isZoomControlsEnabled = true
                            setPadding(16, 16, 16, 128)

                            animateCamera(
                                CameraUpdateFactory.newCameraPosition(
                                    CameraPosition.Builder()
                                        .target(
                                            LatLng(
                                                deviceLocationLatitude as Double,
                                                deviceLocationLongitude as Double
                                            )
                                        )
                                        .zoom(15f).build()
                                )
                            )
                        }

                        // show marker on map
                        response.data?.forEach { location ->
                            googleMap.run {
                                val marker = MarkerOptions().position(
                                    LatLng(
                                        location.coordinate?.latitude as Double,
                                        location.coordinate?.longitude as Double
                                    )
                                ).title(location.name).snippet(location.address)

                                // setup custom marker icon
                                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_marker))

                                // add marker on map
                                addMarker(marker).tag = location.uid

                                // enable marker click
                                setOnMarkerClickListener { markerOnClick ->
                                    markerOnClick.showInfoWindow()
                                    false
                                }

                                // enable info window click
                                setOnInfoWindowClickListener { infoWindow ->
                                    infoWindow.tag
                                    infoWindow.title

                                    val detailActivityIntent = Intent(
                                        requireActivity(),
                                        DetailActivity::class.java
                                    ).apply {
                                        putExtra(Constants.UID, infoWindow.tag.toString())
                                    }
                                    startActivity(detailActivityIntent)
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    // handle success state of view
    private fun showSuccessStateView() {
        loadingAnimationView?.invisible()
        errorAnimationView?.invisible()
    }

    // handle error state of view
    private fun showErrorStateView() {
        errorAnimationView?.visible()
        loadingAnimationView?.invisible()
    }

    // handle loading state of view
    private fun showLoadingStateView() {
        loadingAnimationView?.visible()
        errorAnimationView?.invisible()
    }

    // request last location
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        deviceLocationLatitude = location.latitude
                        deviceLocationLongitude = location.longitude
                    }
                }
            } else {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requireActivity().requestPermission()
        }
    }

    // request new location
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        @Suppress("DEPRECATION") val locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback,
            Looper.myLooper()
        )
    }

    // callback location fuse
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation
            deviceLocationLatitude = lastLocation.latitude
            deviceLocationLongitude = lastLocation.longitude
        }
    }

    // check enable location source status
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // check ACCESS FINE LOCATION and ACCESS COARSE LOCATION permission
    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.PERMISSION_REQUEST_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }
}