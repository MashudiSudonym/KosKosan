package c.m.koskosan.ui.form.update.user.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import c.m.koskosan.R
import c.m.koskosan.data.model.UserResponse
import c.m.koskosan.databinding.ActivityUpdateUserProfileBinding
import c.m.koskosan.databinding.BottomSheetOptionImageBinding
import c.m.koskosan.ui.main.MainActivity
import c.m.koskosan.util.*
import c.m.koskosan.vo.ResponseState
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.rizmaulana.sheenvalidator.lib.SheenValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UpdateUserProfileActivity : AppCompatActivity() {
    private val updateUserProfileViewModel: UpdateUserProfileViewModel by viewModel()
    private lateinit var updateUserProfileBinding: ActivityUpdateUserProfileBinding
    private lateinit var layout: View
    private lateinit var bottomSheet: View
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    private var sheetDialog: BottomSheetDialog? = null
    private var photoPathURI: Uri? = null
    private lateinit var currentPhotoPath: String
    private lateinit var sheenValidator: SheenValidator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        updateUserProfileBinding = ActivityUpdateUserProfileBinding.inflate(layoutInflater)
        val view = updateUserProfileBinding.root
        setContentView(view)

        // initialize layout variable for help using the layout utilities
        layout = view

        // AppBar / ActionBar Title Setup
        setSupportActionBar(updateUserProfileBinding.toolbarUpdateUserProfile)
        supportActionBar?.apply {
            title = getString(R.string.edit_profile)
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        // initialize user profile data to form view
        initializeFieldValueOfUserProfileData()

        // Initialize bottom sheet
        bottomSheetInitialize()

        // Form Validator
        formValidation()
    }

    // Validate form on this activity
    private fun formValidation() {
        sheenValidator = SheenValidator(this).also { sheenValidator ->
            sheenValidator.registerAsRequired(updateUserProfileBinding.edtName)
            sheenValidator.registerAsRequired(updateUserProfileBinding.edtAddress)
            sheenValidator.registerAsRequired(updateUserProfileBinding.edtEmail)
            sheenValidator.registerAsEmail(updateUserProfileBinding.edtEmail)

            sheenValidator.setOnValidatorListener {
                putUserProfileData()
            }
        }

        updateUserProfileBinding.btnSave.setOnClickListener {
            sheenValidator.validate()
        }
    }

    // Posting update of user profile data to database
    private fun putUserProfileData() {
        updateUserProfileViewModel.putUserProfileData(
            updateUserProfileBinding.edtName.text.toString(),
            photoPathURI,
            updateUserProfileBinding.edtPhone.text.toString(),
            updateUserProfileBinding.edtAddress.text.toString(),
            updateUserProfileBinding.edtEmail.text.toString()
        ).observe(this, { response ->
            if (response != null) when (response) {
                is ResponseState.Error -> {
                    response.message?.let {
                        hideSendingAnimation()
                        layout.snackBarWarningLong(it)
                    }
                }
                is ResponseState.Loading -> {
                    response.data?.let {
                        showSendingAnimation()
                        layout.snackBarBasicIndefinite(
                            "Uploading data : ${it}%"
                        )
                    }
                }
                is ResponseState.Success -> {
                    response.data?.let {
                        val intentMainActivity = Intent(this, MainActivity::class.java)

                        hideSendingAnimation()

                        // Open Main Activity
                        finish()
                        startActivity(intentMainActivity)
                    }
                }
            }
        })
    }

    // show sending animation state
    private fun showSendingAnimation() {
        updateUserProfileBinding.formLayout.invisible()
        updateUserProfileBinding.animSending.visible()
    }

    // hide sending animation state
    private fun hideSendingAnimation() {
        updateUserProfileBinding.formLayout.visible()
        updateUserProfileBinding.animSending.gone()
    }

    // Initialize setup for bottom sheet navigation
    private fun bottomSheetInitialize() {
        bottomSheet = updateUserProfileBinding.bottomSheet
        sheetBehavior = BottomSheetBehavior.from(bottomSheet)

        // Choose image button for trigger bottom navigation show up
        updateUserProfileBinding.btnChooseImage.setOnClickListener {
            imageProfileSourceBottomSheet()
        }
    }

    // Bottom sheet for option image profile source
    private fun imageProfileSourceBottomSheet() {
        val bottomSheetOptionImageBinding = BottomSheetOptionImageBinding.inflate(layoutInflater)
        val viewBottomSheet = bottomSheetOptionImageBinding.root

        sheetDialog = BottomSheetDialog(this).apply {
            setContentView(viewBottomSheet)
            show()
            setOnDismissListener { sheetDialog = null }
        }

        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetOptionImageBinding.btnCamera.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                showCamera()
            }

            sheetDialog?.dismiss()
        }

        bottomSheetOptionImageBinding.btnGallery.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                showGallery()
            }

            sheetDialog?.dismiss()
        }
    }

    // process get data from database
    private fun initializeFieldValueOfUserProfileData() {
        updateUserProfileViewModel.getUserProfileData().observe(this, { response ->
            if (response != null) when (response) {
                is ResponseState.Error -> showErrorStateView() // show error state view
                is ResponseState.Loading -> showLoadingStateView() // show loading state view
                is ResponseState.Success -> {
                    // show success state view
                    showSuccessStateView()

                    // initialize data to form view
                    initializeDataToFormFieldView(response)
                }
            }
        })
    }

    // add result of data from database to form field view
    private fun initializeDataToFormFieldView(response: ResponseState<UserResponse>) {
        Glide.with(updateUserProfileBinding.imgProfile)
            .load(response.data?.imageProfile)
            .placeholder(R.drawable.ic_baseline_person_pin)
            .error(R.drawable.ic_broken_image).into(updateUserProfileBinding.imgProfile)
        updateUserProfileBinding.edtName.setText(response.data?.name)
        updateUserProfileBinding.edtPhone.setText(response.data?.phoneNumber)
        updateUserProfileBinding.edtAddress.setText(response.data?.address)
        updateUserProfileBinding.edtEmail.setText(response.data?.email)
    }

    // handle success state of view
    private fun showSuccessStateView() {
        updateUserProfileBinding.animLoading.gone()
        updateUserProfileBinding.animError.gone()
        updateUserProfileBinding.formLayout.visible()
    }

    // handle error state of view
    private fun showErrorStateView() {
        updateUserProfileBinding.animError.visible()
        updateUserProfileBinding.animLoading.gone()
        updateUserProfileBinding.formLayout.invisible()
    }

    // handle loading state of view
    private fun showLoadingStateView() {
        updateUserProfileBinding.formLayout.invisible()
        updateUserProfileBinding.animLoading.visible()
        updateUserProfileBinding.animError.gone()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Permission request camera
        if (requestCode == Constants.PERMISSION_REQUEST_CAMERA) {
            if (
                grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                layout.snackBarBasicShort(
                    getString(R.string.alert_camera_permission_granted)
                )
                startCamera()
            } else {
                layout.snackBarBasicShort(
                    getString(R.string.alert_camera_permission_denied)
                )
            }
        }
        // Permission Request Storage
        if (requestCode == Constants.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
            if (
                grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                layout.snackBarBasicShort(
                    getString(R.string.alert_storage_permission_granted)
                )
                // Open Gallery
            } else {
                layout.snackBarBasicShort(
                    getString(R.string.alert_storage_permission_denied)
                )
            }
        }
    }

    // Show Camera Application
    private fun showCamera() {
        if (checkSelfPermissionCompat(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            layout.snackBarBasicShort(
                getString(R.string.alert_camera_permission_availabel)
            )
            startCamera()
        } else {
            requestCameraPermission()
        }
    }

    // Request Camera Permission
    // If self check permission is denied
    private fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.CAMERA)) {
            layout.snackBarBasicIndefiniteAction(

                getString(R.string.alert_camera_access_required),
                getString(R.string.ok)
            ) {
                requestPermissionsCompat(
                    arrayOf(Manifest.permission.CAMERA),
                    Constants.PERMISSION_REQUEST_CAMERA
                )
            }
        } else {
            layout.snackBarBasicShort(
                getString(R.string.alert_camera_permission_not_available)
            )

            requestPermissionsCompat(
                arrayOf(Manifest.permission.CAMERA),
                Constants.PERMISSION_REQUEST_CAMERA
            )
        }
    }

    // Logic for open camera application
    @SuppressLint("QueryPermissionsNeeded")
    private fun startCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File = createImageFile()
                // Continue only if the File was successfully created
                photoFile.also {
                    photoPathURI = FileProvider.getUriForFile(
                        this,
                        "$packageName.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoPathURI)
                    startActivityForResult(takePictureIntent, Constants.CAMERA_REQUEST_CODE)
                }
            }
        }
    }

    // Creating photo path file and directory name
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        if (storageDir?.exists() == false) storageDir.mkdirs()

        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    // Open Gallery Image
    private fun showGallery() {
        if (checkSelfPermissionCompat(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startGallery()
        } else {
            requestReadStoragePermission()
        }
    }

    // Request Read Storage Permission
    private fun requestReadStoragePermission() {
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            layout.snackBarBasicIndefiniteAction(
                getString(R.string.alert_read_external_storage_required),
                getString(R.string.ok)
            ) {
                requestPermissionsCompat(
                    arrayOf(Manifest.permission.CAMERA),
                    Constants.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
                )
            }
        } else {
            layout.snackBarBasicShort(
                getString(R.string.alert_storage_permission_not_available)
            )

            requestPermissionsCompat(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
            )
        }
    }

    // Logic for Open Gallery
    @SuppressLint("QueryPermissionsNeeded")
    private fun startGallery() {
        Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).also { pickPictureIntent ->
            pickPictureIntent.type = "image/*"
            pickPictureIntent.resolveActivity(packageManager).also {
                startActivityForResult(pickPictureIntent, Constants.PICK_PHOTO_CODE)
            }
        }
    }

    // Use onActivityResult for catch the image capture from camera or gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.CAMERA_REQUEST_CODE -> {
                // Get the dimensions of the View
                val targetWidth = 200
                val targetHeight = 200

                val bmOptions = BitmapFactory.Options().apply {
                    // Get the dimensions of the bitmap
                    inJustDecodeBounds = true

                    val photoWidth: Int = outWidth
                    val photoHeight: Int = outHeight

                    // Determine how much to scale down the image
                    val scaleFactor: Int =
                        (photoWidth / targetWidth).coerceAtMost(photoHeight / targetHeight)

                    // Decode the image file into a Bitmap sized to fill the View
                    inJustDecodeBounds = false
                    inSampleSize = scaleFactor
                }
                BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
                    updateUserProfileBinding.imgProfile.setImageBitmap(bitmap)
                }
            }
            Constants.PICK_PHOTO_CODE -> {
                if (data != null) {
                    photoPathURI = data.data

                    val bitmap =
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            val photoSource =
                                photoPathURI?.let {
                                    ImageDecoder.createSource(
                                        this.contentResolver,
                                        it
                                    )
                                }
                            photoSource?.let { ImageDecoder.decodeBitmap(it) }
                        } else {
                            MediaStore.Images.Media.getBitmap(this.contentResolver, photoPathURI)
                        }

                    updateUserProfileBinding.imgProfile.setImageBitmap(bitmap)
                }
            }
        }
    }

    // handling back button for give a warning before back to last activity
    override fun onBackPressed() {
        var shouldAllowBack: Boolean = false

        if (shouldAllowBack) {
            super.onBackPressed()
        } else {
            layout.snackBarWarningIndefiniteAction(
                getString(R.string.alert_cancel_edit_profile),
                getString(R.string.ok)
            ) {
                super.onBackPressed()
            }
            return
        }

    }

    // function for app bar back arrow
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}