package com.theprime.primegoplayer

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import com.theprime.primegoplayer.utils.Theming


class GoApp : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        GoPreferences.initPrefs(applicationContext)
        AppCompatDelegate.setDefaultNightMode(Theming.getDefaultNightMode(applicationContext))
    }

    override fun newImageLoader() = ImageLoader.Builder(this)
        .diskCachePolicy(CachePolicy.DISABLED)
        .crossfade(true)
        .build()
}
