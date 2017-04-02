package com.lineageinc.br0zip

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.EditTextPreference
import android.preference.ListPreference
import android.preference.PreferenceFragment
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

class BuilderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_builder)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val prefFragment = BuilderPrefs()

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            if (prefFragment.getAndroidVers().isNullOrEmpty()) {
                Snackbar.make(view, getString(R.string.builder_error),
                        Snackbar.LENGTH_LONG).show()
            } else {
                view.animate().scaleX(0f).scaleY(0f).start()
                fabClicked()
            }
        }

        fragmentManager.beginTransaction().replace(R.id.content_frame, prefFragment).commit()
    }

    fun fabClicked() {
        val dialog = ProgressDialog.show(this, getString(R.string.activity_builder),
                getString(R.string.builder_building), true, false)
        Handler().postDelayed({ ->
            dialog.cancel()
            AlertDialog.Builder(this)
                    .setTitle(R.string.builder_bug_title)
                    .setMessage(R.string.builder_bug_message)
                    .setCancelable(false)
                    .setOnDismissListener({ dialog ->
                        val dialogPt2 = ProgressDialog.show(this,
                                getString(R.string.activity_builder),
                                getString(R.string.builder_building_pt2), true, false)

                        Handler().postDelayed(({ ->
                            dialogPt2.dismiss()
                            AlertDialog.Builder(this)
                                    .setTitle(R.string.activity_builder)
                                    .setMessage(R.string.builder_done)
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.builder_ok), {
                                        dialog, which -> finish()
                                    })
                                    .setNegativeButton(getString(R.string.builder_share), {
                                        dialog, which ->
                                        val intent = Intent()
                                        intent.action = Intent.ACTION_SEND
                                        intent.type = "text/plain"
                                        intent.putExtra(Intent.EXTRA_TEXT,
                                                getString(R.string.share_message))
                                        startActivity(intent)
                                        finish()
                                    })
                                    .show()
                        }), 4000)
                    })
                    .setPositiveButton(getString(R.string.builder_bug_ok), {
                        dialog, which -> dialog.dismiss()
                    })
                    .setNegativeButton(getString(R.string.builder_bug_ok), {
                        dialog, which -> dialog.dismiss()
                    })
                    .show()
        }, 3000)
    }

    class BuilderPrefs : PreferenceFragment() {

        override fun onCreate(savedInstance: Bundle?) {
            super.onCreate(savedInstance)
            addPreferencesFromResource(R.xml.builder)

            val phoneName: EditTextPreference = findPreference("phone_model") as EditTextPreference
            phoneName.summary = Build.PRODUCT
            phoneName.setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = (newValue as String)
                true
            }
        }

        fun getAndroidVers(): String? {
            return (findPreference("android_version") as ListPreference).value
        }
    }
}
