package com.example.myfi

import android.app.Application
import com.example.myfi.data.memory.MemoryDataRepository
import com.example.myfi.domain.repository.DataRepository

class MyFiApplication : Application() {
    val repository: DataRepository by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        MemoryDataRepository()
    }
}
