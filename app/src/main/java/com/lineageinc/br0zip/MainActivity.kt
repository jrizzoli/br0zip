package com.lineageinc.br0zip

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        var path: File = Environment.getExternalStorageDirectory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        load()
        showDialogIfNeeded()
    }

    override fun onBackPressed() {
        if (Environment.getExternalStorageDirectory() == path) {
            super.onBackPressed()
        } else {
            navigateTo("..")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder(this)
                    .setTitle(R.string.perms_dialog_title)
                    .setMessage(R.string.perms_dialog_message)
                    .setPositiveButton(getString(R.string.perms_dialog_again), {
                        dialog, which ->
                        requestPerm()
                    })
                    .setNegativeButton(getString(R.string.dismiss), {
                        dialog, which ->
                        dialog.dismiss()
                    })
                    .show()
        } else if (!hasPerm()) {
            Snackbar.make(findViewById(R.id.coordinator), getString(R.string.perms_message),
                    Snackbar.LENGTH_LONG).show()
        } else {
            load()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) {
            return false
        }

        when(item.itemId) {
            R.id.about -> startActivity(Intent(this, AboutActivity::class.java))
            R.id.archive -> Snackbar.make(findViewById(R.id.coordinator),
                    getString(R.string.archive_error), Snackbar.LENGTH_LONG).show()
        }
        return super.onOptionsItemSelected(item)
    }


    fun load() {
        if (!hasPerm()) {
            requestPerm()
            return
        }

        val list = findViewById(R.id.list) as ListView
        val sdcardFiles = path.listFiles()
        val array = ArrayList<WinZipFile>()
        var i = 0

        if (Environment.getExternalStorageDirectory() != path) {
            array.add(WinZipFile("..", true))
        }

        while (i < sdcardFiles.size) {
            val f = sdcardFiles[i]
            array.add(WinZipFile(f.name, f.isDirectory))
            i += 1
        }

        Collections.sort(array) { o1, o2 -> o1.title.compareTo(o2.title) }

        list.adapter = WinZipAdapter(this, array)
    }

    fun requestPerm() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE), 12)
    }

    fun hasPerm(): Boolean {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun navigateTo(newPath: String) {
        val sb = StringBuilder()
        if (newPath == "..") {
            val folders = path.toString().split("/")
            var i = 0
            while (i < folders.size - 1) {
                sb.append("/").append(folders[i])
                i += 1
            }
        } else {
            sb.append(path).append("/").append(newPath)
        }

        path = File(sb.toString())
        load()
    }

    fun winZipperRomPower() {
        AlertDialog.Builder(this)
                .setTitle(R.string.builder_welcome_title)
                .setMessage(R.string.builder_welcome_message)
                .setPositiveButton(getString(R.string.builder_welcome_ok), {
                    dialog, which -> startActivity(Intent(this,
                        BuilderActivity::class.java))
                })
                .setNegativeButton(getString(R.string.builder_welcome_nope), {
                    dialog, which -> dialog.dismiss()
                })
                .show()
    }

    fun notOpenable() {
        Snackbar.make(findViewById(R.id.coordinator), getString(R.string.snack_unable_open_file),
                Snackbar.LENGTH_LONG).show()
    }

    fun showDialogIfNeeded() {
        val prefs: SharedPreferences = getSharedPreferences("WinZip", Context.MODE_PRIVATE)
        if (prefs.getBoolean("br0", true)) {
            prefs.edit().putBoolean("br0", false).apply()
            AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.welcome_sucky)
                    .setPositiveButton(getString(R.string.builder_welcome_ok), {
                        dialog, which -> dialog.dismiss()
                    })
                    .show()
        } else {
            AlertDialog.Builder(this)
                    .setTitle(R.string.license_title)
                    .setMessage(R.string.license_message)
                    .setPositiveButton(getString(R.string.license_buy), {
                        dialog, which -> dialog.dismiss()
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("http://lineageos.org")
                        startActivity(intent)

                    })
                    .setNegativeButton(getString(R.string.license_fuck), {
                        dialog, which -> dialog.dismiss()
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("http://lineageos.org")
                        startActivity(intent)
                    })
                    .setNeutralButton(getString(R.string.dismiss), {
                        dialog, which -> dialog.dismiss()
                    })
                    .show()
        }
    }
}
