package com.dirzaaulia.countries.platform

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

var platformApplicationContext: Context? = null

actual fun platformHttpClientEngine(): HttpClientEngine =
    OkHttp.create {
        config {
            platformApplicationContext?.let { ctx ->
                addInterceptor(ChuckerInterceptor.Builder(ctx).build())
            }
        }
    }
