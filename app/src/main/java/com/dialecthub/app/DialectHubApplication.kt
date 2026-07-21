package com.dialecthub.app

import android.app.Application
import com.dialecthub.app.data.ProgressRepository

class DialectHubApplication : Application() {

    lateinit var progressRepository: ProgressRepository
        private set

    override fun onCreate() {
        super.onCreate()
        progressRepository = ProgressRepository(this)
    }
}
