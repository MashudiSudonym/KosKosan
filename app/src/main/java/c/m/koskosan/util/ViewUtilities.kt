package c.m.koskosan.util

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import c.m.koskosan.R
import coil.load
import com.google.android.material.snackbar.Snackbar

/**
 * Utility file for helping redundant code of View widget feature
 */

object ViewUtilities {
    fun View.visible() {
        visibility = View.VISIBLE
    }

    fun View.invisible() {
        visibility = View.INVISIBLE
    }

    fun View.gone() {
        visibility = View.GONE
    }

    fun View.snackBarBasicShort(title: String) {
        Snackbar.make(this, title, Snackbar.LENGTH_SHORT).show()
    }

    fun View.snackBarWarningLong(title: String) {
        Snackbar.make(this, title, Snackbar.LENGTH_LONG)
            .setTextColor(Color.WHITE)
            .setBackgroundTint(Color.RED)
            .show()
    }

    fun View.snackBarBasicIndefiniteAction(
        title: String,
        actionTitle: String,
        action: (View) -> Unit
    ) {
        Snackbar.make(this, title, Snackbar.LENGTH_INDEFINITE).setAction(actionTitle) {
            action(it)
        }.show()
    }

    fun View.snackBarWarningIndefiniteAction(
        title: String,
        actionTitle: String,
        action: (View) -> Unit
    ) {
        Snackbar.make(this, title, Snackbar.LENGTH_INDEFINITE).setAction(actionTitle) {
            action(it)
        }
            .setTextColor(Color.WHITE)
            .setBackgroundTint(Color.RED)
            .setActionTextColor(Color.WHITE)
            .show()
    }

    fun View.snackBarBasicIndefinite(
        title: String
    ) {
        Snackbar.make(this, title, Snackbar.LENGTH_INDEFINITE).show()
    }

    fun sendMessageWhatsApp(packageContext: Context, uriWhatsApp: String) {
        val whatsAppIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(uriWhatsApp)
        }

        packageContext.startActivity(whatsAppIntent)
    }

    fun loadImageWithCoil(imageView: ImageView, imageUrl: String) {
        imageView.load(imageUrl) {
            placeholder(R.drawable.ic_icon)
            error(R.drawable.ic_broken_image)
        }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}