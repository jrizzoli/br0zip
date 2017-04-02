package com.lineageinc.br0zip

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        fragmentManager.beginTransaction().replace(R.id.content_frame, AboutPrefs()).commit()
    }

    class AboutPrefs : PreferenceFragment() {

        override fun onCreate(savedInstance: Bundle?) {
            super.onCreate(savedInstance)
            addPreferencesFromResource(R.xml.about)

            val team = findPreference("team") as PreferenceCategory
            val teamArray = resources.getStringArray(R.array.about_team)
            for (item in teamArray) {
                val pref = Preference(context)
                pref.summary = item
                team.addPreference(pref)
            }
            val tips = findPreference("tips") as PreferenceCategory
            val tipsArray = resources.getStringArray(R.array.about_tips)
            for (item in tipsArray) {
                val pref = Preference(context)
                pref.summary = item
                tips.addPreference(pref)
            }


        }
    }
}
