package com.dirzaaulia.countries.platform

import android.content.Context
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

var platformApplicationContext: Context? = null

actual fun platformHttpClientEngine(): HttpClientEngine =
    OkHttp.create {
        config {
            // Base Ktor OkHttp Engine
        }
    }
