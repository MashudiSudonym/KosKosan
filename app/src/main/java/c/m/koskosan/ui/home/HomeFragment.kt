package c.m.koskosan.ui.home

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
import c.m.koskosan.data.model.LocationDistanceResponse
import c.m.koskosan.databinding.FragmentHomeBinding
import c.m.koskosan.ui.detail.DetailActivity
import c.m.koskosan.util.Constants.Companion.PERMISSION_REQUEST_LOCATION
import c.m.koskosan.util.Constants.Companion.UID
import c.m.koskosan.util.gone
import c.m.koskosan.util.invisible
import c.m.koskosan.util.visible
import c.m.koskosan.vo.ResponseState
import com.google.android.gms.location.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal
import java.math.RoundingMode


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var layout: View
    private var deviceLocationLatitude: Double? = 0.0
    private var deviceLocationLongitude: Double? = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var deviceCoordinate: Location? = null
    private var locationList: ArrayList<LocationDistanceResponse> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // for handling item view utilities
        layout = view

        // Initialize fused location provider
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient((activity as AppCompatActivity))

        // get device coordinate
        getLastLocation()

        // appbar title setup
        (activity as AppCompatActivity?)?.setSupportActionBar(binding.toolbarHome)
        (activity as AppCompatActivity?)?.supportActionBar?.apply {
            title = getString(R.string.app_name)
        }

        // initialize get data from database
        initializeGetLocationData()

        // swipe to refresh function
        binding.homeSwipeRefreshLayout.setOnRefreshListener {
            binding.homeSwipeRefreshLayout.isRefreshing = false
            // get device coordinate
            getLastLocation()
            // get data
            initializeGetLocationData()
        }

        return view
    }

    // initialize get data from database
    private fun initializeGetLocationData() {
        homeViewModel.getLocations().observe(viewLifecycleOwner, { response ->
            if (response != null) when (response) {
                is ResponseState.Error -> showErrorStateView() // error state
                is ResponseState.Loading -> {
                    // clear list
                    locationList.clear()

                    // loading state
                    showLoadingStateView()
                }
                is ResponseState.Success -> {
                    // success state
                    showSuccessStateView()

                    // gps coordinate and calculate the distance
                    deviceCoordinate = Location("deviceLocation").apply {
                        latitude = deviceLocationLatitude as Double
                        longitude = deviceLocationLongitude as Double
                    }

                    response.data?.forEach { result ->
                        val locationCoordinate = Location("targetLocation").apply {
                            latitude = result.coordinate?.latitude as Double
                            longitude = result.coordinate?.longitude as Double
                        }

                        val distance = BigDecimal(
                            deviceCoordinate?.distanceTo(locationCoordinate)?.div(1000)
                                ?.toDouble() as Double
                        ).setScale(2, RoundingMode.HALF_EVEN).toDouble()
                        val locationWithDistance = LocationDistanceResponse(
                            result.uid,
                            result.name,
                            result.address,
                            result.phone,
                            result.coordinate,
                            result.photo,
                            result.type,
                            result.googlePlace,
                            distance
                        )

                        locationList.add(locationWithDistance)
                        locationList.sortBy { it.distance }
                    }

                    // add data to recycler view adapter
                    homeAdapter = HomeAdapter { locationResponse ->
                        val detailActivityIntent =
                            Intent(requireActivity(), DetailActivity::class.java).apply {
                                putExtra(UID, locationResponse.uid)
                            }
                        startActivity(detailActivityIntent)
                    }
                    homeAdapter.submitList(locationList)
                    binding.rvLocation.adapter = homeAdapter
                    binding.rvLocation.setHasFixedSize(true)
                }
            }
        })
    }

    // handle success state of view
    private fun showSuccessStateView() {
        binding.animLoading.gone()
        binding.animError.gone()
        binding.homeLayout.visible()
    }

    // handle error state of view
    private fun showErrorStateView() {
        binding.animError.visible()
        binding.animLoading.gone()
        binding.homeLayout.invisible()
    }

    // handle loading state of view
    private fun showLoadingStateView() {
        binding.homeLayout.invisible()
        binding.animLoading.visible()
        binding.animError.gone()
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
            requestPermission()
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
            locationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    // callback location fuse
    private val mLocationCallback = object : LocationCallback() {
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

    // request permission for get location
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_LOCATION
        )
    }

    // permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }
}