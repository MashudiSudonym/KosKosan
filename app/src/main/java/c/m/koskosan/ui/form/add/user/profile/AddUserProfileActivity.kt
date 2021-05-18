package c.m.koskosan.ui.form.add.user.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import c.m.koskosan.R
import c.m.koskosan.databinding.ActivityAddUserProfileBinding
import c.m.koskosan.databinding.BottomSheetOptionImageBinding
import c.m.koskosan.ui.main.MainActivity
import c.m.koskosan.util.Constants.PERMISSION_REQUEST_CAMERA
import c.m.koskosan.util.Constants.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
import c.m.koskosan.util.ViewUtilities.gone
import c.m.koskosan.util.ViewUtilities.invisible
import c.m.koskosan.util.ViewUtilities.snackBarBasicIndefinite
import c.m.koskosan.util.ViewUtilities.snackBarBasicIndefiniteAction
import c.m.koskosan.util.ViewUtilities.snackBarBasicShort
import c.m.koskosan.util.ViewUtilities.snackBarWarningLong
import c.m.koskosan.util.ViewUtilities.visible
import c.m.koskosan.util.checkSelfPermissionCompat
import c.m.koskosan.util.requestPermissionsCompat
import c.m.koskosan.util.shouldShowRequestPermissionRationaleCompat
import c.m.koskosan.vo.ResponseState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import id.rizmaulana.sheenvalidator.lib.SheenValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddUserProfileActivity : AppCompatActivity(),
    ActivityCompat.OnRequestPermissionsResultCallback {
    private lateinit var addUserProfileBinding: ActivityAddUserProfileBinding
    private val addUserProfileViewModel: AddUserProfileViewModel by viewModel()
    private lateinit var bottomSheet: View
    private lateinit var sheetBehavior: BottomSheetBehavior<View>
    private var sheetDialog: BottomSheetDialog? = null
    private var photoPathURI: Uri? = null
    private var currentPhotoPath: String? = null
    private lateinit var sheenValidator: SheenValidator
    private lateinit var layout: View
    private var takePictureCameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
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
                    addUserProfileBinding.animCamera.setImageBitmap(bitmap)
                }
            } else {
                layout.snackBarWarningLong(getString(R.string.data_error_null))
            }
        }

    @Suppress("DEPRECATION")
    private var takePictureGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    photoPathURI = result.data?.data

                    val bitmap =
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            val photoSource =
                                photoPathURI?.let { uri ->
                                    ImageDecoder.createSource(
                                        this.contentResolver,
                                        uri
                                    )
                                }
                            photoSource?.let { ImageDecoder.decodeBitmap(it) }
                        } else {
                            MediaStore.Images.Media.getBitmap(this.contentResolver, photoPathURI)
                        }

                    addUserProfileBinding.animCamera.setImageBitmap(bitmap)
                }
            } else {
                layout.snackBarWarningLong(getString(R.string.data_error_null))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding Initialize
        addUserProfileBinding = ActivityAddUserProfileBinding.inflate(layoutInflater)
        val view = addUserProfileBinding.root
        setContentView(view)

        // initialize for using widget utilities
        layout = view

        // AppBar / ActionBar Title Setup
        setSupportActionBar(addUserProfileBinding.toolbarAddUserProfile)
        supportActionBar?.apply { title = getString(R.string.complete_your_profile) }

        // Get user phone number
        addUserProfileViewModel.getUserPhoneNumber().observe(this, {
            addUserProfileBinding.edtPhone.setText(it)
        })

        // Bottom Sheet Initialize
        bottomSheetInitialize()

        // Do Validator Form
        formValidation()
    }

    // Initialize setup of bottom sheet navigation
    private fun bottomSheetInitialize() {
        bottomSheet = addUserProfileBinding.bottomSheet
        sheetBehavior = BottomSheetBehavior.from(bottomSheet)

        // Choose Image Button for trigger bottom navigation show up
        addUserProfileBinding.btnChooseImage.setOnClickListener {
            imageProfileSourceBottomSheet()
        }
    }

    // Validate form on this activity
    private fun formValidation() {
        sheenValidator = SheenValidator(this).also { sheenValidator ->
            sheenValidator.registerAsRequired(addUserProfileBinding.edtName)
            sheenValidator.registerAsRequired(addUserProfileBinding.edtAddress)
            sheenValidator.registerAsRequired(addUserProfileBinding.edtEmail)
            sheenValidator.registerAsEmail(addUserProfileBinding.edtEmail)

            sheenValidator.setOnValidatorListener {
                postUserProfileData()
            }
        }

        addUserProfileBinding.btnSave.setOnClickListener {
            if (photoPathURI == null) {
                layout.snackBarWarningLong(getString(R.string.alert_photo_null))
            } else {
                sheenValidator.validate()
            }
        }
    }

    // Posting user profile data to database
    private fun postUserProfileData() {
        addUserProfileViewModel.setUserProfileDataInput(
            addUserProfileBinding.edtName.text.toString(),
            photoPathURI as Uri,
            addUserProfileBinding.edtPhone.text.toString(),
            addUserProfileBinding.edtAddress.text.toString(),
            addUserProfileBinding.edtEmail.text.toString(),
        )
        addUserProfileViewModel.postUserProfileData().observe(this, { response ->
            if (response != null) when (response) {
                is ResponseState.Error -> {
                    response.message?.let {
                        hideSendingAnimation()
                        layout.snackBarWarningLong(getString(R.string.error_upload_message) + it)
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
                is ResponseState.Loading -> {
                    response.data?.let {
                        showSendingAnimation()
                        layout.snackBarBasicIndefinite(
                            "Uploading data : ${it}%"
                        )
                    }
                }
            }
        })
    }

    // show sending animation
    private fun showSendingAnimation() {
        addUserProfileBinding.formLayout.invisible()
        addUserProfileBinding.animSending.visible()
    }

    // hide sending animation
    private fun hideSendingAnimation() {
        addUserProfileBinding.formLayout.visible()
        addUserProfileBinding.animSending.gone()
    }

    // Permission Status Catch
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Permission Request Camera
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
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
        if (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) {
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
                getString(R.string.alert_camera_permission_available)
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
                    PERMISSION_REQUEST_CAMERA
                )
            }
        } else {
            layout.snackBarBasicShort(
                getString(R.string.alert_camera_permission_not_available)
            )

            requestPermissionsCompat(arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }
    }

    // Logic for open camera application
    @SuppressLint("QueryPermissionsNeeded")
    private fun startCamera() {
        try {
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
                        takePictureCameraLauncher.launch(takePictureIntent)
                    }
                }
            }
        } catch (error: IOException) {
            Timber.e(error)
            layout.snackBarWarningLong(getString(R.string.failed_load_photo_camera))
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
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
                )
            }
        } else {
            layout.snackBarBasicShort(
                getString(R.string.alert_storage_permission_not_available)
            )

            requestPermissionsCompat(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
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
                takePictureGalleryLauncher.launch(pickPictureIntent)
            }
        }
    }

    // Bottom Sheet for option image profile source
    private fun imageProfileSourceBottomSheet() {
        val bottomSheetOptionImageBinding = BottomSheetOptionImageBinding.inflate(layoutInflater)
        val viewBottomSheet = bottomSheetOptionImageBinding.root

        sheetDialog = BottomSheetDialog(this)
        sheetDialog?.setContentView(viewBottomSheet)
        sheetDialog?.show()
        sheetDialog?.setOnDismissListener { sheetDialog = null }

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
            GlobalScope.launch(
                Dispatchers.IO
            ) {
                showGallery()
            }

            sheetDialog?.dismiss()
        }
    }
}