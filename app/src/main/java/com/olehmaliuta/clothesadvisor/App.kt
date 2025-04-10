package com.olehmaliuta.clothesadvisor

import android.app.Application
import com.olehmaliuta.clothesadvisor.database.AppDb

class App : Application() {
    val database by lazy { AppDb.getDatabase(this) }
}