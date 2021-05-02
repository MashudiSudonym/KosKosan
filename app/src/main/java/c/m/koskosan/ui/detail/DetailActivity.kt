package c.m.koskosan.ui.detail

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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import c.m.koskosan.R
import c.m.koskosan.data.model.LocationResponse
import c.m.koskosan.databinding.ActivityDetailBinding
import c.m.koskosan.ui.form.order.OrderActivity
import c.m.koskosan.util.Constants
import c.m.koskosan.util.Constants.Companion.LOCATION_ADDRESS
import c.m.koskosan.util.Constants.Companion.LOCATION_NAME
import c.m.koskosan.util.Constants.Companion.LOCATION_PHONE
import c.m.koskosan.util.Constants.Companion.UID
import c.m.koskosan.util.gone
import c.m.koskosan.util.invisible
import c.m.koskosan.util.visible
import c.m.koskosan.vo.ResponseState
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal
import java.math.RoundingMode

class DetailActivity : AppCompatActivity() {
    private val detailViewModel: DetailViewModel by viewModel()
    private lateinit var detailBinding: ActivityDetailBinding
    private lateinit var layout: View
    private var uid: String? = ""
    private var deviceLocationLatitude: Double? = 0.0
    private var deviceLocationLongitude: Double? = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var deviceCoordinate: Location? = null
    private var gPlaceUrl: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize view binding
        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        val view = detailBinding.root
        setContentView(view)

        // initialize for using widget utilities
        layout = view

        // maps initialize
        detailBinding.mapLocation.onCreate(savedInstanceState)

        // Initialize fused location provider
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        // get device coordinate
        getLastLocation()

        // get parsing intent data
        uid = intent.getStringExtra(UID)

        // AppBar / ActionBar Title Setup
        setSupportActionBar(detailBinding.toolbarDetail)
        supportActionBar?.apply {
            title = getString(R.string.detail)
            setDisplayHomeAsUpEnabled(true)
        }

        // initialize get detail data
        initializeGetLocationDataByUid()

        // initialize swipe refresh data
        detailBinding.detailSwipeRefreshView.setOnRefreshListener {
            detailBinding.detailSwipeRefreshView.isRefreshing = false

            // get device coordinate
            getLastLocation()

            // get data
            initializeGetLocationDataByUid()
        }
    }

    // initialize get detail data
    private fun initializeGetLocationDataByUid() {
        detailViewModel.setUIDInput(uid.toString())
        detailViewModel.getLocationByUid().observe(this, { response ->
            if (response != null) when (response) {
                is ResponseState.Error -> showErrorStateView() // error state
                is ResponseState.Loading -> showLoadingStateView() // loading state
                is ResponseState.Success -> {
                    // success state
                    showSuccessStateView()

                    // show basic data to view
                    showBasicData(response)

                    // show distance location
                    // gps coordinate and calculate the distance
                    showDistanceLocation(response, response.data as LocationResponse)

                    // order button navigate to order activity
                    val orderActivityIntent = Intent(this, OrderActivity::class.java).apply {
                        putExtra(UID, response.data.uid)
                        putExtra(LOCATION_NAME, response.data.name)
                        putExtra(LOCATION_ADDRESS, response.data.address)
                        putExtra(LOCATION_PHONE, response.data.phone)
                    }
                    detailBinding.btnOrder.setOnClickListener { startActivity(orderActivityIntent) }
                }
            }
        })
    }

    // show basic data to view
    @SuppressLint("SetTextI18n")
    private fun showBasicData(response: ResponseState<LocationResponse>) {
        // google place link
        gPlaceUrl = response.data?.googlePlace

        // show basic data
        detailBinding.tvNameLocation.text = response.data?.name
        detailBinding.tvAddressLocation.text = response.data?.address
        detailBinding.tvPhoneLocation.text =
            "${getString(R.string.phone)} : ${response.data?.phone}"
        detailBinding.tvTypeLocation.text =
            getString(R.string.type_of) + when (response.data?.type) {
                "woman" -> getString(R.string.woman)
                "man" -> getString(R.string.man)
                else -> getString(R.string.call_admin_contact)
            }

        // show image data
        val slidePhoto = ArrayList<SlideModel>()

        response.data?.photo?.forEach {
            slidePhoto.add(SlideModel(it))
        }
        detailBinding.imgLocation.setImageList(slidePhoto, ScaleTypes.CENTER_CROP)
    }

    // show distance location
    // gps coordinate and calculate the distance
    @SuppressLint("SetTextI18n")
    private fun showDistanceLocation(
        response: ResponseState<LocationResponse>,
        data: LocationResponse
    ) {
        deviceCoordinate = Location("deviceLocation").apply {
            latitude = deviceLocationLatitude as Double
            longitude = deviceLocationLongitude as Double
        }
        val locationCoordinate = Location("targetLocation").apply {
            latitude = response.data?.coordinate?.latitude as Double
            longitude = data.coordinate?.longitude as Double
        }
        val distance = BigDecimal(
            deviceCoordinate?.distanceTo(locationCoordinate)?.div(1000)
                ?.toDouble() as Double
        ).setScale(2, RoundingMode.HALF_EVEN).toDouble()

        detailBinding.tvDistanceLocation.text =
            getString(R.string.approximate_distance) + distance.toString() + getString(R.string.km)

        // show marker location data
        detailBinding.mapLocation.getMapAsync { googleMap ->
            // setup ui and camera position of map
            googleMap.run {
                mapType = GoogleMap.MAP_TYPE_NORMAL

                uiSettings.isCompassEnabled = true
                uiSettings.isZoomControlsEnabled = true

                animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(
                                LatLng(
                                    response.data?.coordinate?.latitude as Double,
                                    data.coordinate?.longitude as Double
                                )
                            )
                            .zoom(15f).build()
                    )
                )

                val marker = MarkerOptions().position(
                    LatLng(
                        data.coordinate?.latitude as Double,
                        data.coordinate?.longitude as Double
                    )
                ).title(data.name).snippet(data.address)

                // setup custom marker icon
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_marker))

                // add marker on map
                addMarker(marker).tag = data.uid

                // enable marker click
                setOnMarkerClickListener { markerOnClick ->
                    markerOnClick.showInfoWindow()
                    false
                }

                // enable info window click
                setOnInfoWindowClickListener { infoWindow ->
                    infoWindow.tag
                    infoWindow.title
                }
            }
        }
    }

    // handle success state of view
    private fun showSuccessStateView() {
        detailBinding.animLoading.gone()
        detailBinding.animError.gone()
        detailBinding.detailLayout.visible()
    }

    // handle error state of view
    private fun showErrorStateView() {
        detailBinding.animError.visible()
        detailBinding.animLoading.gone()
        detailBinding.detailLayout.invisible()
    }

    // handle loading state of view
    private fun showLoadingStateView() {
        detailBinding.detailLayout.invisible()
        detailBinding.animLoading.visible()
        detailBinding.animError.gone()
    }

    // initialize option menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.detail_menu, menu)
        return true
    }

    // give a action for menu item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.share -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content_base) + gPlaceUrl)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)

                startActivity(shareIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // activate back button arrow
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // check ACCESS FINE LOCATION and ACCESS COARSE LOCATION permission
    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
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
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            Constants.PERMISSION_REQUEST_LOCATION
        )
    }

    // permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.PERMISSION_REQUEST_LOCATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        detailBinding.mapLocation.onLowMemory()
    }

    override fun onResume() {
        super.onResume()
        detailBinding.mapLocation.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        detailBinding.mapLocation.onDestroy()
    }
}