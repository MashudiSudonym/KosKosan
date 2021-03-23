package c.m.koskosan.util

/**
 * Class for global constants variables
 * I made this class because i'am to lazy for writing constants variables in specific class.
 * Description of each constant variable :
 * [CAMERA_REQUEST_CODE] - for intent camera access code and catch result onActivityResult.
 * [PICK_PHOTO_CODE] - for intent storage access code and catch result onActivityResult.
 * [UID] - for passing to activity need UID data. [UID] mean user/unique ID.
 * [REQUEST_SIGN_IN_CODE] - for FirebaseUI to access code and catch result onActivityResult.
 * [PERMISSION_REQUEST_READ_EXTERNAL_STORAGE] - for permission access code of external storage.
 * [PERMISSION_REQUEST_CAMERA] - for permission access code of camera.
 * [PERMISSION_REQUEST_LOCATION] - for permission access code of location.
 * [IMAGE_UPLOAD_WORK] - for image upload worker
 * [KEY_IMAGE_URI] - for image upload uri to path worker
 * [KEY_UPLOADED_URI] - for image upload uri to path worker
 * [KEY_NAME] - for name to path worker
 * [KEY_PHONE] - for phone to path worker
 * [KEY_ADDRESS] - for address to path worker
 * [KEY_EMAIL] - for email to path worker
 */

class Constants {
    companion object {
        const val CAMERA_REQUEST_CODE = 111
        const val PICK_PHOTO_CODE = 69
        const val UID = "uid"
        const val REQUEST_SIGN_IN_CODE = 101
        const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 20
        const val PERMISSION_REQUEST_CAMERA = 10
        const val PERMISSION_REQUEST_LOCATION = 190
        const val IMAGE_UPLOAD_WORK = "IMAGE UPLOAD WORK"
        const val KEY_IMAGE_URI: String = "key-image-uri"
        const val KEY_UPLOADED_URI: String = "key-uploaded-uri"
        const val KEY_NAME: String = "key-name"
        const val KEY_PHONE: String = "key-phone"
        const val KEY_ADDRESS: String = "key-address"
        const val KEY_EMAIL: String = "key-email"
    }
}