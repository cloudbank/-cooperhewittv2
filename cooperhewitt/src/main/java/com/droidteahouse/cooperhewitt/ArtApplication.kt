package com.droidteahouse.cooperhewitt

import android.util.Log
import com.droidteahouse.cooperhewitt.di.DaggerAppComponent

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication

class ArtApplication : DaggerApplication() {

  override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
    return DaggerAppComponent.builder().application(this).build()
  }

  override fun onCreate() {
    super.onCreate()
    Log.d("APP", "APP")
    //get all urls and start service that goes to finish
  }
//start service here that loads hashes into map
  //

}
