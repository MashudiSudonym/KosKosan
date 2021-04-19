package c.m.koskosan.ui.about

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import c.m.koskosan.R
import c.m.koskosan.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var aboutBinding: ActivityAboutBinding
    private lateinit var layout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // view binding initialize
        aboutBinding = ActivityAboutBinding.inflate(layoutInflater)
        val view = aboutBinding.root
        setContentView(view)

        // initialize for using widget utilities
        layout = view

        // AppBar / ActionBar Title Setup
        setSupportActionBar(aboutBinding.toolbarAbout)
        supportActionBar?.apply {
            title = getString(R.string.about)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}